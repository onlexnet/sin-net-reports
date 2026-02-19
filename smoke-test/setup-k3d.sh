#!/bin/bash
# ============================================================================
# k3d + Dapr Local Development Setup Script
# ============================================================================
# This script creates a complete Kubernetes development environment with:
# - k3d cluster (lightweight k3s in Docker)
# - Dapr runtime (with dashboard)
# - SQL Server
# - All microservices
#
# USAGE:
#   ./setup-k3d.sh up     # Create cluster and deploy everything
#   ./setup-k3d.sh down   # Destroy cluster
#   ./setup-k3d.sh logs   # View logs
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CLUSTER_NAME="sinnet-local"
NAMESPACE="sinnet"
SQLSERVER_WAIT_TIMEOUT="${SQLSERVER_WAIT_TIMEOUT:-900s}"
DOCKER_BUILD_CACHE_DIR="${DOCKER_BUILD_CACHE_DIR:-}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_prerequisites() {
    log_info "Checking prerequisites..."
    
    if ! command -v k3d &> /dev/null; then
        log_error "k3d not found. Run: ./install-prerequisites.sh"
        exit 1
    fi
    
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl not found. Run: ./install-prerequisites.sh"
        exit 1
    fi
    
    if ! command -v docker &> /dev/null; then
        log_error "docker not found"
        exit 1
    fi
    
    if ! command -v helm &> /dev/null; then
        log_error "helm not found. Run: ./install-prerequisites.sh"
        exit 1
    fi
    
    log_info "All prerequisites satisfied"
}

cluster_up() {
    log_info "Creating k3d cluster '${CLUSTER_NAME}'..."
    
    if k3d cluster list | grep -q "${CLUSTER_NAME}"; then
        log_warn "Cluster '${CLUSTER_NAME}' already exists. Use './setup-k3d.sh down' to destroy it first."
        exit 1
    fi
    
    # Create k3d cluster with port mappings
    # k3d uses k3s and keeps local resource usage low
    k3d cluster create ${CLUSTER_NAME} \
        --port "3000:30000@loadbalancer" \
        --port "11031:30031@loadbalancer" \
        --port "11021:30021@loadbalancer" \
        --port "18080:30080@loadbalancer" \
        --port "1433:31433@loadbalancer" \
        --agents 0 \
        --wait
    
    log_info "Waiting for cluster to be ready..."
    kubectl wait --for=condition=Ready nodes --all --timeout=120s
    
    log_info "Creating namespace '${NAMESPACE}'..."
    kubectl create namespace "${NAMESPACE}" || true
    
    install_dapr
    deploy_database
    build_and_deploy_services
    
    log_info ""
    log_info "=============================================="
    log_info "k3d cluster is ready!"
    log_info "=============================================="
    log_info ""
    log_info "Access points:"
    log_info "  - Dapr Dashboard:  http://localhost:18080"
    log_info "  - WebAPI GraphQL:  http://localhost:11031/graphiql"
    log_info "  - TimeEntries:     http://localhost:11021/actuator/health"
    log_info "  - Frontend:        http://localhost:3000"
    log_info "  - SQL Server:      localhost:1433 (sa/P@ssw0rd123!)"
    log_info ""
    log_info "Useful commands:"
    log_info "  kubectl get pods -n ${NAMESPACE}              # View pods"
    log_info "  kubectl logs -n ${NAMESPACE} <pod-name>       # View logs"
    log_info "  kubectl logs -n ${NAMESPACE} <pod-name> -c daprd  # View Dapr sidecar logs"
    log_info "  ./setup-k3d.sh logs                           # Tail all logs"
    log_info "  ./setup-k3d.sh down                           # Destroy cluster"
    log_info ""
}

install_dapr() {
    log_info "Installing Dapr runtime..."
    
    # Add Dapr Helm repo
    helm repo add dapr https://dapr.github.io/helm-charts/ 2>/dev/null || true
    helm repo update dapr
    
    # Install Dapr with timeout
    log_info "Installing Dapr control plane (this may take 2-3 minutes)..."
    helm upgrade --install dapr dapr/dapr \
        --version=1.12.3 \
        --namespace dapr-system \
        --create-namespace \
        --set global.logAsJson=false \
        --set global.ha.enabled=false \
        --timeout 10m \
        --wait
    
    log_info "Waiting for Dapr to be ready..."
    kubectl wait --for=condition=Ready pods --all -n dapr-system --timeout=180s || {
        log_warn "Dapr pods not ready yet, checking status..."
        kubectl get pods -n dapr-system
        log_error "Dapr installation timed out. Try running: kubectl get pods -n dapr-system"
        exit 1
    }
    
    # Install Dapr Dashboard
    log_info "Installing Dapr Dashboard..."
    helm upgrade --install dapr-dashboard dapr/dapr-dashboard \
        --namespace dapr-system \
        --timeout 5m \
        --wait || log_warn "Dapr Dashboard installation failed, but continuing..."
    
    # Expose dashboard via LoadBalancer
    log_info "Exposing Dapr Dashboard on http://localhost:18080..."
    # Helm creates dapr-dashboard as ClusterIP on port 8080. We replace it
    # with a custom LoadBalancer service on port 30080 for k3d host mapping.
    # Deleting first avoids strategic merge creating a second port entry.
    kubectl delete service dapr-dashboard -n dapr-system --ignore-not-found=true
    kubectl apply -f "${SCRIPT_DIR}/k8s/dapr-dashboard-service.yaml"
    
    log_info "Dapr installed successfully"
}

deploy_database() {
    log_info "Deploying SQL Server..."
    kubectl apply -f "${SCRIPT_DIR}/k8s/sqlserver.yaml" -n "${NAMESPACE}"
    
    log_info "Waiting for SQL Server to be ready (timeout: ${SQLSERVER_WAIT_TIMEOUT})..."
    kubectl wait --for=condition=Ready pod -l app=sqlserver -n "${NAMESPACE}" --timeout="${SQLSERVER_WAIT_TIMEOUT}"
    
    log_info "SQL Server deployed"
}

build_and_deploy_services() {
    log_info "Building and loading Docker images into k3d..."
    
    cd "${SCRIPT_DIR}/.."

    build_image() {
        local image_tag="$1"
        local dockerfile_path="$2"
        shift 2

        if [[ -n "${DOCKER_BUILD_CACHE_DIR}" ]]; then
            mkdir -p "${DOCKER_BUILD_CACHE_DIR}"
            docker buildx build \
                --load \
                --cache-from "type=local,src=${DOCKER_BUILD_CACHE_DIR}" \
                --cache-to "type=local,dest=${DOCKER_BUILD_CACHE_DIR},mode=max" \
                -t "${image_tag}" \
                -f "${dockerfile_path}" \
                "$@" \
                .
        else
            docker build -t "${image_tag}" -f "${dockerfile_path}" "$@" .
        fi
    }
    
    # Build TimeEntries
    log_info "Building uservice-timeentries..."
    build_image sinnet/uservice-timeentries:local uservice-timeentries/Dockerfile
    k3d image import sinnet/uservice-timeentries:local --cluster "${CLUSTER_NAME}"
    
    # Build WebAPI
    log_info "Building uservice-webapi..."
    build_image sinnet/uservice-webapi:local uservice-webapi/Dockerfile
    k3d image import sinnet/uservice-webapi:local --cluster "${CLUSTER_NAME}"
    
    # Build Frontend
    log_info "Building static-webapp..."
    build_image sinnet/static-webapp:local static-webapp/Dockerfile \
        --build-arg BACKEND_BASE_URL=http://localhost:11031 \
        --build-arg USE_TEST_LOGIN=true \
        --build-arg ENVIRONMENT=development
    k3d image import sinnet/static-webapp:local --cluster "${CLUSTER_NAME}"
    
    log_info "Deploying services..."
    kubectl apply -f "${SCRIPT_DIR}/k8s/dapr-config.yaml" -n "${NAMESPACE}"
    kubectl apply -f "${SCRIPT_DIR}/k8s/timeentries.yaml" -n "${NAMESPACE}"
    kubectl apply -f "${SCRIPT_DIR}/k8s/webapi.yaml" -n "${NAMESPACE}"
    kubectl apply -f "${SCRIPT_DIR}/k8s/static-webapp.yaml" -n "${NAMESPACE}"
    
    log_info "Waiting for services to be ready..."
    kubectl wait --for=condition=Ready pod -l app=timeentries -n "${NAMESPACE}" --timeout=300s || log_warn "TimeEntries not ready yet"
    kubectl wait --for=condition=Ready pod -l app=webapi -n "${NAMESPACE}" --timeout=300s || log_warn "WebAPI not ready yet"
    kubectl wait --for=condition=Ready pod -l app=static-webapp -n "${NAMESPACE}" --timeout=300s || log_warn "Frontend not ready yet"
    
    log_info "Services deployed"
}

cluster_down() {
    log_info "Destroying k3d cluster '${CLUSTER_NAME}'..."
    k3d cluster delete "${CLUSTER_NAME}"
    log_info "Cluster destroyed"
}

show_logs() {
    log_info "Tailing logs from all pods in namespace '${NAMESPACE}'..."
    kubectl logs -f -n "${NAMESPACE}" --all-containers=true --max-log-requests=10 -l "app in (timeentries,webapi,static-webapp,sqlserver)" --prefix=true
}

deploy_only() {
    log_info "Deploying services to existing cluster..."
    
    if ! k3d cluster list | grep -q "${CLUSTER_NAME}"; then
        log_error "Cluster '${CLUSTER_NAME}' does not exist. Run './setup-k3d.sh up' first."
        exit 1
    fi
    
    # Check if namespace exists
    kubectl get namespace "${NAMESPACE}" > /dev/null 2>&1 || kubectl create namespace "${NAMESPACE}"
    
    # Check if SQL Server is already deployed
    if ! kubectl get deployment sqlserver -n "${NAMESPACE}" > /dev/null 2>&1; then
        deploy_database
    else
        log_info "SQL Server already deployed"
    fi
    
    build_and_deploy_services
    
    log_info ""
    log_info "Services deployed successfully!"
    log_info "Access points:"
    log_info "  - Dapr Dashboard:  http://localhost:18080"
    log_info "  - WebAPI GraphQL:  http://localhost:11031/graphiql"
    log_info "  - TimeEntries:     http://localhost:11021/actuator/health"
    log_info "  - Frontend:        http://localhost:3000"
    log_info ""
}

# Main execution
case "${1:-}" in
    up)
        check_prerequisites
        cluster_up
        ;;
    down)
        cluster_down
        ;;
    logs)
        show_logs
        ;;
    deploy)
        check_prerequisites
        deploy_only
        ;;
    *)
        log_error "Usage: $0 {up|down|logs|deploy}"
        log_info ""
        log_info "Commands:"
        log_info "  up      - Create cluster and deploy everything"
        log_info "  deploy  - Deploy/redeploy services to existing cluster"
        log_info "  down    - Destroy cluster"
        log_info "  logs    - Tail logs from all services"
        exit 1
        ;;
esac

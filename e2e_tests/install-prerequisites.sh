#!/bin/bash
# ============================================================================
# Install k3d and Helm for local Kubernetes development
# ============================================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# Install kubectl
if ! command -v kubectl &> /dev/null; then
    log_info "Installing kubectl..."
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
    chmod +x kubectl
    sudo mv kubectl /usr/local/bin/kubectl
    log_info "kubectl installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
else
    log_info "kubectl already installed: $(kubectl version --client --short 2>/dev/null || kubectl version --client)"
fi

# Install k3d (lightweight Kubernetes using k3s)
if ! command -v k3d &> /dev/null; then
    log_info "Installing k3d..."
    curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash
    log_info "k3d installed: $(k3d version)"
else
    log_info "k3d already installed: $(k3d version)"
fi

# Install Helm
if ! command -v helm &> /dev/null; then
    log_info "Installing Helm..."
    curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
    log_info "Helm installed: $(helm version --short)"
else
    log_info "Helm already installed: $(helm version --short)"
fi

log_info ""
log_info "All prerequisites installed!"
log_info "Now run: ./setup-k3d.sh up"

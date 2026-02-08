# Makefile for SinNet Reports Smoke Tests
# Provides convenient docker-compose commands

.PHONY: help build up test down clean logs

help: ## Show this help message
	@echo "SinNet Reports - Smoke Test Commands"
	@echo ""
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-15s %s\n", $$1, $$2}'

build: ## Build all Docker images (requires pre-built services)
	@echo "Building Docker images..."
	cd ../uservice-timeentries && mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-timeentries
	cd ../uservice-webapi && mvn spring-boot:build-image -ntp -pl host -DskipTests -Dspring-boot.build-image.imageName=uservice-webapi
	docker-compose build test-runner
	@echo "✓ All images built successfully"

up: ## Start all services (without tests)
	@echo "Starting services..."
	docker-compose up -d
	@echo "✓ Services started. Access webapp at http://localhost:3000"
	@echo "  To view logs: make logs"
	@echo "  To stop: make down"

test: ## Run smoke tests (starts services, runs tests, keeps services running)
	@echo "Starting services and running tests..."
	docker-compose --profile test up --abort-on-container-exit test-runner
	@echo ""
	@echo "Tests completed. Services are still running."
	@echo "  WebApp: http://localhost:3000"
	@echo "  GraphQL: http://localhost:8081/graphql"
	@echo "  To stop: make down"

test-ci: ## Run smoke tests in CI mode (exits after tests complete)
	@echo "Running smoke tests in CI mode..."
	docker-compose --profile test up --abort-on-container-exit --exit-code-from test-runner test-runner
	docker-compose down -v

down: ## Stop all services
	@echo "Stopping services..."
	docker-compose --profile test down
	@echo "✓ Services stopped"

clean: ## Remove all containers, volumes, and networks
	@echo "Cleaning up..."
	docker-compose --profile test down -v --remove-orphans
	@echo "✓ Cleanup complete"

logs: ## Show logs from all services
	docker-compose logs -f

logs-timeentries: ## Show logs from timeentries service
	docker-compose logs -f uservice-timeentries

logs-webapi: ## Show logs from webapi service
	docker-compose logs -f uservice-webapi

logs-webapp: ## Show logs from webapp
	docker-compose logs -f static-webapp

logs-db: ## Show logs from database
	docker-compose logs -f sqlserver

status: ## Show status of all services
	docker-compose ps

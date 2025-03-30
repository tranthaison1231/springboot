.PHONY: build run clean stop restart logs test help dev check-java check-docker

# Default target
.DEFAULT_GOAL := help

# Variables
MAVEN_CMD := ./mvnw
DOCKER_COMPOSE := docker-compose
REQUIRED_JAVA_VERSION := 23

help: ## Show this help message
	@echo 'Usage:'
	@echo '  make [target]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-20s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

check-java: ## Check if Java is installed with correct version
	@echo "📝 Checking Java installation..."
	@export PATH="/opt/homebrew/opt/openjdk/bin:$$PATH"; \
	if ! command -v java &> /dev/null; then \
		echo "❌ Java is not installed. Please install Java $(REQUIRED_JAVA_VERSION) or later."; \
		echo "For macOS, run: brew install openjdk"; \
		echo "Or download from: https://adoptium.net/"; \
		exit 1; \
	fi; \
	if ! java -version 2>&1 | grep -q "version \"$(REQUIRED_JAVA_VERSION)"; then \
		echo "❌ Wrong Java version. Required: $(REQUIRED_JAVA_VERSION)"; \
		echo "Current version:"; \
		java -version; \
		echo "For macOS, run: brew install openjdk"; \
		echo "Or download from: https://adoptium.net/"; \
		exit 1; \
	fi; \
	echo "✅ Java $(REQUIRED_JAVA_VERSION) is installed"

check-docker: ## Check if Docker is running
	@echo "📝 Checking Docker status..."
	@if ! docker info > /dev/null 2>&1; then \
		echo "❌ Docker is not running. Please start Docker Desktop"; \
		exit 1; \
	fi; \
	echo "✅ Docker is running"

dev: check-java check-docker ## Start development environment (build, run, and show logs)
	@echo "🚀 Starting development environment..."
	@echo "📦 Building application..."
	@export PATH="/opt/homebrew/opt/openjdk/bin:$$PATH"; \
	$(MAVEN_CMD) clean package -DskipTests || { echo "❌ Maven build failed"; exit 1; }
	@echo "🐳 Building Docker images..."
	@$(DOCKER_COMPOSE) build || { echo "❌ Docker build failed"; exit 1; }
	@echo "🚀 Starting containers..."
	@$(DOCKER_COMPOSE) up -d || { echo "❌ Failed to start containers"; exit 1; }
	@echo "✅ Development environment is ready!"
	@echo "📝 Showing logs (Ctrl+C to exit logs, containers will keep running)..."
	@$(DOCKER_COMPOSE) logs -f

build: check-java check-docker ## Build the application and Docker images
	@echo "📦 Building application..."
	@export PATH="/opt/homebrew/opt/openjdk/bin:$$PATH"; \
	$(MAVEN_CMD) clean package -DskipTests || { echo "❌ Maven build failed"; exit 1; }
	@echo "🐳 Building Docker images..."
	@$(DOCKER_COMPOSE) build || { echo "❌ Docker build failed"; exit 1; }
	@echo "✅ Build complete!"

run: check-docker ## Run the application with Docker Compose
	@echo "🚀 Starting containers..."
	@$(DOCKER_COMPOSE) up -d
	@echo "✅ Application is running!"

clean: ## Clean up containers, volumes, and build artifacts
	@echo "🧹 Cleaning up..."
	-$(DOCKER_COMPOSE) down -v 2>/dev/null || true
	-$(MAVEN_CMD) clean 2>/dev/null || true
	-rm -rf target/ 2>/dev/null || true
	@echo "✨ Clean up complete"

stop: ## Stop all containers
	@echo "🛑 Stopping containers..."
	-$(DOCKER_COMPOSE) stop 2>/dev/null || true
	@echo "✅ Containers stopped"

restart: stop run ## Restart all containers

logs: ## Show logs from all containers
	$(DOCKER_COMPOSE) logs -f

logs-app: ## Show logs from Spring Boot application
	$(DOCKER_COMPOSE) logs -f app

logs-db: ## Show logs from PostgreSQL
	$(DOCKER_COMPOSE) logs -f db

test: check-java ## Run tests
	@echo "🧪 Running tests..."
	$(MAVEN_CMD) test

db-shell: ## Access PostgreSQL shell
	@echo "🐘 Connecting to PostgreSQL shell..."
	$(DOCKER_COMPOSE) exec db psql -U postgres -d springboot_db

app-shell: ## Access Spring Boot container shell
	@echo "📦 Connecting to application container..."
	$(DOCKER_COMPOSE) exec app /bin/sh 
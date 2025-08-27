#!/bin/bash

# ============================================================================
# Multi-Environment Deployment Script for Ensurance Pharmacy System
# ============================================================================
# Automatically detects git branch and deploys to appropriate environment
# Kills processes using target ports before deployment
# ============================================================================

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Environment port mappings
declare -A ENV_PORTS
ENV_PORTS[dev]="3000 3001 3002 3003"
ENV_PORTS[main]="5175 8089 8081 8082"
ENV_PORTS[qa]="4000 4001 4002 4003"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to detect current git branch
get_git_branch() {
    local branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown")
    
    # Map branch names to environments
    case $branch in
        "main"|"master")
            echo "main"
            ;;
        "develop"|"dev"|"development")
            echo "dev"
            ;;
        "qa"|"test"|"testing"|"staging")
            echo "qa"
            ;;
        *)
            # Default to dev for feature branches
            echo "dev"
            ;;
    esac
}

# Function to kill processes using specific ports
kill_port_processes() {
    local ports="$1"
    local environment="$2"
    
    print_status "Checking for processes using ports for $environment environment..."
    
    for port in $ports; do
        local pid=$(lsof -ti:$port 2>/dev/null || true)
        if [ ! -z "$pid" ]; then
            print_warning "Port $port is being used by process $pid"
            print_status "Killing process $pid using port $port..."
            kill -9 $pid 2>/dev/null || true
            sleep 1
            
            # Verify the process is killed
            if lsof -ti:$port >/dev/null 2>&1; then
                print_error "Failed to kill process using port $port"
                exit 1
            else
                print_success "Successfully killed process using port $port"
            fi
        else
            print_status "Port $port is available"
        fi
    done
}

# Function to create environment directories
create_env_directories() {
    local env="$1"
    print_status "Creating directories for $env environment..."
    
    mkdir -p "databases/$env"
    mkdir -p "logs/$env"
    
    # Copy database files if they don't exist
    if [ ! -f "databases/$env/ensurance/USUARIO.sqlite" ]; then
        mkdir -p "databases/$env/ensurance"
        if [ -f "backv4/sqlite/USUARIO.sqlite" ]; then
            cp "backv4/sqlite/USUARIO.sqlite" "databases/$env/ensurance/"
        fi
    fi
    
    if [ ! -f "databases/$env/pharmacy/USUARIO.sqlite" ]; then
        mkdir -p "databases/$env/pharmacy"
        if [ -f "backv5/sqlite/USUARIO.sqlite" ]; then
            cp "backv5/sqlite/USUARIO.sqlite" "databases/$env/pharmacy/"
        fi
    fi
}

# Function to stop existing containers
stop_containers() {
    local env="$1"
    print_status "Stopping existing containers for $env environment..."
    
    docker-compose -f "docker-compose.$env.yml" down 2>/dev/null || true
    
    # Force remove container if it exists
    local container_name="ensurance-pharmacy-$env"
    if docker ps -a --format "table {{.Names}}" | grep -q "^$container_name$"; then
        print_status "Removing existing container: $container_name"
        docker rm -f "$container_name" 2>/dev/null || true
    fi
}

# Function to display environment URLs
show_urls() {
    local env="$1"
    local ports="${ENV_PORTS[$env]}"
    local port_array=($ports)
    
    print_success "🚀 $env environment deployed successfully!"
    echo ""
    echo "📱 Access URLs:"
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ Ensurance Frontend: http://localhost:${port_array[0]}                │"
    echo "│ Pharmacy Frontend:  http://localhost:${port_array[1]}                │"
    echo "│ Ensurance API:      http://localhost:${port_array[2]}/api           │"
    echo "│ Pharmacy API:       http://localhost:${port_array[3]}/api2          │"
    echo "└─────────────────────────────────────────────────────────────┘"
    echo ""
}

# Function to show logs
show_logs() {
    local env="$1"
    print_status "Showing logs for $env environment (Ctrl+C to exit)..."
    docker-compose -f "docker-compose.$env.yml" logs -f
}

# Main deployment function
deploy() {
    local env="$1"
    local force_rebuild="$2"
    
    print_status "Starting deployment for $env environment..."
    
    # Validate environment
    if [[ ! " dev main qa " =~ " $env " ]]; then
        print_error "Invalid environment: $env. Valid options: dev, main, qa"
        exit 1
    fi
    
    # Check if docker-compose file exists
    if [ ! -f "docker-compose.$env.yml" ]; then
        print_error "docker-compose.$env.yml not found!"
        exit 1
    fi
    
    # Kill processes using target ports
    kill_port_processes "${ENV_PORTS[$env]}" "$env"
    
    # Stop existing containers
    stop_containers "$env"
    
    # Create environment directories
    create_env_directories "$env"
    
    # Build and start containers
    print_status "Building and starting containers for $env environment..."
    
    if [ "$force_rebuild" = "true" ]; then
        docker-compose -f "docker-compose.$env.yml" build --no-cache
    else
        docker-compose -f "docker-compose.$env.yml" build
    fi
    
    docker-compose -f "docker-compose.$env.yml" up -d
    
    # Wait for services to be ready
    print_status "Waiting for services to start..."
    sleep 10
    
    # Show URLs
    show_urls "$env"
}

# Help function
show_help() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  deploy [ENV]     Deploy to specific environment (dev, main, qa)"
    echo "  auto             Auto-detect git branch and deploy"
    echo "  stop [ENV]       Stop environment"
    echo "  logs [ENV]       Show logs for environment"
    echo "  status           Show status of all environments"
    echo "  clean            Clean up all containers and images"
    echo ""
    echo "Options:"
    echo "  --rebuild        Force rebuild of Docker images"
    echo "  --help           Show this help message"
    echo ""
    echo "Environment Port Mappings:"
    echo "  dev:  3000-3003 (Frontend: 3000, 3001 | Backend: 3002, 3003)"
    echo "  main: 5175, 8089, 8081, 8082 (Production ports)"
    echo "  qa:   4000-4003 (Frontend: 4000, 4001 | Backend: 4002, 4003)"
}

# Parse command line arguments
COMMAND=""
ENVIRONMENT=""
FORCE_REBUILD="false"

while [[ $# -gt 0 ]]; do
    case $1 in
        deploy|auto|stop|logs|status|clean)
            COMMAND="$1"
            shift
            ;;
        dev|main|qa)
            ENVIRONMENT="$1"
            shift
            ;;
        --rebuild)
            FORCE_REBUILD="true"
            shift
            ;;
        --help|-h)
            show_help
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Execute commands
case $COMMAND in
    "deploy")
        if [ -z "$ENVIRONMENT" ]; then
            print_error "Environment required for deploy command"
            show_help
            exit 1
        fi
        deploy "$ENVIRONMENT" "$FORCE_REBUILD"
        ;;
    "auto")
        DETECTED_ENV=$(get_git_branch)
        print_status "Detected git branch environment: $DETECTED_ENV"
        deploy "$DETECTED_ENV" "$FORCE_REBUILD"
        ;;
    "stop")
        if [ -z "$ENVIRONMENT" ]; then
            print_error "Environment required for stop command"
            exit 1
        fi
        stop_containers "$ENVIRONMENT"
        print_success "Stopped $ENVIRONMENT environment"
        ;;
    "logs")
        if [ -z "$ENVIRONMENT" ]; then
            print_error "Environment required for logs command"
            exit 1
        fi
        show_logs "$ENVIRONMENT"
        ;;
    "status")
        print_status "Container status for all environments:"
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep ensurance-pharmacy || echo "No containers running"
        ;;
    "clean")
        print_warning "This will remove all containers and images. Continue? (y/N)"
        read -r response
        if [[ "$response" =~ ^[Yy]$ ]]; then
            docker-compose -f docker-compose.dev.yml down --rmi all 2>/dev/null || true
            docker-compose -f docker-compose.main.yml down --rmi all 2>/dev/null || true
            docker-compose -f docker-compose.qa.yml down --rmi all 2>/dev/null || true
            print_success "Cleaned up all containers and images"
        fi
        ;;
    "")
        print_error "No command specified"
        show_help
        exit 1
        ;;
    *)
        print_error "Unknown command: $COMMAND"
        show_help
        exit 1
        ;;
esac

#!/bin/bash

# Test Runner Script for Ensurance Pharmacy Project
# Consolidates all test and coverage commands for the 4 systems

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Directorio del script y directorio raíz del proyecto
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Function to print colored output
print_info() {
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

# Function to run Ensurance frontend tests
run_ensurance_tests() {
    print_info "Running Ensurance frontend tests..."
    (cd "$ROOT_DIR/ensurance" && npm run test:run)
    print_success "Ensurance tests completed"
}

# Function to run Ensurance frontend tests with coverage
run_ensurance_coverage() {
    print_info "Running Ensurance frontend tests with coverage..."
    (cd "$ROOT_DIR/ensurance" && npm run test:run -- --coverage)
    print_success "Ensurance coverage completed"
}

# Function to run Pharmacy frontend tests
run_pharmacy_tests() {
    print_info "Running Pharmacy frontend tests..."
    (cd "$ROOT_DIR/pharmacy" && npm run test:unit)
    print_success "Pharmacy tests completed"
}

# Function to run Pharmacy frontend tests with coverage
run_pharmacy_coverage() {
    print_info "Running Pharmacy frontend tests with coverage..."
    (cd "$ROOT_DIR/pharmacy" && npm run test:unit -- --coverage)
    print_success "Pharmacy coverage completed"
}

# Function to run BackV5 backend tests
run_backv5_tests() {
    print_info "Running BackV5 backend tests..."
    mvn -f "$ROOT_DIR/backv5/pom.xml" clean test jacoco:report
    print_success "BackV5 tests completed"
}

# Function to run BackV4 backend tests
run_backv4_tests() {
    print_info "Running BackV4 backend tests..."
    mvn -f "$ROOT_DIR/backv4/pom.xml" clean test jacoco:report
    print_success "BackV4 tests completed"
}

# Function to run SonarQube analysis
run_sonar_analysis() {
    print_info "Running SonarQube analysis..."
    mvn clean test jacoco:report sonar:sonar \
      -Dsonar.host.url=http://localhost:9000 \
      -Dsonar.token=sqp_6ac9246579c658868005ccbf2d6afc073186fd48 \
      -Dsonar.projectKey=ensurance-pharmacy \
      -Dsonar.projectName=ensurance-pharmacy
    print_success "SonarQube analysis completed"
}

# Function to install dependencies
install_dependencies() {
    print_info "Installing dependencies for all systems..."
    
    print_info "Installing Ensurance dependencies..."
    (cd "$ROOT_DIR/ensurance" && npm install)
    
    print_info "Installing Pharmacy dependencies..."
    (cd "$ROOT_DIR/pharmacy" && npm install)
    
    print_success "All dependencies installed"
}

# Function to run all tests
run_all_tests() {
    print_info "Running all tests for all systems..."
    run_ensurance_tests
    run_pharmacy_tests
    run_backv5_tests
    run_backv4_tests
    print_success "All tests completed successfully!"
}

# Function to run all tests with coverage
run_all_coverage() {
    print_info "Running all tests with coverage for all systems..."
    run_ensurance_coverage
    run_pharmacy_coverage
    run_backv5_tests
    run_backv4_tests
    print_success "All coverage reports generated successfully!"
}

# Main menu function
show_menu() {
    echo ""
    echo -e "${BLUE}=== Ensurance Pharmacy Test Runner ===${NC}"
    echo ""
    echo "Frontend Systems:"
    echo "  1) Ensurance - Run tests"
    echo "  2) Ensurance - Run tests with coverage"
    echo "  3) Pharmacy - Run tests"
    echo "  4) Pharmacy - Run tests with coverage"
    echo ""
    echo "Backend Systems:"
    echo "  5) BackV5 - Run tests with coverage"
    echo "  6) BackV4 - Run tests with coverage"
    echo ""
    echo "Combined Options:"
    echo "  7) Run ALL tests (no coverage)"
    echo "  8) Run ALL tests with coverage"
    echo ""
    echo "Utilities:"
    echo "  9) Install all dependencies"
    echo " 10) Run SonarQube analysis"
    echo ""
    echo "  0) Exit"
    echo ""
}

# Main execution loop
main() {
    while true; do
        show_menu
        read -p "Select an option (0-10): " choice
        
        case $choice in
            1)
                run_ensurance_tests
                ;;
            2)
                run_ensurance_coverage
                ;;
            3)
                run_pharmacy_tests
                ;;
            4)
                run_pharmacy_coverage
                ;;
            5)
                run_backv5_tests
                ;;
            6)
                run_backv4_tests
                ;;
            7)
                run_all_tests
                ;;
            8)
                run_all_coverage
                ;;
            9)
                install_dependencies
                ;;
            10)
                run_sonar_analysis
                ;;
            0)
                print_info "Exiting..."
                exit 0
                ;;
            *)
                print_error "Invalid option. Please select 0-10."
                ;;
        esac
        
        echo ""
        read -p "Press Enter to continue..."
    done
}

# Check if script is being run directly
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi

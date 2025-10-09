#!/bin/bash

# view-k6-dashboard.sh - View K6 test results
# Usage: ./view-k6-dashboard.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESULTS_VOLUME="scripts_k6_results"

echo "========================================="
echo "   K6 Dashboard & Results Viewer"
echo "========================================="
echo ""

# Check if volume exists
if ! docker volume ls | grep -q "$RESULTS_VOLUME"; then
    echo "❌ No K6 results found. Run a K6 test first!"
    echo ""
    echo "Run this command to execute a test:"
    echo "  cd $SCRIPT_DIR && ./run-tests.sh"
    exit 1
fi

# Check if results exist in volume
RESULTS_COUNT=$(docker run --rm -v $RESULTS_VOLUME:/results alpine sh -c "ls -1 /results 2>/dev/null | wc -l" || echo "0")

if [ "$RESULTS_COUNT" -eq "0" ]; then
    echo "❌ K6 results volume is empty. Run a K6 test first!"
    exit 1
fi

echo "✅ K6 results found!"
echo ""
echo "📊 Available options:"
echo ""
echo "  1. View exported HTML dashboard (recommended)"
echo "  2. View JSON summary"
echo "  3. View raw results"
echo "  4. Copy results to local directory"
echo "  5. Show all files in results"
echo ""
read -p "Select an option (1-5): " choice

case $choice in
    1)
        echo ""
        echo "🌐 Opening K6 HTML Dashboard..."
        
        # Check if HTML dashboard exists
        HTML_EXISTS=$(docker run --rm -v $RESULTS_VOLUME:/results alpine sh -c "test -f /results/k6-dashboard/index.html && echo 'yes' || echo 'no'")
        
        if [ "$HTML_EXISTS" = "yes" ]; then
            # Copy dashboard to temporary location
            TEMP_DIR=$(mktemp -d)
            docker run --rm -v $RESULTS_VOLUME:/results -v "$TEMP_DIR:/output" alpine cp -r /results/k6-dashboard /output/
            
            echo "Dashboard extracted to: $TEMP_DIR/k6-dashboard"
            echo ""
            echo "Opening in browser..."
            
            # Open in browser
            if command -v xdg-open &> /dev/null; then
                xdg-open "$TEMP_DIR/k6-dashboard/index.html"
            elif command -v open &> /dev/null; then
                open "$TEMP_DIR/k6-dashboard/index.html"
            else
                echo "Please open manually: file://$TEMP_DIR/k6-dashboard/index.html"
            fi
            
            echo ""
            echo "✅ Dashboard opened! Files are in: $TEMP_DIR/k6-dashboard"
            echo ""
            echo "Note: This is a temporary copy. To keep it, copy to your desired location."
        else
            echo "❌ HTML dashboard not found."
            echo ""
            echo "The dashboard is generated during test execution."
            echo "Make sure K6 is configured with web-dashboard export."
        fi
        ;;
        
    2)
        echo ""
        echo "📄 K6 Summary (JSON):"
        echo "========================================"
        docker run --rm -v $RESULTS_VOLUME:/results alpine cat /results/summary.json 2>/dev/null | python3 -m json.tool || \
        docker run --rm -v $RESULTS_VOLUME:/results alpine cat /results/summary.json 2>/dev/null
        echo ""
        ;;
        
    3)
        echo ""
        echo "📊 K6 Metrics Summary:"
        echo "========================================"
        docker run --rm -v $RESULTS_VOLUME:/results alpine cat /results/k6-results.json 2>/dev/null | \
        python3 -c "import sys, json; data=json.load(sys.stdin); print(json.dumps(data.get('metrics', {}), indent=2))" 2>/dev/null || \
        echo "Use option 4 to copy full results and analyze locally"
        ;;
        
    4)
        echo ""
        OUTPUT_DIR="$SCRIPT_DIR/k6-results-$(date +%Y%m%d-%H%M%S)"
        mkdir -p "$OUTPUT_DIR"
        
        echo "Copying results to: $OUTPUT_DIR"
        docker run --rm -v $RESULTS_VOLUME:/results -v "$OUTPUT_DIR:/output" alpine sh -c "cp -r /results/* /output/"
        
        echo ""
        echo "✅ Results copied successfully!"
        echo ""
        echo "Files available at: $OUTPUT_DIR"
        ls -lh "$OUTPUT_DIR"
        ;;
        
    5)
        echo ""
        echo "📁 K6 Results Files:"
        echo "========================================"
        docker run --rm -v $RESULTS_VOLUME:/results alpine ls -lh /results
        echo ""
        ;;
        
    *)
        echo "Invalid option"
        exit 1
        ;;
esac

echo ""
echo "========================================="
echo "Additional Information:"
echo "========================================="
echo ""
echo "📊 View metrics in Grafana:"
echo "   http://localhost:3300 (if monitoring stack is running)"
echo ""
echo "🔄 Run another test:"
echo "   cd $SCRIPT_DIR && ./run-tests.sh"
echo ""
echo "🧹 Clean old results:"
echo "   cd $SCRIPT_DIR && ./cleanup-results.sh"
echo ""

#!/bin/bash
# Script para limpiar resultados de tests anteriores

set -e

RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${YELLOW}[WARNING]${NC} Este script eliminará TODOS los resultados de tests anteriores"
echo ""
echo "Volúmenes a eliminar:"
echo "  - scripts_k6_results"
echo "  - scripts_jmeter_results"
echo ""
read -p "¿Estás seguro? (escribe 'yes' para confirmar): " confirmation

if [[ "$confirmation" != "yes" ]]; then
    echo -e "${GREEN}[CANCELLED]${NC} Operación cancelada"
    exit 0
fi

echo -e "${YELLOW}[INFO]${NC} Eliminando volúmenes..."

# Eliminar volúmenes
docker volume rm -f scripts_k6_results 2>/dev/null || true
docker volume rm -f scripts_jmeter_results 2>/dev/null || true

echo -e "${GREEN}[SUCCESS]${NC} Volúmenes eliminados exitosamente"
echo ""
echo "Los nuevos tests crearán volúmenes frescos automáticamente"

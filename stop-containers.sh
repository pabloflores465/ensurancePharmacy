#!/bin/bash

# Script para detener TODOS los contenedores
# No elimina volúmenes ni redes
# Uso: ./stop-containers.sh

set -e

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo ""
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}⏸️  DETENER CONTENEDORES${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Verificar que Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: Docker no está corriendo${NC}"
    exit 1
fi

# Verificar si existe el archivo docker-compose
if [ ! -f "docker-compose.full.yml" ]; then
    echo -e "${RED}❌ Error: docker-compose.full.yml no encontrado${NC}"
    echo "Ejecuta este script desde el directorio del proyecto"
    exit 1
fi

echo -e "${BLUE}[INFO]${NC} Deteniendo contenedores..."
echo ""

# Detener contenedores sin eliminar volúmenes ni redes
docker compose -f docker-compose.full.yml stop

echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ CONTENEDORES DETENIDOS${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${BLUE}ℹ️  Los volúmenes y datos se mantienen intactos${NC}"
echo ""
echo "Para volver a iniciar los contenedores:"
echo "  ./start-containers-only.sh"
echo ""
echo "Para ver contenedores detenidos:"
echo "  docker ps -a --filter 'name=ensurance'"
echo ""

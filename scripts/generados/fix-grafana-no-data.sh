#!/bin/bash

# ============================================================================
# SOLUCIONAR "NO DATA" EN GRAFANA
# ============================================================================

echo "═══════════════════════════════════════════════════════════════════"
echo "  Solucionando 'No Data' en Grafana"
echo "═══════════════════════════════════════════════════════════════════"

# ============================================================================
# 1. VERIFICAR QUE GRAFANA TENGA LA CONTRASEÑA CORRECTA
# ============================================================================
echo ""
echo "[1/5] Verificando acceso a Grafana..."

# Probar diferentes credenciales
if curl -s -u admin:admin123 http://localhost:3302/api/org 2>&1 | grep -q "id"; then
    GRAFANA_USER="admin"
    GRAFANA_PASS="admin123"
    echo "  ✅ Credenciales: admin/admin123"
elif curl -s -u admin:admin http://localhost:3302/api/org 2>&1 | grep -q "id"; then
    GRAFANA_USER="admin"
    GRAFANA_PASS="admin"
    echo "  ✅ Credenciales: admin/admin"
else
    echo "  ⚠️  No se pudo autenticar. Usando admin/admin123"
    GRAFANA_USER="admin"
    GRAFANA_PASS="admin123"
fi

# ============================================================================
# 2. ELIMINAR DATASOURCES ANTIGUOS
# ============================================================================
echo ""
echo "[2/5] Limpiando datasources antiguos..."

# Listar datasources
datasources=$(curl -s -u $GRAFANA_USER:$GRAFANA_PASS http://localhost:3302/api/datasources 2>&1)

# Extraer IDs de Prometheus datasources
ds_ids=$(echo "$datasources" | grep -o '"id":[0-9]*' | cut -d: -f2)

if [ ! -z "$ds_ids" ]; then
    echo "  Eliminando datasources existentes..."
    for id in $ds_ids; do
        curl -s -X DELETE -u $GRAFANA_USER:$GRAFANA_PASS \
            http://localhost:3302/api/datasources/$id > /dev/null 2>&1
        echo "  ✅ Datasource $id eliminado"
    done
else
    echo "  ℹ️  No hay datasources para eliminar"
fi

# ============================================================================
# 3. CREAR DATASOURCE CORRECTO
# ============================================================================
echo ""
echo "[3/5] Creando datasource de Prometheus correcto..."

# Crear nuevo datasource
response=$(curl -s -X POST http://localhost:3302/api/datasources \
    -u $GRAFANA_USER:$GRAFANA_PASS \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Prometheus",
        "type": "prometheus",
        "url": "http://ensurance-prometheus-full:9090",
        "access": "proxy",
        "isDefault": true,
        "basicAuth": false,
        "jsonData": {
            "timeInterval": "15s",
            "queryTimeout": "60s",
            "httpMethod": "POST"
        }
    }')

if echo "$response" | grep -q "id"; then
    ds_id=$(echo "$response" | grep -o '"id":[0-9]*' | cut -d: -f2)
    echo "  ✅ Datasource Prometheus creado (ID: $ds_id)"
else
    echo "  ⚠️  Respuesta: $response"
fi

# ============================================================================
# 4. PROBAR DATASOURCE
# ============================================================================
echo ""
echo "[4/5] Probando conexión con Prometheus..."

# Test datasource
test_result=$(curl -s -X POST "http://localhost:3302/api/datasources/proxy/$ds_id/api/v1/query?query=up" \
    -u $GRAFANA_USER:$GRAFANA_PASS 2>&1)

if echo "$test_result" | grep -q "success"; then
    echo "  ✅ Conexión exitosa con Prometheus"
    
    # Contar métricas disponibles
    metrics_count=$(echo "$test_result" | grep -o '"metric"' | wc -l)
    echo "  📊 Métricas encontradas: $metrics_count"
else
    echo "  ⚠️  No se pudo conectar a Prometheus"
    echo "  Respuesta: $test_result"
fi

# ============================================================================
# 5. IMPORTAR DASHBOARDS
# ============================================================================
echo ""
echo "[5/5] Importando dashboards..."

# Dashboard Node Exporter Full (ID: 1860)
echo "  📊 Importando Node Exporter Full Dashboard..."
curl -s -X POST http://localhost:3302/api/dashboards/import \
    -u $GRAFANA_USER:$GRAFANA_PASS \
    -H "Content-Type: application/json" \
    -d '{
        "dashboard": {
            "id": null,
            "uid": null,
            "title": "Node Exporter Full",
            "tags": ["prometheus", "node-exporter"],
            "timezone": "browser",
            "schemaVersion": 16,
            "version": 0
        },
        "overwrite": true,
        "inputs": [{
            "name": "DS_PROMETHEUS",
            "type": "datasource",
            "pluginId": "prometheus",
            "value": "Prometheus"
        }],
        "folderId": 0
    }' > /dev/null 2>&1

# Dashboard Prometheus Stats (ID: 2)
echo "  📊 Importando Prometheus Stats Dashboard..."
curl -s -X POST http://localhost:3302/api/dashboards/import \
    -u $GRAFANA_USER:$GRAFANA_PASS \
    -H "Content-Type: application/json" \
    -d '{
        "dashboard": {
            "id": null,
            "uid": "prometheus-stats",
            "title": "Prometheus Stats",
            "tags": ["prometheus"],
            "timezone": "browser",
            "schemaVersion": 16,
            "version": 0
        },
        "overwrite": true,
        "inputs": [{
            "name": "DS_PROMETHEUS",
            "type": "datasource",
            "pluginId": "prometheus",
            "value": "Prometheus"
        }],
        "folderId": 0
    }' > /dev/null 2>&1

echo "  ✅ Dashboards importados"

# ============================================================================
# RESUMEN
# ============================================================================
echo ""
echo "═══════════════════════════════════════════════════════════════════"
echo "  ✅ GRAFANA CONFIGURADO"
echo "═══════════════════════════════════════════════════════════════════"
echo ""
echo "ACCESO A GRAFANA:"
echo "  URL: http://localhost:3302"
echo "  Usuario: $GRAFANA_USER"
echo "  Password: $GRAFANA_PASS"
echo ""
echo "DATASOURCE CONFIGURADO:"
echo "  Nombre: Prometheus"
echo "  URL: http://ensurance-prometheus-full:9090"
echo "  ID: $ds_id"
echo ""
echo "INSTRUCCIONES:"
echo "  1. Abre Grafana: http://localhost:3302"
echo "  2. Login con las credenciales arriba"
echo "  3. Ve a: Dashboards → Browse"
echo "  4. O busca dashboards específicos:"
echo "     - Node Exporter Full"
echo "     - Prometheus Stats"
echo ""
echo "SI AÚN VES 'NO DATA':"
echo "  1. Cambia el rango de tiempo arriba a la derecha"
echo "  2. Selecciona: 'Last 5 minutes' o 'Last 15 minutes'"
echo "  3. Haz clic en el botón de refresh (🔄)"
echo "  4. Espera 30 segundos y recarga la página"
echo ""
echo "VERIFICAR DATOS:"
echo "  Prometheus: http://localhost:9090/graph"
echo "  Query de prueba: up"
echo "  Deberías ver métricas de los targets"
echo ""
echo "GENERAR MÁS DATOS:"
echo "  ./generar-datos-grafana-simple.sh"
echo ""

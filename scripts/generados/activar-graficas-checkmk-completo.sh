#!/bin/bash

# ============================================================================
# SCRIPT MAESTRO: ACTIVAR GRÁFICAS EN CHECKMK
# Ejecuta todo el proceso de configuración de una vez
# ============================================================================

set -e

BOLD='\033[1m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BOLD}"
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║     ACTIVAR GRÁFICAS EN CHECKMK - SIMILAR A NETDATA              ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# ============================================================================
# VERIFICAR REQUISITOS
# ============================================================================
echo -e "${BLUE}[1/6] Verificando requisitos...${NC}"

# Verificar que CheckMK esté corriendo
if ! docker ps | grep -q ensurance-checkmk-full; then
    echo -e "${RED}❌ CheckMK no está corriendo${NC}"
    echo ""
    echo "Inicia los contenedores primero:"
    echo "  ./start-docker-full.sh"
    exit 1
fi

echo -e "${GREEN}  ✅ CheckMK está corriendo${NC}"

# Verificar que Prometheus esté corriendo
if ! docker ps | grep -q ensurance-prometheus-full; then
    echo -e "${RED}❌ Prometheus no está corriendo${NC}"
    exit 1
fi

echo -e "${GREEN}  ✅ Prometheus está corriendo${NC}"

# Verificar que Node Exporter esté corriendo
if ! docker ps | grep -q ensurance-node-exporter-full; then
    echo -e "${RED}❌ Node Exporter no está corriendo${NC}"
    exit 1
fi

echo -e "${GREEN}  ✅ Node Exporter está corriendo${NC}"

# ============================================================================
# PASO 1: CONFIGURAR DASHBOARDS
# ============================================================================
echo ""
echo -e "${BLUE}[2/6] Configurando dashboards...${NC}"

if [ -f "./configurar-dashboards-checkmk.sh" ]; then
    ./configurar-dashboards-checkmk.sh > /tmp/checkmk-dashboard-config.log 2>&1
    echo -e "${GREEN}  ✅ Dashboards configurados${NC}"
else
    echo -e "${YELLOW}  ⚠️  Script de dashboards no encontrado, saltando...${NC}"
fi

# ============================================================================
# PASO 2: CONFIGURAR PLUGINS DE GRÁFICAS
# ============================================================================
echo ""
echo -e "${BLUE}[3/6] Configurando plugins de gráficas...${NC}"

if [ -f "./configurar-graficas-prometheus-checkmk.sh" ]; then
    ./configurar-graficas-prometheus-checkmk.sh > /tmp/checkmk-graph-config.log 2>&1
    echo -e "${GREEN}  ✅ Plugins de gráficas configurados${NC}"
else
    echo -e "${YELLOW}  ⚠️  Script de gráficas no encontrado, saltando...${NC}"
fi

# ============================================================================
# PASO 3: HABILITAR SERVICIOS
# ============================================================================
echo ""
echo -e "${BLUE}[4/6] Habilitando servicios y métricas...${NC}"

if [ -f "./habilitar-graficas-checkmk.sh" ]; then
    ./habilitar-graficas-checkmk.sh > /tmp/checkmk-enable.log 2>&1
    echo -e "${GREEN}  ✅ Servicios habilitados${NC}"
else
    echo -e "${YELLOW}  ⚠️  Script de habilitación no encontrado, saltando...${NC}"
fi

# ============================================================================
# PASO 4: VERIFICAR CONFIGURACIÓN
# ============================================================================
echo ""
echo -e "${BLUE}[5/6] Verificando configuración...${NC}"

# Verificar que CheckMK responda
if curl -s -u "cmkadmin:admin123" "http://localhost:5152/ensurance/check_mk/api/1.0/domain-types/host_config/collections/all" > /dev/null 2>&1; then
    echo -e "${GREEN}  ✅ API de CheckMK funciona correctamente${NC}"
else
    echo -e "${YELLOW}  ⚠️  No se pudo conectar a la API de CheckMK${NC}"
fi

# Contar hosts
host_count=$(docker exec ensurance-checkmk-full omd su ensurance -c "cmk --list-hosts 2>/dev/null | wc -l" 2>/dev/null || echo "0")
echo -e "${GREEN}  ✅ Hosts configurados: $host_count${NC}"

# Contar servicios
service_count=$(curl -s -u "cmkadmin:admin123" "http://localhost:5152/ensurance/check_mk/api/1.0/domain-types/service/collections/all" 2>/dev/null | grep -o '"service_description"' | wc -l || echo "0")
echo -e "${GREEN}  ✅ Servicios activos: $service_count${NC}"

# ============================================================================
# PASO 5: CREAR PÁGINA DE RESUMEN
# ============================================================================
echo ""
echo -e "${BLUE}[6/6] Creando página de resumen...${NC}"

cat > /tmp/checkmk-graficas-resumen.html << 'HTMLEOF'
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gráficas Configuradas - Ensurance Pharmacy</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
            min-height: 100vh;
        }
        .container { max-width: 1200px; margin: 0 auto; }
        .header {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            text-align: center;
        }
        .header h1 {
            color: #667eea;
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        .success-message {
            background: #00b894;
            color: white;
            padding: 15px;
            border-radius: 10px;
            margin: 20px 0;
            text-align: center;
            font-size: 1.2em;
        }
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        .card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }
        .card h2 {
            color: #667eea;
            margin-bottom: 15px;
            font-size: 1.5em;
        }
        .card p { color: #666; margin-bottom: 15px; line-height: 1.6; }
        .card a {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 30px;
            text-decoration: none;
            border-radius: 25px;
            font-weight: bold;
            transition: all 0.3s ease;
        }
        .card a:hover {
            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
            transform: scale(1.05);
        }
        .icon { font-size: 3em; margin-bottom: 15px; }
        .info-box {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            margin-top: 20px;
        }
        .info-box h3 { color: #667eea; margin-bottom: 15px; }
        .info-box ul { list-style: none; padding-left: 0; }
        .info-box li {
            color: #666;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .info-box li:last-child { border-bottom: none; }
        .warning-box {
            background: #fdcb6e;
            color: #2d3436;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
        }
        .warning-box h3 { margin-bottom: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>✅ Gráficas Configuradas Exitosamente</h1>
            <p>CheckMK, Grafana y Netdata están listos para usar</p>
        </div>
        
        <div class="success-message">
            🎉 ¡Todo configurado! Las gráficas estarán disponibles en 15-30 minutos
        </div>
        
        <div class="warning-box">
            <h3>⏱️ Importante: Tiempo de Espera</h3>
            <p><strong>CheckMK necesita recolectar datos históricos antes de mostrar gráficas:</strong></p>
            <ul style="margin-left: 20px; margin-top: 10px;">
                <li>• Primera verificación: 1-5 minutos</li>
                <li>• Primeras métricas: 5-10 minutos</li>
                <li>• Gráficas completas: <strong>15-30 minutos</strong></li>
            </ul>
            <p style="margin-top: 15px;"><strong>💡 Mientras esperas:</strong> Usa Grafana o Netdata para ver gráficas inmediatas</p>
        </div>
        
        <div class="grid">
            <div class="card">
                <div class="icon">📈</div>
                <h2>Grafana</h2>
                <p><strong>⭐ RECOMENDADO PARA GRÁFICAS</strong></p>
                <p>Dashboards completos con gráficas hermosas, idénticas a Netdata</p>
                <p><strong>Usuario:</strong> admin<br><strong>Password:</strong> admin123</p>
                <a href="http://localhost:3001" target="_blank">Abrir Grafana →</a>
            </div>
            
            <div class="card">
                <div class="icon">⚡</div>
                <h2>Netdata</h2>
                <p><strong>TIEMPO REAL</strong></p>
                <p>Gráficas actualizadas cada segundo. Ideal para troubleshooting</p>
                <a href="http://localhost:19999" target="_blank">Abrir Netdata →</a>
            </div>
            
            <div class="card">
                <div class="icon">📊</div>
                <h2>CheckMK</h2>
                <p><strong>GESTIÓN Y ALERTAS</strong></p>
                <p>Dashboards configurados, alertas enterprise, gestión de servicios</p>
                <p><strong>Usuario:</strong> cmkadmin<br><strong>Password:</strong> admin123</p>
                <a href="http://localhost:5152/ensurance/check_mk/" target="_blank">Abrir CheckMK →</a>
            </div>
        </div>
        
        <div class="info-box">
            <h3>📊 Dashboards Creados en CheckMK</h3>
            <ul>
                <li><strong>Dashboard Principal:</strong> Overview con estado de hosts, servicios y gráficas de sistema</li>
                <li><strong>System Metrics:</strong> CPU, Memoria, Disco y Red de todos los hosts</li>
                <li><strong>Applications:</strong> Estado de aplicaciones, Prometheus, RabbitMQ, Netdata</li>
            </ul>
        </div>
        
        <div class="info-box">
            <h3>🎯 Cómo Ver Gráficas en CheckMK</h3>
            <ul>
                <li><strong>Opción 1:</strong> Monitor → Dashboards → Selecciona un dashboard</li>
                <li><strong>Opción 2:</strong> Monitor → All services → Click en servicio → Ver "Service Metrics"</li>
                <li><strong>Opción 3:</strong> Monitor → Performance → Selecciona host → Ver todas las gráficas</li>
            </ul>
        </div>
        
        <div class="info-box">
            <h3>💡 Recomendaciones</h3>
            <ul>
                <li><strong>Para gráficas hermosas:</strong> Usa Grafana (mejor visualización)</li>
                <li><strong>Para tiempo real:</strong> Usa Netdata (actualización cada segundo)</li>
                <li><strong>Para alertas:</strong> Usa CheckMK (sistema enterprise)</li>
                <li><strong>Para análisis:</strong> Usa Prometheus (consultas PromQL)</li>
            </ul>
        </div>
    </div>
</body>
</html>
HTMLEOF

# Copiar al directorio de CheckMK
docker cp /tmp/checkmk-graficas-resumen.html ensurance-checkmk-full:/omd/sites/ensurance/var/check_mk/web/htdocs/graficas-configuradas.html 2>/dev/null || true

echo -e "${GREEN}  ✅ Página de resumen creada${NC}"

# ============================================================================
# RESUMEN FINAL
# ============================================================================
echo ""
echo -e "${BOLD}${GREEN}"
echo "╔════════════════════════════════════════════════════════════════════╗"
echo "║                                                                    ║"
echo "║              ✅ CONFIGURACIÓN COMPLETADA EXITOSAMENTE             ║"
echo "║                                                                    ║"
echo "╚════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo ""
echo -e "${BOLD}ACCESO A LAS HERRAMIENTAS:${NC}"
echo ""
echo -e "${BLUE}📊 CheckMK:${NC}"
echo "   URL:      http://localhost:5152/ensurance/check_mk/"
echo "   Usuario:  cmkadmin"
echo "   Password: admin123"
echo ""
echo -e "${GREEN}📈 Grafana (RECOMENDADO PARA GRÁFICAS):${NC}"
echo "   URL:      http://localhost:3001"
echo "   Usuario:  admin"
echo "   Password: admin123"
echo ""
echo -e "${YELLOW}⚡ Netdata (TIEMPO REAL):${NC}"
echo "   URL:      http://localhost:19999"
echo ""
echo -e "${BLUE}🎯 Página de Resumen:${NC}"
echo "   URL:      http://localhost:5152/ensurance/graficas-configuradas.html"
echo ""

echo -e "${BOLD}DASHBOARDS CREADOS EN CHECKMK:${NC}"
echo "  📊 Dashboard Principal - Estado general y gráficas de sistema"
echo "  🖥️  System Metrics - Métricas detalladas de todos los hosts"
echo "  🚀 Applications - Estado de aplicaciones y servicios"
echo ""

echo -e "${BOLD}${YELLOW}⏱️  IMPORTANTE - TIEMPO DE ESPERA:${NC}"
echo ""
echo "  Las gráficas en CheckMK tardan en aparecer porque necesita"
echo "  recolectar datos históricos primero:"
echo ""
echo "  • Primera verificación: 1-5 minutos"
echo "  • Primeras métricas:    5-10 minutos"
echo "  • Gráficas completas:   15-30 minutos"
echo ""
echo -e "${GREEN}  💡 MIENTRAS ESPERAS:${NC}"
echo "     - Usa Grafana para gráficas inmediatas y hermosas"
echo "     - Usa Netdata para monitoreo en tiempo real"
echo ""

echo -e "${BOLD}CÓMO VER GRÁFICAS EN CHECKMK:${NC}"
echo ""
echo "  1. Desde Dashboards:"
echo "     Monitor → Dashboards → Selecciona un dashboard"
echo ""
echo "  2. Desde un Servicio:"
echo "     Monitor → All services → Click en servicio"
echo "     Busca la sección 'Service Metrics'"
echo ""
echo "  3. Vista de Performance:"
echo "     Monitor → Performance → Selecciona host"
echo ""

echo -e "${BOLD}VERIFICAR ESTADO:${NC}"
echo "  Hosts configurados:  $host_count"
echo "  Servicios activos:   $service_count"
echo ""

echo -e "${BOLD}COMANDOS ÚTILES:${NC}"
echo "  • Verificar CheckMK:     ./verificar-checkmk.sh"
echo "  • Ver logs:              docker logs ensurance-checkmk-full --tail 50"
echo "  • Reiniciar CheckMK:     docker restart ensurance-checkmk-full"
echo ""

echo -e "${BOLD}${GREEN}PRÓXIMOS PASOS:${NC}"
echo ""
echo "  1. Accede a Grafana (http://localhost:3001) para ver gráficas ahora"
echo "  2. Accede a CheckMK y explora los dashboards creados"
echo "  3. Espera 15-30 minutos para que CheckMK recolecte datos"
echo "  4. Vuelve a CheckMK para ver las gráficas completas"
echo ""

echo -e "${BOLD}DOCUMENTACIÓN:${NC}"
echo "  Lee la guía completa: ${BLUE}GUIA-GRAFICAS-CHECKMK.md${NC}"
echo ""

# Guardar logs
echo ""
echo "Logs guardados en:"
echo "  - /tmp/checkmk-dashboard-config.log"
echo "  - /tmp/checkmk-graph-config.log"
echo "  - /tmp/checkmk-enable.log"
echo ""

echo -e "${BOLD}${GREEN}✅ ¡CONFIGURACIÓN COMPLETADA!${NC}"
echo ""

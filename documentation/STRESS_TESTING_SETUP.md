# Stress Testing - Configuración Completa

## 🎯 Proyecto: Ensurance Pharmacy - Fase III

### Herramientas Implementadas

✅ **Apache JMeter 5.6.3**  
✅ **K6 (Grafana) 0.49.0**  
✅ **Grafana 11.3.0 + Prometheus 2.53.0**

---

## 📍 Ubicación de Archivos

```
ensurancePharmacy/
├── stress/                          # 📁 Directorio principal
│   ├── 🚀 run-tests.sh             # Menu interactivo
│   ├── 🔍 validate-setup.sh        # Validación
│   ├── 📊 view-jmeter-report.sh    # Ver reportes
│   ├── 🧹 cleanup-results.sh        # Limpiar
│   │
│   ├── 📖 README.md                 # Guía principal
│   ├── 📖 QUICKSTART.md             # Inicio rápido
│   ├── 📖 STRESS_TESTING_GUIDE.md   # Guía completa
│   ├── 📖 IMPLEMENTATION_SUMMARY.md # Resumen técnico
│   ├── 📖 EXAMPLES.md               # Ejemplos de comandos
│   │
│   ├── jmeter/
│   │   ├── test-plans/
│   │   │   ├── ensurance-full-test.jmx  # ⭐ Plan completo
│   │   │   └── sample-plan.jmx          # Plan simple
│   │   └── README.md
│   │
│   └── k6/
│       ├── scripts/
│       │   ├── load-test.js        # ⭐ Carga progresiva
│       │   ├── stress-test.js      # ⭐ Test de estrés
│       │   ├── spike-test.js       # ⭐ Picos repentinos
│       │   ├── soak-test.js        # ⭐ Resistencia 30min
│       │   └── sample-script.js    # Básico
│       └── README.md
│
├── monitoring/
│   ├── grafana/
│   │   ├── dashboards/
│   │   │   └── k6-dashboard.json   # ⭐ Dashboard K6
│   │   └── provisioning/
│   │       ├── dashboards/
│   │       │   └── dashboards.yml
│   │       └── datasources/
│   │           └── prometheus.yml
│   └── prometheus/
│       └── prometheus.yml          # ⭐ Config actualizada
│
└── scripts/
    ├── docker-compose.stress.yml   # ⭐ Servicios stress
    └── docker-compose.monitor.yml  # ⭐ Grafana + Prometheus
```

---

## 🚀 Comandos Principales

### 1️⃣ Validar Setup
```bash
cd stress
./validate-setup.sh
```

### 2️⃣ Menu Interactivo
```bash
cd stress
./run-tests.sh
```
### 3️⃣ Comandos Directos

**JMeter:**
```bash
cd scripts
# Ejecutar test
JMETER_PLAN=ensurance-full-test.jmx docker compose -f docker-compose.stress.yml run --rm jmeter

# Levantar servidor de reportes (se hace automáticamente con run-tests.sh)
docker compose -f docker-compose.stress.yml up -d jmeter-report

# Acceder a: http://localhost:8085
# El contenedor es visible en Portainer como "ensurance-jmeter-report"
```

**K6:**
```bash
cd scripts
# Ejecutar test
TEST_SCRIPT=load-test.js docker compose -f docker-compose.stress.yml run --rm k6
```

### 4️⃣ Iniciar Grafana
```bash
cd scripts
docker-compose -f docker-compose.monitor.yml up -d
```
---

## 📊 Dashboards y Visualización

| Herramienta | URL | Credenciales |
|-------------|-----|--------------|
| **Grafana** | http://localhost:3300 | admin / changeme |
| **K6 Dashboard** | http://localhost:5665 | - (solo mientras corre K6) |
| **Prometheus** | http://localhost:9095 | - |
| **JMeter Report** | http://localhost:8085 | - (se levanta automáticamente después del test) |

---

## 📈 Gráficas Implementadas

{{ ... }}
1. ✅ Virtual Users (VUs)
2. ✅ Request Rate (req/sec)
3. ✅ Response Time Percentiles (p50, p95, p99)
4. ✅ Error Rate (gauge)
5. ✅ Total HTTP Requests
6. ✅ Failed Checks
7. ✅ Average p95 Response Time
8. ✅ Total Iterations

### JMeter Report (10+ gráficas)
1. ✅ Response Times Over Time
2. ✅ Active Threads Over Time
3. ✅ Bytes Throughput Over Time
4. ✅ Latencies Over Time
5. ✅ Response Time Percentiles
6. ✅ Transactions Per Second
7. ✅ Response Time Distribution
8. ✅ Connect Time Over Time
9. ✅ Success/Error Rate
10. ✅ Response Codes

---

## 🎯 Tests Configurados

| Test | Tipo | Duración | Usuarios | Objetivo |
|------|------|----------|----------|----------|
| **K6 Load** | Carga progresiva | 5 min | 10→50→100 | Rendimiento bajo carga |
| **K6 Stress** | Estrés | 8 min | 50→300 | Encontrar límites |
| **K6 Spike** | Picos | 2.5 min | 10→500→10 | Recuperación |
| **K6 Soak** | Resistencia | 30 min | 50 constante | Memory leaks |
| **JMeter Simple** | Verificación | <1 min | 5 | Health checks |
| **JMeter Full** | Completo | Config | Config | BackV4 + BackV5 |

---

## 🔧 Endpoints Testeados

### BackV4 (Puerto 8081)
- `GET /api/health` - Health check

### BackV5 (Puerto 8082)
- `GET /api/health` - Health check
- `GET /api/usuarios` - Lista usuarios
- `GET /api/medicamentos` - Lista medicamentos
- `GET /api/polizas` - Lista pólizas

---

## 📦 Tecnologías y Versiones

| Componente | Versión | Propósito |
|------------|---------|-----------|
| **Apache JMeter** | 5.6.3 | Stress testing tradicional |
| **K6** | 0.49.0 | Stress testing moderno |
| **Grafana** | 11.3.0 | Visualización |
| **Prometheus** | 2.53.0 | Métricas |
| **Pushgateway** | 1.9.0 | Push de métricas |

---

## ✅ Checklist de Uso

### Antes del Test
- [ ] Validar setup: `./validate-setup.sh`
- [ ] Verificar backends corriendo: `curl http://localhost:8081/api/health`
- [ ] Iniciar Grafana (opcional): `docker-compose -f docker-compose.monitor.yml up -d`

### Durante el Test
- [ ] Monitorear K6 dashboard: http://localhost:5665
- [ ] Ver métricas en Grafana: http://localhost:3300
- [ ] Revisar logs: `docker logs -f ensurance-k6`

### Después del Test
- [ ] Ver reportes JMeter: http://localhost:8085 (se abre automáticamente)
- [ ] Analizar métricas en Grafana
- [ ] Exportar resultados si necesario
- [ ] Documentar hallazgos
- [ ] Detener servidor JMeter: Presiona Ctrl+C en la terminal

---

## 🎓 Recursos de Documentación

1. **[stress/README.md](../stress/README.md)** - README principal
2. **[stress/QUICKSTART.md](../stress/QUICKSTART.md)** - Guía de 5 minutos
3. **[stress/STRESS_TESTING_GUIDE.md](../stress/STRESS_TESTING_GUIDE.md)** - Guía detallada
4. **[stress/EXAMPLES.md](../stress/EXAMPLES.md)** - Ejemplos de comandos
5. **[stress/IMPLEMENTATION_SUMMARY.md](../stress/IMPLEMENTATION_SUMMARY.md)** - Resumen técnico
6. **[stress/jmeter/README.md](../stress/jmeter/README.md)** - Docs JMeter
7. **[stress/k6/README.md](../stress/k6/README.md)** - Docs K6

---

## 🆘 Troubleshooting Rápido

**❌ No puedo conectar a backends**
```bash
# Verifica que estén corriendo
curl http://localhost:8081/api/health
curl http://localhost:8082/api/health
```

**❌ Puerto en uso**
```bash
# Detén servicios existentes
docker-compose -f docker-compose.stress.yml down
docker-compose -f docker-compose.monitor.yml down
```

**❌ No veo métricas en Grafana**
- Espera 30 segundos después de iniciar Grafana
- Verifica Prometheus: http://localhost:9095
- Recarga el dashboard

**❌ JMeter no genera reporte**
```bash
# Ver logs
docker logs ensurance-jmeter
# Verificar volumen
docker volume ls | grep jmeter
```

**❌ No puedo acceder al reporte JMeter**
- El servidor HTTP se levanta automáticamente después del test en http://localhost:8085
- Si necesitas levantarlo manualmente: `./view-jmeter-report.sh`
- Verifica que el test haya completado exitosamente

---

## 📞 Comandos de Ayuda

```bash
# Ver estructura completa
cd stress && tree -L 3

# Ver todos los scripts
ls -lh *.sh

# Ver documentación disponible
ls -lh *.md

# Validar configuración
./validate-setup.sh

# Menu de ayuda
./run-tests.sh
```

---

## 🎉 Implementación Completa

✅ **Apache JMeter** - Configurado y funcional  
✅ **K6 (Grafana)** - 5 scripts de prueba  
✅ **Grafana Dashboard** - 8+ gráficas de performance  
✅ **JMeter Reports** - 10+ gráficas adicionales  
✅ **Documentación** - Guías completas  
✅ **Scripts de utilidad** - Automatización  
✅ **Integración completa** - Prometheus + Grafana  

---

**Estado**: ✅ Listo para usar  
**Última actualización**: 2025-10-07  
**Fase del Proyecto**: Fase III - Herramientas de medición/test de Stress

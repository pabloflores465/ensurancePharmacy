# 🚀 START HERE - Stress Testing Suite

## ⚡ Inicio Inmediato (2 comandos)

```bash
# 1. Validar que todo esté listo
cd stress
./validate-setup.sh

# 2. Ejecutar menu interactivo
./run-tests.sh
```

---

## 📚 Documentación Disponible

| Archivo | Propósito | Tiempo de Lectura |
|---------|-----------|-------------------|
| **[QUICKSTART.md](./QUICKSTART.md)** | Guía rápida de 5 minutos | ⏱️ 5 min |
| **[README.md](./README.md)** | Documentación principal | ⏱️ 10 min |
| **[STRESS_TESTING_GUIDE.md](./STRESS_TESTING_GUIDE.md)** | Guía detallada completa | ⏱️ 15 min |
| **[EXAMPLES.md](./EXAMPLES.md)** | Ejemplos de comandos | ⏱️ 10 min |
| **[IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)** | Resumen técnico | ⏱️ 10 min |

---

## 🎯 Lo que tienes disponible
### ✅ Apache JMeter 5.6.3
- Plan simple de verificación
- Plan completo multi-escenario
- Reportes HTML automáticos

### 🎯 Tests Disponibles

### K6 Tests
1. **load-test.js** - Carga progresiva con múltiples escenarios (5 min)
2. **stress-test.js** - Push hasta 300 usuarios para encontrar límites (8 min)
3. **spike-test.js** - Picos repentinos de 500 usuarios (2.5 min)
4. **soak-test.js** - 50 usuarios constantes por 30 minutos

### JMeter Tests
5. **sample-plan.jmx** - Test simple de verificación
6. **ensurance-full-test.jmx** - Test completo con BackV4 y BackV5

### Utilidades
7. Iniciar Grafana + Prometheus
8. Verificar Backends
9. Ver reportes JMeter
10. **Levantar Backends DEV** (nuevo)
11. **Detener Backends DEV** (nuevo)

### ✅ Scripts de Utilidad
- Menu interactivo
- Validación de setup
- Visualizador de reportes

---

## 🎓 Recomendación de Lectura

**Si tienes 5 minutos:**
→ Lee **[QUICKSTART.md](./QUICKSTART.md)** y ejecuta `./run-tests.sh`

**Si tienes 15 minutos:**
→ Lee **[README.md](./README.md)** para entender la estructura completa

**Si necesitas ejemplos específicos:**
→ Consulta **[EXAMPLES.md](./EXAMPLES.md)**

**Para la entrega del proyecto:**
→ Revisa **[IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md)**

---

## 🔗 URLs Importantes

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| Grafana | http://localhost:3300 | admin / changeme |
| K6 Dashboard | http://localhost:5665 | - |
| Prometheus | http://localhost:9095 | - |
| JMeter Report | http://localhost:8085 | ejecutar `./view-jmeter-report.sh` |

---

## ✨ Próximos Pasos

1. ✅ Ejecuta `./validate-setup.sh` para verificar el setup
2. ✅ Inicia Grafana: `cd ../scripts && docker-compose -f docker-compose.monitor.yml up -d`
3. ✅ Ejecuta tu primer test: `./run-tests.sh` (opción 1 - Load Test)
4. ✅ Revisa resultados en Grafana: http://localhost:3300
5. ✅ Explora otros tests y documentación

---

## 💡 Comando más Útil

```bash
./run-tests.sh
```

Este menu interactivo te guía por todos los tests disponibles.

---

**Todo está listo para usar. ¡Comienza ahora con `./validate-setup.sh`!**

from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from datetime import datetime

doc = SimpleDocTemplate("documentation/EnsurancePharmacy_Documentacion_Completa.pdf",
                        pagesize=letter, rightMargin=50, leftMargin=50,
                        topMargin=50, bottomMargin=50)

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name='Title1', parent=styles['Heading1'], fontSize=22,
                          textColor=colors.HexColor('#667eea'), spaceAfter=20,
                          alignment=TA_CENTER, fontName='Helvetica-Bold'))
styles.add(ParagraphStyle(name='H2', parent=styles['Heading2'], fontSize=14,
                          textColor=colors.HexColor('#764ba2'), spaceAfter=10,
                          fontName='Helvetica-Bold'))
styles.add(ParagraphStyle(name='Body', parent=styles['BodyText'], fontSize=9,
                          alignment=TA_JUSTIFY, spaceAfter=6))
styles.add(ParagraphStyle(name='Bullet', parent=styles['BodyText'], fontSize=8,
                          leftIndent=15, spaceAfter=3))

story = []

# Port ada
story.append(Spacer(1, 2*inch))
story.append(Paragraph("ENSURANCE PHARMACY", styles['Title1']))
story.append(Spacer(1, 0.2*inch))
story.append(Paragraph("Sistema Integrado de Seguros y Farmacia<br/>Documentación Técnica Completa",
                      styles['Heading2']))
story.append(Spacer(1, inch))
story.append(Paragraph(f"Fecha: {datetime.now().strftime('%d/%m/%Y')}", styles['Normal']))
story.append(PageBreak())

# CONTENIDO (resumen de 26 slides)
content_sections = [
    ("1. RESUMEN EJECUTIVO", [
        "Sistema completo de microservicios para gestión de seguros médicos y farmacia",
        "Arquitectura distribuida: 2 frontends Vue 3 + 2 backends Java 21",
        "Multi-ambiente dockerizado (DEV/QA/MAIN)",
        "Pipeline CI/CD: GitHub Actions, Jenkins, Drone",
        "Observabilidad: Prometheus (9095) + Grafana (3300)",
        "Stress Testing: k6 (5665) + JMeter (9600) + k6 Operator (7860)"
    ]),
    ("2. ARQUITECTURA DEL SISTEMA", [
        "Componentes Principales:",
        "• Ensurance Frontend (Vue 3 + TypeScript + Vite) - Puerto 5175",
        "• Pharmacy Frontend (Vue 3 + JavaScript) - Puerto 8089",
        "• Ensurance Backend (Java 21 + HttpServer) - Puerto 8081",
        "• Pharmacy Backend (Java 21 + HttpServer) - Puerto 8082",
        "",
        "Base de Datos:",
        "• SQLite en ambientes dev/qa",
        "• Oracle en producción",
        "• Hibernate ORM para abstracción"
    ]),
    ("3. STACK TECNOLÓGICO", [
        "Frontend: Vue 3, TypeScript/JavaScript, Vite/Vue CLI, Tailwind CSS, Pinia, Vitest/Jest",
        "Backend: Java 21, HttpServer nativo, Hibernate 6.x, Maven, JaCoCo",
        "DevOps: Docker, GitHub Actions, Jenkins, Drone CI, SonarQube, Codecov",
        "Observabilidad: Prometheus, Grafana, CheckMK, métricas custom",
        "Testing: k6, JMeter, k6 Operator, scripts automatizados"
    ]),
    ("4. CONFIGURACIÓN MULTI-AMBIENTE", [
        "DEV (Puertos 3000-3003): Ramas develop/dev, hot-reload, debug Java",
        "QA (Puertos 4000-4003): Ramas qa/test, ambiente pruebas, datos testing",
        "MAIN (Puertos 5175, 8089, 8081, 8082): Ramas main/master, producción optimizada"
    ]),
    ("5. PIPELINE CI/CD", [
        "GitHub Actions:",
        "• Tests automatizados todos los componentes",
        "• Análisis cobertura JaCoCo (backend) y LCOV (frontend)",
        "• SonarQube Quality Gate por proyecto",
        "• Notificaciones email",
        "",
        "Jenkins:",
        "• Build multi-etapa",
        "• Deploy automatizado por ambiente",
        "• Integración Prometheus",
        "",
        "Drone CI:",
        "• Pipeline declarativo .drone.yml",
        "• Ejecución paralela de jobs",
        "• Docker-in-Docker support"
    ]),
    ("6. OBSERVABILIDAD Y MONITOREO", [
        "Prometheus (Puerto 9095):",
        "• TSDB optimizado, scraping 15s, retención 15 días",
        "• Métricas backend: http_requests_total, http_request_duration, db_queries_total",
        "• Métricas frontend: page_views_total, medicine_searches_total",
        "• Endpoints: 9464 (Pharmacy BE), 9465 (Ensurance BE), 9466 (Ensurance FE), 9467 (Pharmacy FE)",
        "",
        "Grafana (Puerto 3300):",
        "• Dashboards pre-configurados",
        "• Alertas en tiempo real",
        "• Credenciales: admin/changeme",
        "• Visualizaciones: gráficos, gauges, heatmaps"
    ]),
    ("7. HERRAMIENTAS DE STRESS TESTING", [
        "k6 (Grafana) - Puerto 5665:",
        "• Framework moderno en JavaScript",
        "• Métricas: RPS, percentiles (p50, p95, p99), error rate",
        "• Escenarios: Load testing gradual, Spike, Soak, Stress",
        "• Exporta a JSON, InfluxDB, Prometheus",
        "• Dashboard web embebido",
        "",
        "JMeter (Apache) - Puerto 9600:",
        "• Framework empresarial Java",
        "• Planes .jmx reutilizables",
        "• Métricas: Throughput, response times, error percentage",
        "• Reportes HTML detallados",
        "",
        "k6 Operator - Puerto 7860:",
        "• API REST para gestión de tests",
        "• Ejecución distribuida",
        "• Compatible Kubernetes"
    ]),
    ("8. ANÁLISIS DE PERFORMANCE", [
        "Frontend Lighthouse:",
        "• Ensurance: LCP 479ms (TTFB 16ms, Render Delay 463ms), CLS 0.00",
        "• Pharmacy: LCP 179ms (TTFB 2ms), CLS 0.00 (excelente)",
        "",
        "Backend (Proyección 5,000 usuarios):",
        "• Necesita 833 RPS",
        "• Actual: ~100 RPS (sin connection pool optimizado)",
        "• Cuellos de botella: Queries N+1, transacciones manuales, sin caché"
    ]),
    ("9. DEUDA TÉCNICA IDENTIFICADA", [
        "Total: 18 deudas técnicas",
        "",
        "Críticas (5):",
        "• Passwords texto plano, Roles sin validar, Deploy vacío, Secretos sin documentar, Entities expuestas",
        "",
        "Altas (4):",
        "• SonarQube duplica tests, Pipeline serial, Validaciones insuficientes, Lógica en DAOs",
        "",
        "Medias (7):",
        "• Codecov sin enforcement, Jenkinsfile monolítico, Logging printStackTrace, Tests limitados, etc.",
        "",
        "Bajas (2):",
        "• Notificaciones SMTP, Transacciones manuales"
    ]),
    ("10. ANÁLISIS DE CAUSA RAÍZ (RCA)", [
        "Categorías Ishikawa:",
        "1. Código: Passwords sin hashing, lógica en DAOs, validaciones manuales",
        "2. Arquitectura: Sin middleware, sin servicios, entities expuestas",
        "3. Base de Datos: Pool no optimizado, queries N+1, transacciones manuales",
        "4. Infraestructura: Sin caché HTTP, tests duplicados CI/CD, sin CDN",
        "5. Proceso: Tests limitados, sin load testing, deploy manual, sin APM"
    ]),
    ("11. RECOMENDACIONES Y PLAN DE ACCIÓN", [
        "Acciones Inmediatas (Semana 1-2):",
        "R1: Optimizar Connection Pool - 4h - Impacto: +400% throughput",
        "R2: Eliminar Queries N+1 - 8h - Impacto: -80% latencia",
        "R3: ThreadPool HttpServer - 2h - Impacto: Soporta 500+ requests",
        "",
        "Corto Plazo (Mes 1):",
        "• Implementar DTOs con MapStruct",
        "• Code splitting frontend",
        "• Caché HTTP y Hibernate 2nd level",
        "",
        "Mediano Plazo (Trimestre):",
        "• Migración Spring Boot",
        "• APM completo",
        "• Load testing automatizado"
    ]),
    ("12. ROADMAP DE MEJORA", [
        "Corto Plazo: DTOs, code splitting, caché",
        "Mediano Plazo: Spring Boot, APM, load testing",
        "Largo Plazo: Kubernetes, service mesh, autoscaling, multi-región"
    ]),
    ("13. MÉTRICAS OBJETIVO POST-REMEDIACIÓN", [
        "Backend:",
        "• Throughput: 100 → 1000 RPS",
        "• Latencia p95: 500ms → 80ms",
        "• CPU @ 5K: 90% → 40%",
        "",
        "Frontend:",
        "• LCP Ensurance: 479ms → <300ms",
        "• Bundle size: -60%",
        "• Time to Interactive: -50%",
        "",
        "Infraestructura:",
        "• Build time CI/CD: -50%",
        "• Deploy: Manual → <5min",
        "• Test coverage: → >80%"
    ]),
    ("14. CONCLUSIONES", [
        "Sistema robusto con arquitectura moderna de microservicios",
        "Pipeline CI/CD completo con múltiples herramientas",
        "Observabilidad avanzada con Prometheus y Grafana",
        "Stress testing automatizado con k6 y JMeter",
        "18 deudas técnicas identificadas y priorizadas",
        "Plan de acción claro con ROI estimado en 2-3 meses",
        "Proyección de soporte para 5,000 usuarios tras optimizaciones"
    ])
]

for title, items in content_sections:
    story.append(Paragraph(title, styles['H2']))
    story.append(Spacer(1, 0.1*inch))
    for item in items:
        story.append(Paragraph(item, styles['Bullet']))
    story.append(Spacer(1, 0.15*inch))

story.append(PageBreak())
story.append(Paragraph("INFORMACIÓN ADICIONAL", styles['Title1']))
story.append(Spacer(1, 0.2*inch))
story.append(Paragraph("Este documento complementa la presentación ejecutiva PPTX y los análisis detallados disponibles en el directorio /documentation/:", styles['Body']))
story.append(Paragraph("• matriz-deuda-tecnica.md - Matriz completa de deuda técnica", styles['Bullet']))
story.append(Paragraph("• analisis-rca-performance.md - Análisis de causa raíz detallado", styles['Bullet']))
story.append(Paragraph("• diagrama-espina-pescado-rca.html - Diagrama Ishikawa interactivo", styles['Bullet']))
story.append(Paragraph("• matriz-deuda-tecnica-grafica.html - Matriz gráfica con Canvas API", styles['Bullet']))

doc.build(story)
print("✅ PDF generado: documentation/EnsurancePharmacy_Documentacion_Completa.pdf")

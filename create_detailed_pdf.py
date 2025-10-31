from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.lib.units import inch
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer, PageBreak
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from datetime import datetime

# Crear documento
doc = SimpleDocTemplate(
    "documentation/EnsurancePharmacy_Documentacion_Completa.pdf",
    pagesize=letter,
    rightMargin=72,
    leftMargin=72,
    topMargin=72,
    bottomMargin=72,
)

# Estilos personalizados
styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name='CustomTitle',
    parent=styles['Heading1'],
    fontSize=24,
    textColor=colors.HexColor('#667eea'),
    spaceAfter=30,
    alignment=TA_CENTER,
    fontName='Helvetica-Bold'
))
styles.add(ParagraphStyle(
    name='CustomHeading2',
    parent=styles['Heading2'],
    fontSize=16,
    textColor=colors.HexColor('#764ba2'),
    spaceAfter=12,
    spaceBefore=12,
    fontName='Helvetica-Bold'
))
styles.add(ParagraphStyle(
    name='CustomHeading3',
    parent=styles['Heading3'],
    fontSize=13,
    textColor=colors.HexColor('#2d3748'),
    spaceAfter=8,
    spaceBefore=8,
    fontName='Helvetica-Bold'
))
styles.add(ParagraphStyle(
    name='BodyJustify',
    parent=styles['BodyText'],
    fontSize=10,
    alignment=TA_JUSTIFY,
    spaceAfter=8
))
styles.add(ParagraphStyle(
    name='BulletCustom',
    parent=styles['BodyText'],
    fontSize=9,
    leftIndent=20,
    spaceAfter=4
))

story = []

# Función auxiliar para tablas
def create_table(data, col_widths):
    t = Table(data, colWidths=col_widths)
    t.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor('#667eea')),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'LEFT'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('FONTSIZE', (0, 0), (-1, 0), 9),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 10),
        ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
        ('GRID', (0, 0), (-1, -1), 1, colors.black),
        ('FONTSIZE', (0, 1), (-1, -1), 8),
    ]))
    return t

# PORTADA
story.append(Spacer(1, 2*inch))
story.append(Paragraph("ENSURANCE PHARMACY", styles['CustomTitle']))
story.append(Spacer(1, 0.3*inch))
story.append(Paragraph("Sistema Integrado de Seguros y Farmacia", styles['Heading2']))
story.append(Paragraph("Documentación Técnica Completa", styles['Heading2']))
story.append(Spacer(1, 1*inch))
story.append(Paragraph(f"Fecha: {datetime.now().strftime('%d de %B, %Y')}", styles['Normal']))
story.append(Paragraph("Versión: 1.0", styles['Normal']))
story.append(PageBreak())

# El contenido continúa en otro mensaje debido a límites de tokens...
print("Generando PDF... (Parte 1/2)")

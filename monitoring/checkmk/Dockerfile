FROM checkmk/check-mk-raw:2.2.0-latest

# Configuración de CheckMK para Ensurance Pharmacy
ENV CMK_SITE_ID=ensurance
ENV CMK_PASSWORD=admin123

# Copiar archivos de configuración
COPY ./config/ /opt/omd/sites/ensurance/etc/check_mk/

# Exponer puertos
EXPOSE 5000 6557

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:5000/ensurance/check_mk/ || exit 1

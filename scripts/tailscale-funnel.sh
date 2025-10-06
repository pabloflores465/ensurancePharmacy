#!/usr/bin/env sh
set -euo pipefail

# 1) Trae el túnel arriba con tu clave (ajusta hostname/tags si quieres)
sudo tailscale up --reset \
  --auth-key="${TS_AUTHKEY}" \
  --hostname="macbook-air-de-gp" \
  --accept-routes=true

# 2) Limpia cualquier config previa de Serve/Funnel
tailscale serve reset || true
tailscale funnel reset || true

# 3) Vuelve a montar tus rutas:
#    /        -> 127.0.0.1:8000
#    /sonnar  -> 127.0.0.1:9000/sonnar
#    /jenkins -> 127.0.0.1:8080/jenkins
tailscale serve  --bg --https=443 --set-path=/        http://127.0.0.1:8000
tailscale serve  --bg --https=443 --set-path=/sonnar  http://127.0.0.1:9000/sonnar
tailscale serve  --bg --https=443 --set-path=/jenkins http://127.0.0.1:8080/jenkins

# 4) Abre al internet el puerto 443 con Funnel (persistente)
#    (puedes repetir con 8443 o 10000 si prefieres otro)
tailscale funnel --bg --https=443 http://127.0.0.1:8000

# 5) Estado
echo "==== SERVE STATUS ===="
tailscale serve status
echo "==== FUNNEL STATUS ===="
tailscale funnel status
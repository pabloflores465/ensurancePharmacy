import express from 'express';
import promClient from 'prom-client';

const app = express();
const register = new promClient.Registry();

// Métricas JVM default (CPU, memoria, etc)
promClient.collectDefaultMetrics({ register });

// Métrica personalizada: total de peticiones del frontend
const pageViewCounter = new promClient.Counter({
  name: 'ensurance_frontend_page_views_total',
  help: 'Total number of page views in Ensurance frontend',
  labelNames: ['route'],
  registers: [register]
});

// Endpoint de métricas
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});

const PORT = process.env.METRICS_PORT || 9466;
const HOST = process.env.METRICS_HOST || '0.0.0.0';

app.listen(PORT, HOST, () => {
  console.log(`Ensurance metrics server running on http://${HOST}:${PORT}/metrics`);
});

export { pageViewCounter };

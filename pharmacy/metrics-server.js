const express = require('express');
const promClient = require('prom-client');

const app = express();
const register = new promClient.Registry();

// Métricas JVM default (CPU, memoria, etc)
promClient.collectDefaultMetrics({ register });

// Métrica personalizada: total de búsquedas de medicamentos
const medicineSearchCounter = new promClient.Counter({
  name: 'pharmacy_frontend_medicine_searches_total',
  help: 'Total number of medicine searches in Pharmacy frontend',
  labelNames: ['search_type'],
  registers: [register]
});

// Endpoint de métricas
app.get('/metrics', async (req, res) => {
  res.set('Content-Type', register.contentType);
  res.end(await register.metrics());
});

const PORT = process.env.METRICS_PORT || 9467;
const HOST = process.env.METRICS_HOST || '0.0.0.0';

app.listen(PORT, HOST, () => {
  console.log(`Pharmacy metrics server running on http://${HOST}:${PORT}/metrics`);
});

module.exports = { medicineSearchCounter };

// scripts/generate-env-json.js
import fs from 'fs';
import path from 'path';
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';

// __dirname compatible con ES Modules
const __dirname = path.dirname(fileURLToPath(import.meta.url));

// Ruta al .env dos carpetas atrás
const envPath = path.resolve(__dirname, '../../../.env');

// Ruta donde se guardará env.json (público y accesible)
const outputPath = path.resolve(__dirname, '../public/env.json');

// Asegurar que exista la carpeta /public
if (!fs.existsSync(path.dirname(outputPath))) {
  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
}

// Función para leer y convertir el .env
function generarJSON() {
  const raw = fs.readFileSync(envPath, 'utf-8');
  const parsed = dotenv.parse(raw);

  const filteredVars = Object.fromEntries(
    Object.entries(parsed).filter(([key]) => key.startsWith('VITE_'))
  );

  fs.writeFileSync(outputPath, JSON.stringify(filteredVars, null, 2));
  console.log('✅ env.json actualizado:', new Date().toLocaleTimeString());
}

// Ejecutar inmediatamente
generarJSON();

// Y repetir cada 10 segundos
setInterval(generarJSON, 30 * 1000);
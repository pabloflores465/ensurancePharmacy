// scripts/generate-env-json.js
import fs from 'fs';
import path from 'path';
import dotenv from 'dotenv';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const envPath = path.resolve(__dirname, '../../../.env');
const outputPath = path.resolve(__dirname, '../public/env.json');

// Asegurar que public/ exista
if (!fs.existsSync(path.dirname(outputPath))) {
  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
}

function generarJSON() {
  const raw = fs.readFileSync(envPath, 'utf-8');
  const parsed = dotenv.parse(raw); // 🔁 solo lee el archivo .env directamente

  const filteredVars = Object.fromEntries(
    Object.entries(parsed).filter(([key]) =>
      key.startsWith('VITE_')
    )
  );

  fs.writeFileSync(outputPath, JSON.stringify(filteredVars, null, 2));
  console.log('✅ env.json actualizado:', new Date().toLocaleTimeString());
}

generarJSON();
setInterval(generarJSON, 10 * 1000);
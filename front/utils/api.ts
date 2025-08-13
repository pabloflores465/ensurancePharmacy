import { useRuntimeConfig } from "#app";

export const getInsuranceApiUrl = (endpoint: string): string => {
  const config = useRuntimeConfig();
  const base = (config.public as any).ensuranceApiUrl || "";
  const clean = endpoint.startsWith("/") ? endpoint : `/${endpoint}`;
  if (base) return `${String(base).replace(/\/$/, "")}${clean}`;
  // Fallback por si no se definió variable
  return `http://localhost:8081/api${clean}`;
};

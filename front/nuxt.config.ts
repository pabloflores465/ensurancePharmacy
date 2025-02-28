import tailwindcss from "@tailwindcss/vite";
export default defineNuxtConfig({
  compatibilityDate: "2024-11-01",
  css: ['./main.css'],
  devtools: {
    enabled: true,
    timeline: {
      enabled: true
    }
  },
  vite: {
    plugins: [
      tailwindcss(),
    ],
  },
});

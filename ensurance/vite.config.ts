/// <reference types="vitest" />
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), tailwindcss()],
  // @ts-ignore
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: "src/test/setup.ts",
    css: true,
  },
});

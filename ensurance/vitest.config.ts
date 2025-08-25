import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    setupFiles: ['tests/setup.ts'],
    coverage: {
      provider: 'v8',
      reports: ['text', 'html', 'lcov'],
      all: true,
      include: [
        'src/components/**/*.{vue,ts}',
        'src/utils/**/*.{ts}',
        'src/pages/*.{vue,ts}'
      ],
      exclude: [
        'src/App.vue',
        'src/main.ts',
        'src/router.ts',
        'src/eventBus.ts',
        'src/utils/api-integration.ts',
        'src/components/ServiceIntegration.vue',
        'src/pages/admin/**',
          'src/pages/catalog/**',
          'src/pages/employee/**',
          'src/pages/register.vue',
          'src/pages/user-services.vue'
      ],
      thresholds: {
        lines: 90,
        statements: 90,
        functions: 80,
        branches: 80
      }
    }
  }
})

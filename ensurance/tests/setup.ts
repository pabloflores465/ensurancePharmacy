import { vi } from 'vitest'

// Mock window.alert globally so tests can assert calls
Object.defineProperty(window, 'alert', {
  value: vi.fn(),
  writable: true,
})

// Optional: mock scrollTo to avoid jsdom not implemented warnings if used
if (!('scrollTo' in window)) {
  // @ts-ignore
  window.scrollTo = vi.fn()
}

// Quiet noisy console logs from API URL helpers and page mount debug messages
const originalLog = console.log
console.log = (...args: any[]) => {
  try {
    const first = args[0]
    if (typeof first === 'string') {
      const suppress = [
        'ENSURANCE_API_BASE:',
        'portConfig.ensurance:',
        'API URL del hospital:',
        'Cargando citas...',
        'Citas obtenidas:',
        'Usuarios del hospital:',
        'Usuario encontrado en hospital:',
        'Usuario no encontrado en el sistema de hospital',
        'Intentando GET',
      ]
      if (suppress.some((s) => first.startsWith(s))) return
    }
  } catch {}
  originalLog(...args)
}

import { afterEach, beforeAll, afterAll, vi } from 'vitest'

let originalAlert: typeof window.alert | undefined

beforeAll(() => {
  originalAlert = window.alert
  // Evita que los tests fallen por alertas del navegador
  // y nos permite hacer assertions con vi.spy
  Object.defineProperty(window, 'alert', { value: vi.fn(), writable: true })
})

afterAll(() => {
  if (originalAlert) {
    Object.defineProperty(window, 'alert', { value: originalAlert, writable: true })
  }
})

afterEach(() => {
  localStorage.clear()
  sessionStorage.clear()
  vi.clearAllMocks()
})

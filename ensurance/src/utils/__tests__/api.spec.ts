import { describe, it, expect, vi, beforeEach } from 'vitest'

// Asegura aislamiento entre tests porque el módulo mantiene estado interno (portConfig)
beforeEach(() => {
  vi.resetModules()
  localStorage.clear()
})

describe('utils/api.ts', () => {
  it('genera URLs por defecto con puertos iniciales (8080 ensurance, 8081 pharmacy)', async () => {
    const api = await import('../api')

    const urlIns = api.getInsuranceApiUrl('users')
    const urlPh = api.getPharmacyApiUrl('/medicines')

    const host = (import.meta as any).env?.VITE_IP || 'localhost'
    expect(urlIns).toBe(`http://${host}:8080/api/users`)
    expect(urlPh).toBe(`http://${host}:8081/api/medicines`)
  })

  it('configureApiPorts persiste en localStorage y afecta a los URL builders', async () => {
    const api = await import('../api')

    api.configureApiPorts({ ensurance: '9090', pharmacy: '9091' })

    // Verifica persistencia
    const stored = localStorage.getItem('apiPortConfig')
    expect(stored).toBeTruthy()
    expect(JSON.parse(stored as string)).toEqual({ ensurance: '9090', pharmacy: '9091' })

    // Verifica URLs
    const host = (import.meta as any).env?.VITE_IP || 'localhost'
    expect(api.getInsuranceApiUrl('/status')).toBe(`http://${host}:9090/api/status`)
    expect(api.getPharmacyApiUrl('products')).toBe(`http://${host}:9091/api/products`)
  })

  it('loadPortConfiguration lee localStorage y actualiza los URL builders', async () => {
    // Pre-carga localStorage con una configuración
    localStorage.setItem('apiPortConfig', JSON.stringify({ ensurance: '7777', pharmacy: '6666' }))

    const api = await import('../api')

    api.loadPortConfiguration()

    const host = (import.meta as any).env?.VITE_IP || 'localhost'
    expect(api.getInsuranceApiUrl('ping')).toBe(`http://${host}:7777/api/ping`)
    expect(api.getPharmacyApiUrl('/items')).toBe(`http://${host}:6666/api/items`)
  })
})

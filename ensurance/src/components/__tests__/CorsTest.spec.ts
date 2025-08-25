import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'

// Use hoisted mocks to avoid Vitest hoisting issues
const hoisted = vi.hoisted(() => {
  return {
    mockTestCorsHospital: vi.fn(),
    mockTestCorsInsurance: vi.fn(),
  }
})

vi.mock('../../utils/api-integration', () => {
  return {
    testCorsHospital: hoisted.mockTestCorsHospital,
    testCorsInsurance: hoisted.mockTestCorsInsurance,
  }
})

import CorsTest from '../CorsTest.vue'

describe('CorsTest.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders config info with VITE_IP and buttons enabled', () => {
    const wrapper = mount(CorsTest)

    // Shows IP from env (or fallback text)
    const ipText = wrapper.text()
    const expectedIp = (import.meta as any).env?.VITE_IP || 'No configurado'
    expect(ipText).toContain(String(expectedIp))

    // Buttons present and enabled initially
    const buttons = wrapper.findAll('button')
    expect(buttons.length).toBeGreaterThan(0)
    expect(buttons[0].attributes('disabled')).toBeUndefined()
  })

  it('hospital button triggers test, shows loading and displays success result', async () => {
    vi.useFakeTimers()
    try {
      hoisted.mockTestCorsHospital.mockImplementationOnce(() =>
        new Promise((resolve) => setTimeout(() => resolve({ ok: true, service: 'hospital' }), 5))
      )

      const wrapper = mount(CorsTest)

      const btn = wrapper.findAll('button').find(b => b.text().includes('Probar CORS con Hospital'))
      expect(btn).toBeTruthy()

      await (btn as any)!.trigger('click')
      await nextTick()

      // While pending, button should be disabled
      expect((btn as any)!.attributes('disabled')).toBeDefined()

      // Resolve pending promise
      vi.runAllTimers()
      await flushPromises()

      // After resolve, result should appear and button re-enabled
      expect(wrapper.text()).toContain('"ok": true')
      expect(wrapper.text()).toContain('"service": "hospital"')

      const btnAfter = wrapper.findAll('button').find(b => b.text().includes('Probar CORS con Hospital'))
      expect(btnAfter && btnAfter.attributes('disabled')).toBeUndefined()

      expect(hoisted.mockTestCorsHospital).toHaveBeenCalledTimes(1)
    } finally {
      vi.useRealTimers()
    }
  })

  it('insurance button triggers test and displays success result', async () => {
    hoisted.mockTestCorsInsurance.mockResolvedValueOnce({ ok: true, service: 'insurance' })

    const wrapper = mount(CorsTest)

    const btn = wrapper.findAll('button').find(b => b.text().includes('Probar CORS con Seguro'))
    expect(btn).toBeTruthy()

    await (btn as any)!.trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('"ok": true')
    expect(wrapper.text()).toContain('"service": "insurance"')

    expect(hoisted.mockTestCorsInsurance).toHaveBeenCalledTimes(1)
  })

  it('shows error when hospital CORS test fails and re-enables button', async () => {
    vi.useFakeTimers()
    try {
      hoisted.mockTestCorsHospital.mockImplementationOnce(() =>
        new Promise((_, reject) => setTimeout(() => reject(new Error('Hospital CORS blocked')), 5))
      )

      const wrapper = mount(CorsTest)

      const btn = wrapper.findAll('button').find(b => b.text().includes('Probar CORS con Hospital'))
      expect(btn).toBeTruthy()

      await (btn as any)!.trigger('click')
      await flushPromises()

      // still loading before timers run
      expect((btn as any)!.attributes('disabled')).toBeDefined()

      vi.runAllTimers()
      await flushPromises()

      expect(wrapper.text()).toContain('Hospital CORS blocked')
      const btnAfter = wrapper.findAll('button').find(b => b.text().includes('Probar CORS con Hospital'))
      expect(btnAfter && btnAfter.attributes('disabled')).toBeUndefined()
    } finally {
      vi.useRealTimers()
    }
  })

  it('shows default message when insurance CORS test rejects with non-Error', async () => {
    // Reject with string to trigger default fallback text in component
    hoisted.mockTestCorsInsurance.mockRejectedValueOnce('weird failure')

    const wrapper = mount(CorsTest)

    const btn = wrapper.findAll('button').find(b => b.text().includes('Probar CORS con Seguro'))
    await (btn as any)!.trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Error al probar CORS con Seguro')
  })
})

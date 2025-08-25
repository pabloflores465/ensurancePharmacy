import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ApiDiagnostics from '../ApiDiagnostics.vue'

// Define hoisted mocks to be used inside vi.mock factories
const { axiosGet, mockTestCorsInsurance, mockTestCorsHospital } = vi.hoisted(() => ({
  axiosGet: vi.fn(),
  mockTestCorsInsurance: vi.fn(),
  mockTestCorsHospital: vi.fn(),
}))

vi.mock('../../utils/api-integration', () => ({
  testCorsInsurance: mockTestCorsInsurance,
  testCorsHospital: mockTestCorsHospital,
}))

vi.mock('axios', () => ({
  default: {
    get: axiosGet,
    interceptors: { request: { use: vi.fn() }, response: { use: vi.fn() } },
  },
}))

beforeEach(() => {
  vi.clearAllMocks()
  // Reset mocks to default resolved state unless overridden within a test
  mockTestCorsInsurance.mockResolvedValue({ ping: 'insurance-ok' })
  mockTestCorsHospital.mockResolvedValue({ ping: 'hospital-ok' })
  axiosGet.mockReset()
})

afterEach(() => {
  vi.restoreAllMocks()
})

function getCards(wrapper: any) {
  // Three cards in order: Insurance, Hospital, Pharmacy
  return wrapper.findAll('div.border.rounded-lg.p-4')
}

function getProbarButtons(wrapper: any) {
  // Exclude the top "Probar todas las conexiones" button
  return wrapper.findAll('button').filter((b: any) => b.text() === 'Probar')
}

describe('ApiDiagnostics.vue', () => {
  it('marks Insurance as connected when test succeeds', async () => {
    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    expect(buttons.length).toBe(3)

    await buttons[0].trigger('click') // Insurance
    await flushPromises()

    expect(mockTestCorsInsurance).toHaveBeenCalled()

    const cards = getCards(wrapper)
    expect(cards[0].text()).toContain('Conectado')
    expect(cards[0].text()).toContain('insurance-ok')
  })

  it('uses hospital fallback endpoint when primary test fails', async () => {
    mockTestCorsHospital.mockRejectedValueOnce(new Error('primary-fail'))
    axiosGet.mockResolvedValueOnce({ data: [{ id: 123 }] }) // fallback /api/services/

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[1].trigger('click') // Hospital
    await flushPromises()

    expect(mockTestCorsHospital).toHaveBeenCalled()
    expect(axiosGet).toHaveBeenCalled()
    const calledWith = (axiosGet.mock.calls[0] && axiosGet.mock.calls[0][0]) || ''
    expect(String(calledWith)).toContain('/api/services/')

    const cards = getCards(wrapper)
    expect(cards[1].text()).toContain('Conectado')
    expect(cards[1].text()).toContain('123')
  })

  it('pharmacy tries multiple endpoints and succeeds on alternative', async () => {
    // First pharmacy endpoint fails, second succeeds
    axiosGet
      .mockRejectedValueOnce(new Error('medicines-fail')) // /medicines
      .mockResolvedValueOnce({ data: [{ name: 'Aspirina' }] }) // /medications

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[2].trigger('click') // Pharmacy
    await flushPromises()

    expect(axiosGet).toHaveBeenCalledTimes(2)
    const firstUrl = axiosGet.mock.calls[0][0]
    const secondUrl = axiosGet.mock.calls[1][0]
    expect(String(firstUrl)).toContain('/medicines')
    expect(String(secondUrl)).toContain('/medications')

    const cards = getCards(wrapper)
    expect(cards[2].text()).toContain('Conectado')
    expect(cards[2].text()).toContain('Aspirina')
  })

  it('runAllTests executes sequentially and updates all statuses', async () => {
    // Insurance ok by default
    mockTestCorsInsurance.mockResolvedValueOnce({ ping: 'ok' })
    // Hospital primary fails -> fallback axios succeeds
    mockTestCorsHospital.mockRejectedValueOnce(new Error('primary-fail'))
    axiosGet
      .mockResolvedValueOnce({ data: [{ id: 1 }] }) // hospital fallback /api/services/
      .mockRejectedValueOnce(new Error('pharm-1')) // pharmacy /medicines
      .mockResolvedValueOnce({ data: [{ name: 'AltOk' }] }) // pharmacy /medications

    const wrapper = mount(ApiDiagnostics)

    // Click top button: "Probar todas las conexiones"
    const topBtn = wrapper
      .findAll('button')
      .find((b: any) => b.text().includes('Probar todas las conexiones') || b.text().includes('Probando...'))
    expect(topBtn).toBeTruthy()

    await (topBtn as any).trigger('click')
    await flushPromises()

    // After runAllTests completes, verify calls and statuses
    expect(mockTestCorsInsurance).toHaveBeenCalled()
    expect(mockTestCorsHospital).toHaveBeenCalled()
    expect(axiosGet).toHaveBeenCalledTimes(3)

    const cards = getCards(wrapper)
    expect(cards[0].text()).toContain('Conectado')
    expect(cards[1].text()).toContain('Conectado')
    expect(cards[2].text()).toContain('Conectado')

    // Button should be enabled again with original label
    const topBtnAfter = wrapper
      .findAll('button')
      .find((b: any) => b.text().includes('Probar todas las conexiones'))
    expect(topBtnAfter).toBeTruthy()
  })

  it('marks Insurance as error and shows message when test fails', async () => {
    mockTestCorsInsurance.mockRejectedValueOnce(new Error('ins-fail'))

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[0].trigger('click') // Insurance
    await flushPromises()

    const cards = getCards(wrapper)
    expect(cards[0].text()).toContain('Error de conexión')
    expect(cards[0].text()).toContain('ins-fail')
  })

  it('hospital shows error when primary and fallback both fail', async () => {
    mockTestCorsHospital.mockRejectedValueOnce(new Error('primary-fail'))
    axiosGet.mockRejectedValueOnce(new Error('fallback-fail'))

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[1].trigger('click') // Hospital
    await flushPromises()

    const cards = getCards(wrapper)
    expect(cards[1].text()).toContain('Error de conexión')
    // Error message displayed should be from the primary failure according to implementation
    expect(cards[1].text()).toContain('primary-fail')
  })

  it('pharmacy shows error when all endpoints fail', async () => {
    axiosGet
      .mockRejectedValueOnce(new Error('pharm-1'))
      .mockRejectedValueOnce(new Error('pharm-2'))
      .mockRejectedValueOnce(new Error('pharm-3'))

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[2].trigger('click') // Pharmacy
    await flushPromises()

    expect(axiosGet).toHaveBeenCalledTimes(3)

    const cards = getCards(wrapper)
    expect(cards[2].text()).toContain('Error de conexión')
    expect(cards[2].text()).toContain('Todos los endpoints de farmacia fallaron')
  })

  it('insurance shows default unknown error message when rejecting with non-Error', async () => {
    mockTestCorsInsurance.mockRejectedValueOnce('string-failure' as any)

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[0].trigger('click')
    await flushPromises()

    const cards = getCards(wrapper)
    expect(cards[0].text()).toContain('Error de conexión')
    expect(cards[0].text()).toContain('Error desconocido al conectar con el sistema de seguros')
  })

  it('hospital shows default unknown error message when primary rejects with non-Error and fallback fails', async () => {
    mockTestCorsHospital.mockRejectedValueOnce('bad' as any)
    axiosGet.mockRejectedValueOnce(new Error('fallback-fail'))

    const wrapper = mount(ApiDiagnostics)

    const buttons = getProbarButtons(wrapper)
    await buttons[1].trigger('click')
    await flushPromises()

    const cards = getCards(wrapper)
    expect(cards[1].text()).toContain('Error de conexión')
    expect(cards[1].text()).toContain('Error desconocido al conectar con el sistema hospitalario')
  })
})

import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'

// Hoisted axios mock: callable axios({}) + get/post used in component
const hoisted = vi.hoisted(() => {
  const axiosFn: any = vi.fn()
  axiosFn.get = vi.fn()
  axiosFn.post = vi.fn()
  return { axios: axiosFn }
})

vi.mock('axios', () => ({
  default: hoisted.axios,
}))

// Mock getInsuranceApiUrl so we control the base URL composed inside tryMultipleIPs
vi.mock('../../utils/api', () => ({
  getInsuranceApiUrl: (endpoint: string) => `http://ins.test${endpoint}`,
}))

import DailyAppointments from '../admin/daily-appointments.vue'

describe('admin/daily-appointments.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('carga citas de hoy con today=true; vacío dispara sincronización sin resultados y muestra mensaje', async () => {
    vi.useFakeTimers()

    // 1) First call (fetchAppointments today) -> empty
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    // 2) syncAppointmentsFromHospital -> GET hospital appointments -> none
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(DailyAppointments)
    await flushPromises()

    // Debe mostrar mensaje de vacío del listado
    expect(wrapper.text()).toContain('No hay citas programadas para esta fecha')

    // Al no haber en hospital, muestra mensaje de éxito específico y lo limpia tras el timer
    expect(wrapper.text()).toContain('No se encontraron citas en el hospital para la fecha seleccionada')

    vi.runAllTimers()
    await flushPromises()

    // Mensaje se limpia
    expect(wrapper.text()).not.toContain('No se encontraron citas en el hospital para la fecha seleccionada')
  })

  it('sincroniza desde hospital cuando existen y crea citas en seguros; luego recarga y completa', async () => {
    vi.useFakeTimers()

    // 1) First fetch (today) -> empty to forzar sync
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    // 2) Hospital appointments devuelve 1
    const startIso = new Date().toISOString()
    hoisted.axios.get.mockResolvedValueOnce({
      data: {
        appointments: [
          { _id: 'h1', start: startIso, reason: 'Consulta', doctor: { name: 'Dr. X' }, patient: 'p1' },
        ],
      },
    })
    // 3) GET paciente del hospital
    hoisted.axios.get.mockResolvedValueOnce({ data: { email: 'pat@test.com' } })
    // 4) GET /users/by-email (tryMultipleIPs -> axios({}))
    hoisted.axios.mockResolvedValueOnce({ data: { idUser: 42 } })
    // 5) POST /ensurance-appointments
    hoisted.axios.mockResolvedValueOnce({ status: 201, data: {} })
    // 6) Recarga fetchAppointments -> ahora devuelve 1 cita en seguros
    hoisted.axios.mockResolvedValueOnce({
      data: [
        { idAppointment: 1, appointmentDate: startIso, doctorName: 'Dr. X', reason: 'Consulta', idUser: 42 },
      ],
    })

    const wrapper = mount(DailyAppointments)
    await flushPromises()

    // Debe listar la cita sincronizada (tabla visible)
    expect(wrapper.text()).toContain('Dr. X')
    expect(wrapper.text()).toContain('Consulta')

    // Mensaje de sincronización completada y se limpia tras timeout
    expect(wrapper.text()).toContain('Sincronización completada')
    vi.runAllTimers()
    await flushPromises()
    expect(wrapper.text()).not.toContain('Sincronización completada')

    // Verificar que se intentó crear en seguros
    const calls = hoisted.axios.mock.calls
    // Debe haber al menos una llamada POST a /ensurance-appointments
    const hasPost = calls.some((c: any) => c[0]?.method === 'POST' && String(c[0]?.url || '').includes('/ensurance-appointments'))
    expect(hasPost).toBe(true)
  })

  it('changeDate usa query ?date=YYYY-MM-DD cuando no es hoy', async () => {
    // Primera carga: devolver algo para salir de loading
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    // syncAppointmentsFromHospital -> nada
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(DailyAppointments)
    await flushPromises()

    // Preparar siguiente fetch con fecha no-hoy
    const nextDate = '2030-12-31'
    const input = wrapper.get('input[type="date"]')
    await input.setValue(nextDate)

    // La acción de cambio dispara fetchAppointments
    // Siguiente respuesta para ese fetch
    hoisted.axios.mockResolvedValueOnce({ data: [] })

    await input.trigger('change')
    await flushPromises()

    // Inspeccionar última llamada a axios({}) y su URL
    const last = hoisted.axios.mock.calls[hoisted.axios.mock.calls.length - 1]?.[0]
    expect(String(last?.url || '')).toContain(`/ensurance-appointments?date=${nextDate}`)
  })

  it('muestra error cuando falla tryMultipleIPs en fetchAppointments', async () => {
    hoisted.axios.mockRejectedValueOnce(new Error('Network down'))

    const wrapper = mount(DailyAppointments)
    await flushPromises()

    expect(wrapper.text()).toContain('Error al cargar las citas')
  })

  it('muestra información del hospital predeterminado cuando existe', async () => {
    localStorage.setItem('defaultHospital', JSON.stringify({ name: 'Hosp Demo', port: '6060' }))

    // First fetch -> empty; sync -> none
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(DailyAppointments)
    await flushPromises()

    expect(wrapper.text()).toContain('Hospital seleccionado: Hosp Demo (Puerto: 6060)')
  })

  it('formatDate devuelve N/A cuando no hay fecha', async () => {
    // Evitar llamadas iniciales: devolver vacío y sin hospital data
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(DailyAppointments)
    await flushPromises()

    // Forzar estado con una cita sin fecha
    ;(wrapper.vm as any).appointments = [
      { idAppointment: 99, appointmentDate: '', doctorName: '', reason: '', idUser: 1 },
    ]
    ;(wrapper.vm as any).loading = false
    await (wrapper.vm as any).$nextTick?.()

    expect(wrapper.text()).toContain('N/A')
  })
})

import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// Hoisted axios mock (callable + methods used by the components)
const hoisted = vi.hoisted(() => {
  const axiosFn: any = vi.fn()
  axiosFn.get = vi.fn()
  axiosFn.post = vi.fn()
  axiosFn.delete = vi.fn()
  return { axios: axiosFn }
})

vi.mock('axios', () => ({
  default: hoisted.axios,
}))

// Component under test
import Appointments from '../appointments.vue'

// Helper to prime common successful GETs on mount
async function primeCommonGetMocks() {
  // loadDoctors -> /doctors, /api/services/
  hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
  hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
  // loadAppointments -> /api/appointments/
  hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
}

describe('appointments.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    // jsdom doesn't have confirm by default
    // @ts-ignore
    window.confirm = vi.fn().mockReturnValue(true)
    // @ts-ignore
    window.alert = vi.fn()
  })

  it('inicializa la semana (7 días) y carga sin usuario', async () => {
    await primeCommonGetMocks()

    const wrapper = mount(Appointments)
    await flushPromises()

    expect((wrapper.vm as any).weekDays.length).toBe(7)
    // Render del título del calendario
    expect(wrapper.text()).toContain('Calendario de Citas')
  })

  it('permite seleccionar un horario libre y muestra el formulario', async () => {
    await primeCommonGetMocks()

    const wrapper = mount(Appointments)
    await flushPromises()

    // Click en el primer slot disponible
    const slots = wrapper.findAll('.slot')
    expect(slots.length).toBeGreaterThan(0)
    await slots[0].trigger('click')

    expect(wrapper.find('.appointment-form').exists()).toBe(true)
    expect(wrapper.text()).toContain('Nueva Cita para')
  })

  it('al enviar sin doctor seleccionado, muestra alerta de validación', async () => {
    await primeCommonGetMocks()

    const wrapper = mount(Appointments)
    await flushPromises()

    // Seleccionar un horario para habilitar el formulario
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')

    await wrapper.find('form').trigger('submit.prevent')
    expect(window.alert).toHaveBeenCalledWith('Debe seleccionar un doctor')
  })

  it('al enviar sin usuario autenticado, alerta que debe iniciar sesión', async () => {
    await primeCommonGetMocks()

    const wrapper = mount(Appointments)
    await flushPromises()

    // Simular selección de horario
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')

    // Establecer un doctor válido en el modelo
    ;(wrapper.vm as any).doctors = [{ _id: 'doc1', name: 'Dr. Test' }]
    ;(wrapper.vm as any).appointment.doctor = 'doc1'

    await wrapper.find('form').trigger('submit.prevent')
    expect(window.alert).toHaveBeenCalledWith('Debe iniciar sesión para agendar una cita')
  })

  it('crea una cita exitosamente y muestra confirmación', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 1, email: 'u@test.com', name: 'User Test' }))

    // Prime initial GETs on mount
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'doc1', name: 'Dr. Uno' }] } }) // /doctors
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } }) // /api/services/
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } }) // /api/appointments/

    const wrapper = mount(Appointments)
    await flushPromises()

    // Select a free slot and fill form
    const slots = wrapper.findAll('.slot')
    expect(slots.length).toBeGreaterThan(0)
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'Chequeo'
    ;(wrapper.vm as any).appointment.doctor = 'doc1'

    // During submit: GET /users?email=... returns found user with _id
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'huser1', email: 'u@test.com' }] } })
    // POST hospital create
    hoisted.axios.post.mockResolvedValueOnce({ data: { _id: 'apt1' } })
    // POST ensurance appointment (can succeed or fail; success here)
    hoisted.axios.post.mockResolvedValueOnce({ status: 201, data: {} })
    // After success, loadAppointments again -> GET /api/appointments/ then GET /users
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Cita agendada correctamente')
    expect(hoisted.axios.post).toHaveBeenCalled()
  })

  it('cancela una cita exitosamente y muestra confirmación', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 1, email: 'u@test.com', name: 'User Test' }))

    // Prime initial GETs on mount
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Seed one user appointment to list
    ;(wrapper.vm as any).userAppointments = [{ _id: 'apt1', start: new Date().toISOString(), doctor: 'doc1', reason: 'R' }]
    await wrapper.vm.$nextTick()

    // Mock deletes: hospital then ensurance
    hoisted.axios.delete.mockResolvedValueOnce({ status: 200 })
    hoisted.axios.delete.mockResolvedValueOnce({ status: 200 })
    // After deletion reload appointments
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    const cancelBtn = wrapper.find('button.btn-danger-sm')
    expect(cancelBtn.exists()).toBe(true)
    await cancelBtn.trigger('click')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Cita cancelada correctamente')
    expect(hoisted.axios.delete).toHaveBeenCalledTimes(2)
  })

  it('usa doctor.username cuando name no está al guardar en seguros', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 9, email: 'svc@test.com', name: 'Svc User' }))

    // Mount initial loads (values not critical here)
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Open form and seed doctors with username-only entry to force fallback
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).doctors = [{ _id: 'svc1', username: 'Servicio Uno' }]
    ;(wrapper.vm as any).appointment.reason = 'Srv'
    ;(wrapper.vm as any).appointment.doctor = 'svc1'

    // User exists in hospital
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'h9', email: 'svc@test.com' }] } })
    // Hospital create OK
    hoisted.axios.post.mockResolvedValueOnce({ data: { _id: 'apt-svc' } })
    // Insurance post OK (we assert payload)
    hoisted.axios.post.mockResolvedValueOnce({ status: 201, data: {} })
    // Reload after success
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    // Ensure second POST (insurance) received doctorName from username/name of service
    const postCalls = hoisted.axios.post.mock.calls
    // [0] hospital, [1] insurance
    expect(postCalls.length).toBeGreaterThanOrEqual(2)
    const insurancePayload = postCalls[1][1]
    expect(insurancePayload).toMatchObject({ doctorName: 'Servicio Uno' })
    expect(window.alert).toHaveBeenCalledWith('Cita agendada correctamente')
  })

  it('usa detalle de error (.detail) cuando falla creación en hospital', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 10, email: 'detail@test.com', name: 'Detail User' }))

    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'd10', name: 'D10' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'R'
    ;(wrapper.vm as any).appointment.doctor = 'd10'

    // user exists
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'h10', email: 'detail@test.com' }] } })
    // hospital create fails with detail
    hoisted.axios.post.mockRejectedValueOnce({ response: { data: { detail: 'Detalle error' } } })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al crear la cita: Detalle error')
  })

  it('no permite seleccionar un slot tomado (no abre formulario)', async () => {
    await primeCommonGetMocks()

    const wrapper = mount(Appointments)
    await flushPromises()

    // Seed an appointment at first day 08:00
    const day0: Date = (wrapper.vm as any).weekDays[0]
    const y = day0.getFullYear()
    const m = (day0.getMonth() + 1).toString().padStart(2, '0')
    const d = day0.getDate().toString().padStart(2, '0')
    ;(wrapper.vm as any).appointments = [{ start: `${y}-${m}-${d}T08:00:00` }]
    await wrapper.vm.$nextTick()

    const firstSlot = wrapper.findAll('.slot')[0]
    expect(firstSlot.classes()).toContain('taken')
    await firstSlot.trigger('click')
    await wrapper.vm.$nextTick()

    expect(wrapper.find('.appointment-form').exists()).toBe(false)
    expect((wrapper.vm as any).selectedSlot).toBeNull()
  })

  it('botón Cancelar del formulario resetea y oculta el formulario', async () => {
    await primeCommonGetMocks()

    const wrapper = mount(Appointments)
    await flushPromises()

    // Open form
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    expect(wrapper.find('.appointment-form').exists()).toBe(true)

    // Provide doctors to satisfy required field (not actually submitting)
    ;(wrapper.vm as any).doctors = [{ _id: 'd', name: 'D' }]
    ;(wrapper.vm as any).appointment.doctor = 'd'
    ;(wrapper.vm as any).appointment.reason = 'R'

    const cancelBtn = wrapper.find('button.btn-danger')
    expect(cancelBtn.exists()).toBe(true)
    await cancelBtn.trigger('click')
    await wrapper.vm.$nextTick()

    expect((wrapper.vm as any).selectedSlot).toBeNull()
    expect(wrapper.find('.appointment-form').exists()).toBe(false)
  })

  it('muestra error si falla la cancelación en el hospital', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 1, email: 'u@test.com', name: 'User Test' }))

    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    ;(wrapper.vm as any).userAppointments = [{ _id: 'aptBad', start: new Date().toISOString(), doctor: 'd', reason: 'R' }]
    await wrapper.vm.$nextTick()

    hoisted.axios.delete.mockRejectedValueOnce({ response: { data: { error: 'No encontrado' } } })

    const cancelBtn = wrapper.find('button.btn-danger-sm')
    await cancelBtn.trigger('click')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith(expect.stringContaining('Error al cancelar la cita:'))
  })

  it('registra usuario en hospital cuando no existe y crea la cita exitosamente', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 2, email: 'new@test.com', name: 'Nuevo Usuario' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'doc2', name: 'D2' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Select slot and fill
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'Control'
    ;(wrapper.vm as any).appointment.doctor = 'doc2'

    // Submit sequence
    // 1) GET /users?email -> not found (empty appointments array)
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
    // 2) POST /register -> returns _id
    hoisted.axios.post.mockResolvedValueOnce({ data: { _id: 'huser-new' } })
    // 3) POST /api/appointments/create -> returns _id
    hoisted.axios.post.mockResolvedValueOnce({ data: { _id: 'apt-new' } })
    // 4) POST ensurance appointment
    hoisted.axios.post.mockResolvedValueOnce({ status: 201, data: {} })
    // 5) loadAppointments after success -> GET /api/appointments/ then GET /users
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Cita agendada correctamente')
  })

  it('muestra error detallado si falla la creación de la cita en el hospital', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 3, email: 'err@test.com', name: 'Err User' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'doc3', name: 'D3' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Fill selection
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'Dolor'
    ;(wrapper.vm as any).appointment.doctor = 'doc3'

    // During submit: user exists in hospital
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'huser3', email: 'err@test.com' }] } })
    // Hospital appointment create fails
    hoisted.axios.post.mockRejectedValueOnce({ response: { data: { error: 'Fallo hospital' } } })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al crear la cita: Fallo hospital')
  })
 
  it('crea en hospital pero falla guardado en seguros; aún confirma y recarga', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 11, email: 'okhos@test.com', name: 'Ok Hospital' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'd11', name: 'D11' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Select slot and fill form
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'Razón'
    ;(wrapper.vm as any).appointment.doctor = 'd11'

    // Submit sequence: user exists, hospital create OK, insurance POST fails
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'h-ok', email: 'okhos@test.com' }] } }) // GET /users?email
    hoisted.axios.post.mockResolvedValueOnce({ data: { _id: 'apt-ok' } }) // POST hospital create
    hoisted.axios.post.mockRejectedValueOnce({ response: { data: { error: 'Seguro caído' } } }) // POST ensurance fails
    // loadAppointments after flow continues
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } }) // GET /api/appointments/
    hoisted.axios.get.mockResolvedValueOnce({ data: [] }) // GET /users

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    // Even if ensurance failed, success alert should be shown
    expect(window.alert).toHaveBeenCalledWith('Cita agendada correctamente')
  })
  
  it('muestra alerta si falla la creación de usuario en el hospital y no continúa con la cita', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 4, email: 'failreg@test.com', name: 'Fail Reg' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'doc4', name: 'D4' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Select slot and fill form
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'Consulta'
    ;(wrapper.vm as any).appointment.doctor = 'doc4'

    // Submit sequence: user not found, register fails
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } }) // GET /users?email -> not found
    hoisted.axios.post.mockRejectedValueOnce({ response: { data: { error: 'Registro falló' } } }) // POST /register -> fail

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    // Should show hospital user verification error and stop flow
    expect(window.alert).toHaveBeenCalledWith('Error al verificar usuario en el sistema del hospital')
    // Only the register POST should have been attempted
    expect(hoisted.axios.post).toHaveBeenCalledTimes(1)
  })

  it('muestra alerta si falla GET /users?email durante verificación en hospital', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 5, email: 'getfail@test.com', name: 'Get Fail' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'doc5', name: 'D5' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Select slot and fill form
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'R'
    ;(wrapper.vm as any).appointment.doctor = 'doc5'

    // Submit: first call is GET /users?email which fails
    hoisted.axios.get.mockRejectedValueOnce(new Error('Network'))

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al verificar usuario en el sistema del hospital')
    expect(hoisted.axios.post).not.toHaveBeenCalled()
  })

  it('muestra error si hospital responde sin _id para la cita', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 6, email: 'noid@test.com', name: 'No Id' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'doc6', name: 'D6' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'R'
    ;(wrapper.vm as any).appointment.doctor = 'doc6'

    // User exists in hospital
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'h6', email: 'noid@test.com' }] } })
    // Hospital create resolves but without _id
    hoisted.axios.post.mockResolvedValueOnce({ data: {} })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al crear la cita: No se pudo crear la cita en el hospital')
  })

  it('no cancela la cita si el usuario rechaza la confirmación', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 7, email: 'cancel@test.com', name: 'Cancel User' }))

    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    ;(wrapper.vm as any).userAppointments = [{ _id: 'aptX', start: new Date().toISOString(), doctor: 'd', reason: 'R' }]
    await wrapper.vm.$nextTick()

    // Override confirm to cancel
    // @ts-ignore
    window.confirm = vi.fn().mockReturnValue(false)

    const cancelBtn = wrapper.find('button.btn-danger-sm')
    await cancelBtn.trigger('click')
    await flushPromises()

    expect(hoisted.axios.delete).not.toHaveBeenCalled()
    expect(window.alert).not.toHaveBeenCalled()
  })

  it('al cancelar, falla eliminación en seguros pero aún confirma', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 8, email: 'insdel@test.com', name: 'Ins Del' }))

    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    ;(wrapper.vm as any).userAppointments = [{ _id: 'aptY', start: new Date().toISOString(), doctor: 'd', reason: 'R' }]
    await wrapper.vm.$nextTick()

    // Hospital delete ok, insurance delete fails
    hoisted.axios.delete.mockResolvedValueOnce({ status: 200 })
    hoisted.axios.delete.mockRejectedValueOnce(new Error('Insurance down'))
    // Reload after deletion
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    const cancelBtn = wrapper.find('button.btn-danger-sm')
    await cancelBtn.trigger('click')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Cita cancelada correctamente')
    expect(hoisted.axios.delete).toHaveBeenCalledTimes(2)
  })

  it('muestra "Error desconocido" al crear cita cuando el error no tiene estructura conocida', async () => {
    // Usuario autenticado
    localStorage.setItem('user', JSON.stringify({ idUser: 12, email: 'unk@test.com', name: 'Unknown Err' }))

    // Mount sequence
    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [{ _id: 'd12', name: 'D12' }] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    // Select a slot and fill minimal fields
    const slots = wrapper.findAll('.slot')
    await slots[0].trigger('click')
    ;(wrapper.vm as any).appointment.reason = 'R'
    ;(wrapper.vm as any).appointment.doctor = 'd12'

    // User exists in hospital
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [{ _id: 'h12', email: 'unk@test.com' }] } })
    // Hospital create fails with unknown structure (no response, no message)
    hoisted.axios.post.mockRejectedValueOnce({})

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al crear la cita: Error desconocido')
  })

  it('muestra detalle (.detail) cuando falla la cancelación en el hospital', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 13, email: 'det@test.com', name: 'Det User' }))

    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    ;(wrapper.vm as any).userAppointments = [{ _id: 'aptDet', start: new Date().toISOString(), doctor: 'd', reason: 'R' }]
    await wrapper.vm.$nextTick()

    hoisted.axios.delete.mockRejectedValueOnce({ response: { data: { detail: 'Detalle cancelación' } } })

    const cancelBtn = wrapper.find('button.btn-danger-sm')
    await cancelBtn.trigger('click')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al cancelar la cita: Detalle cancelación')
  })

  it('muestra "Error desconocido" cuando falla la cancelación sin estructura conocida', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 14, email: 'unkc@test.com', name: 'Unknown Cancel' }))

    hoisted.axios.get.mockResolvedValueOnce({ data: { doctors: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { services: [] } })
    hoisted.axios.get.mockResolvedValueOnce({ data: { appointments: [] } })

    const wrapper = mount(Appointments)
    await flushPromises()

    ;(wrapper.vm as any).userAppointments = [{ _id: 'aptUnk', start: new Date().toISOString(), doctor: 'd', reason: 'R' }]
    await wrapper.vm.$nextTick()

    hoisted.axios.delete.mockRejectedValueOnce({})

    const cancelBtn = wrapper.find('button.btn-danger-sm')
    await cancelBtn.trigger('click')
    await flushPromises()

    expect(window.alert).toHaveBeenCalledWith('Error al cancelar la cita: Error desconocido')
  })
})

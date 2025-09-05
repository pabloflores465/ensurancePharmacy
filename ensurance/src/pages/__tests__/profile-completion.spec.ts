import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'

// Hoisted spies
const hoisted = vi.hoisted(() => ({
  pushSpy: vi.fn(),
  axiosGet: vi.fn(),
  axiosPut: vi.fn(),
}))

vi.mock('../../router', () => ({
  default: { push: hoisted.pushSpy },
}))

vi.mock('axios', () => ({
  default: {
    get: hoisted.axiosGet,
    put: hoisted.axiosPut,
  },
}))

// Component under test
import ProfileCompletion from '../profile-completion.vue'

describe('profile-completion.vue', () => {
  const originalLocation = window.location

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  afterEach(() => {
    // restore window.location if replaced
    if (window.location !== originalLocation) {
      Object.defineProperty(window, 'location', { value: originalLocation, writable: false })
    }
  })

  it('redirige a /login cuando no hay usuario en localStorage', async () => {
    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    expect(hoisted.pushSpy).toHaveBeenCalledWith('/login')
    // loading debe terminar
    expect((wrapper.vm as any).loading).toBe(false)
  })

  it('carga y fusiona datos del usuario cuando hay idUser (éxito)', async () => {
    localStorage.setItem('user', JSON.stringify({
      idUser: 10,
      name: 'Ana',
      email: 'ana@test.com',
      cui: 123,
      phone: '555',
      address: 'Calle 1',
      birthDate: '2000-01-01',
      role: 'PATIENT',
      enabled: 1,
      password: 'x',
    }))

    hoisted.axiosGet.mockResolvedValueOnce({
      data: { phone: '777', address: 'Av 2' },
    })

    mount(ProfileCompletion)
    await flushPromises()

    const saved = JSON.parse(localStorage.getItem('user')!)
    expect(saved.phone).toBe('777')
    expect(saved.address).toBe('Av 2')
  })

  it('muestra error cuando falla la carga adicional del usuario', async () => {
    localStorage.setItem('user', JSON.stringify({
      idUser: 11,
      name: 'Luis',
      email: 'luis@test.com',
      cui: 111,
      phone: '000',
      address: 'Calle 2',
      birthDate: '1999-01-01',
      role: 'PATIENT',
      enabled: 1,
      password: 'x',
    }))

    hoisted.axiosGet.mockRejectedValueOnce(new Error('Network'))

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    expect(wrapper.text()).toContain('No se pudieron cargar tus datos')
  })

  it('bloquea guardar cuando faltan campos requeridos (botón deshabilitado y no llama PUT)', async () => {
    localStorage.setItem('user', JSON.stringify({
      idUser: 12,
      name: '', // falta nombre
      email: 'p@test.com',
      cui: 1,
      phone: '1',
      address: 'a',
      birthDate: '2000-01-01',
      role: 'PATIENT',
      enabled: 1,
      password: 'x',
    }))

    // No se llama a axios.get si no lo necesitamos; pero con idUser sí se llama; retornamos data vacío
    hoisted.axiosGet.mockResolvedValueOnce({ data: {} })

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    const saveBtn = wrapper.get('button')
    // hasMissingFields debe ser true -> botón deshabilitado
    expect(saveBtn.attributes('disabled')).toBeDefined()

    // Intentar click no debe disparar PUT
    await saveBtn.trigger('click')
    expect(hoisted.axiosPut).not.toHaveBeenCalled()
  })

  it('guarda con éxito, actualiza localStorage y redirige a /home tras 2s', async () => {
    vi.useFakeTimers()

    // Usuario completo para que hasMissingFields sea false (rol PATIENT requiere insuranceNumber y allergies como refs)
    localStorage.setItem('user', JSON.stringify({
      idUser: 20,
      name: 'Pedro',
      email: 'pedro@test.com',
      cui: 999,
      phone: '123',
      address: 'Z',
      birthDate: '1990-05-05',
      role: 'PATIENT',
      enabled: 1,
      password: 'pwd',
      policy: { idPolicy: 5 },
    }))

    hoisted.axiosGet.mockResolvedValueOnce({ data: {} })
    hoisted.axiosPut.mockResolvedValueOnce({ status: 200, data: { idUser: 20, name: 'Pedro' } })

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    // Llenar campos adicionales requeridos por PATIENT
    await wrapper.get('#insuranceNumber').setValue('ABC123')
    await wrapper.get('#allergies').setValue('Ninguna')

    // El botón debería estar habilitado ahora
    const saveBtn = wrapper.get('button')
    expect(saveBtn.attributes('disabled')).toBeUndefined()

    // Preparar window.location.href reemplazable
    const locMock = { href: '' } as any
    Object.defineProperty(window, 'location', { value: locMock, writable: true })

    await saveBtn.trigger('click')
    await flushPromises()

    // success = true mostrado
    expect(wrapper.text()).toContain('¡Perfil completado!')

    // Avanzar timers para ejecutar redirección
    vi.runAllTimers()
    await flushPromises()

    // Verificar que se intentó redirigir a /home
    expect(window.location.href).toBe('/home')

    // Verificar localStorage actualizado con profile_completed y profile_data
    const saved = JSON.parse(localStorage.getItem('user')!)
    expect(saved.profile_completed).toBe(true)
    expect(saved.profile_data).toMatchObject({
      insuranceNumber: 'ABC123',
      allergies: 'Ninguna',
    })

    vi.useRealTimers()
  })

  it('ADMIN: requiere licenseNumber para habilitar guardar', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Admin A',
      email: 'a@test.com',
      cui: 1,
      phone: '1',
      address: 'a',
      birthDate: '2000-01-01',
      role: 'ADMIN',
    }))

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    const saveBtn = wrapper.get('button')
    // Falta licenseNumber -> deshabilitado
    expect(saveBtn.attributes('disabled')).toBeDefined()

    await wrapper.get('#licenseNumber').setValue('LIC-001')
    // Ahora debería habilitarse
    expect(saveBtn.attributes('disabled')).toBeUndefined()
  })

  it('EMPLOYEE: requiere hospitalName para habilitar guardar', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Emp B',
      email: 'b@test.com',
      cui: 2,
      phone: '2',
      address: 'b',
      birthDate: '2001-01-01',
      role: 'EMPLOYEE',
    }))

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    const saveBtn = wrapper.get('button')
    // Falta hospitalName -> deshabilitado
    expect(saveBtn.attributes('disabled')).toBeDefined()

    await wrapper.get('#hospitalName').setValue('General Hospital')
    expect(saveBtn.attributes('disabled')).toBeUndefined()
  })

  it('INTERCONNECTION: requiere systemAccess para habilitar guardar', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Conn C',
      email: 'c@test.com',
      cui: 3,
      phone: '3',
      address: 'c',
      birthDate: '2002-01-01',
      role: 'INTERCONNECTION',
    }))

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    const saveBtn = wrapper.get('button')
    // Falta systemAccess -> deshabilitado
    expect(saveBtn.attributes('disabled')).toBeDefined()

    await wrapper.get('#systemAccess').setValue('External System X')
    expect(saveBtn.attributes('disabled')).toBeUndefined()
  })

  it('saveProfile: PUT falla y muestra mensaje de error', async () => {
    // Usuario PATIENT con idUser y campos base
    localStorage.setItem('user', JSON.stringify({
      idUser: 33,
      name: 'Pat D',
      email: 'd@test.com',
      cui: 4,
      phone: '4',
      address: 'd',
      birthDate: '2003-01-01',
      role: 'PATIENT',
      enabled: 1,
      password: 'pwd',
      policy: { idPolicy: 1 },
    }))

    hoisted.axiosGet.mockResolvedValueOnce({ data: {} })
    hoisted.axiosPut.mockRejectedValueOnce(new Error('Boom'))

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    await wrapper.get('#insuranceNumber').setValue('NUM-1')
    await wrapper.get('#allergies').setValue('Ninguna')

    const saveBtn = wrapper.get('button')
    expect(saveBtn.attributes('disabled')).toBeUndefined()

    await saveBtn.trigger('click')
    await flushPromises()

    expect(hoisted.axiosPut).toHaveBeenCalled()
    expect(wrapper.text()).toContain('No se pudo guardar tu perfil')
  })

  it('actualiza campos básicos via v-model y envía PUT con valores actualizados', async () => {
    // Usuario PATIENT con todos los campos base completos
    localStorage.setItem('user', JSON.stringify({
      idUser: 44,
      name: 'Nombre Original',
      email: 'orig@test.com',
      cui: 123456,
      phone: '1111',
      address: 'Dir Original',
      birthDate: '1990-01-01',
      role: 'PATIENT',
      enabled: 1,
      password: 'pwd',
    }))

    hoisted.axiosGet.mockResolvedValueOnce({ data: {} })
    hoisted.axiosPut.mockResolvedValueOnce({ status: 200, data: { idUser: 44 } })

    const wrapper = mount(ProfileCompletion)
    await flushPromises()

    // Interactuar con los inputs que usan v-model en el template
    await wrapper.get('#name').setValue('Nuevo Nombre')
    await wrapper.get('#email').setValue('nuevo@test.com')
    await wrapper.get('#phone').setValue('7777')
    await wrapper.get('#address').setValue('Nueva Dirección')

    // Campos adicionales requeridos por PATIENT
    await wrapper.get('#insuranceNumber').setValue('INS-99')
    await wrapper.get('#allergies').setValue('Ninguna')

    const saveBtn = wrapper.get('button')
    expect(saveBtn.attributes('disabled')).toBeUndefined()
    await saveBtn.trigger('click')
    await flushPromises()

    // Verificar que el payload del PUT contiene los nuevos valores
    expect(hoisted.axiosPut).toHaveBeenCalled()
    const putPayload = hoisted.axiosPut.mock.calls[0][1]
    expect(putPayload).toMatchObject({
      idUser: 44,
      name: 'Nuevo Nombre',
      email: 'nuevo@test.com',
      phone: '7777',
      address: 'Nueva Dirección',
    })
  })
})

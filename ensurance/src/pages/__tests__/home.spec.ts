import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// Hoisted spies
const hoisted = vi.hoisted(() => ({
  pushSpy: vi.fn(),
  axiosGet: vi.fn(),
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: hoisted.pushSpy }),
}))

vi.mock('axios', () => ({
  default: { get: hoisted.axiosGet },
}))

// Component under test
import Home from '../home.vue'

describe('home.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('muestra detalles de póliza para paciente con datos (success)', async () => {
    // User con policy id
    localStorage.setItem('user', JSON.stringify({
      name: 'Juan',
      email: 'juan@test.com',
      role: 'patient',
      policy: { idPolicy: 123 },
    }))

    hoisted.axiosGet.mockResolvedValueOnce({
      data: {
        percentage: 70,
        cost: 100,
        creationDate: '2023-01-02',
        expDate: '2024-05-06',
      },
    })

    const wrapper = mount(Home)
    await flushPromises()

    // Debe mostrar la sección de póliza con datos
    expect(wrapper.text()).toContain('Tu Póliza de Seguro')
    expect(wrapper.text()).toContain('70%')
    expect(wrapper.text()).toContain('Básica') // etiqueta para 70
    expect(wrapper.text()).toContain('Q100.00') // precio formateado
  })

  it('muestra error cuando falla la carga de detalles de póliza', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Ana',
      email: 'ana@test.com',
      role: 'patient',
      policy: { idPolicy: 456 },
    }))

    hoisted.axiosGet.mockRejectedValueOnce(new Error('Network'))

    const wrapper = mount(Home)
    await flushPromises()

    expect(wrapper.text()).toContain('No se pudieron cargar los detalles de tu póliza.')
  })

  it('sin idPolicy: no pide API y muestra mensaje de no disponible', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Luis',
      email: 'luis@test.com',
      role: 'patient',
      policy: { /* sin idPolicy */ },
    }))

    const wrapper = mount(Home)
    await flushPromises()

    // No debería haberse llamado a axios
    expect(hoisted.axiosGet).not.toHaveBeenCalled()
    expect(wrapper.text()).toContain('No hay información disponible sobre tu póliza.')
  })

  it('paneles por rol: muestra panel de empleado y navega al registrar cliente', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Erika',
      email: 'erika@test.com',
      role: 'employee',
    }))

    const wrapper = mount(Home)
    await flushPromises()

    expect(wrapper.text()).toContain('Panel de Empleado')
    // Click en la tarjeta de registrar cliente
    const cards = wrapper.findAll('.bg-teal-100')
    expect(cards.length).toBeGreaterThan(0)
    await cards[0].trigger('click')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/employee/register-client')
  })

  it('paneles por rol: muestra panel de administración y navega a gestión de usuarios', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Admin',
      email: 'admin@test.com',
      role: 'admin',
    }))

    const wrapper = mount(Home)
    await flushPromises()

    expect(wrapper.text()).toContain('Panel de Administración')
    // Click en la tarjeta de gestión de usuarios
    const adminCard = wrapper.find('.bg-blue-100')
    expect(adminCard.exists()).toBe(true)
    await adminCard.trigger('click')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/admin/users')
  })

  it('tolera JSON inválido en localStorage y termina loading', async () => {
    localStorage.setItem('user', '{ invalid json')

    const wrapper = mount(Home)
    await flushPromises()

    expect((wrapper.vm as any).isLoading).toBe(false)
    // No debería crashear y muestra el encabezado general
    expect(wrapper.text()).toContain('Bienvenido a Ensurance')
  })

  it('formatPrice formatea valores y maneja casos falsy', async () => {
    const wrapper = mount(Home)
    const vm = wrapper.vm as any
    expect(vm.formatPrice(undefined)).toBe('N/A')
    expect(vm.formatPrice(null)).toBe('N/A')
    expect(vm.formatPrice(0)).toBe('N/A')
    expect(vm.formatPrice(123.4)).toBe('Q123.40')
  })

  it('formatDate devuelve N/A para falsy y fecha local para cadenas válidas', async () => {
    const wrapper = mount(Home)
    const vm = wrapper.vm as any
    expect(vm.formatDate(undefined)).toBe('N/A')
    const d = '2023-01-02'
    expect(vm.formatDate(d)).toBe(new Date(d).toLocaleDateString())
  })

  it('fetchUserPolicyDetails: early return cuando no hay user/policy/idPolicy', async () => {
    // Montamos sin usuario en localStorage para evitar onMounted con fetch
    const wrapper = mount(Home)
    const vm = wrapper.vm as any

    // Caso 1: user null
    vm.user = null
    vm.isLoading = true
    await vm.fetchUserPolicyDetails()
    expect(hoisted.axiosGet).not.toHaveBeenCalled()
    expect(vm.isLoading).toBe(false)

    // Caso 2: user sin policy
    vm.user = { role: 'patient' }
    vm.isLoading = true
    await vm.fetchUserPolicyDetails()
    expect(hoisted.axiosGet).not.toHaveBeenCalled()
    expect(vm.isLoading).toBe(false)

    // Caso 3: user con policy pero sin idPolicy
    vm.user = { role: 'patient', policy: {} }
    vm.isLoading = true
    await vm.fetchUserPolicyDetails()
    expect(hoisted.axiosGet).not.toHaveBeenCalled()
    expect(vm.isLoading).toBe(false)
  })

  it('fetchUserPolicyDetails: éxito realiza petición y setea policyDetails', async () => {
    const wrapper = mount(Home)
    const vm = wrapper.vm as any
    vm.user = { role: 'patient', policy: { idPolicy: 999 } }

    hoisted.axiosGet.mockResolvedValueOnce({
      data: { percentage: 90, cost: 50, creationDate: '2023-02-03', expDate: '2024-03-04' },
    })

    await vm.fetchUserPolicyDetails()
    expect(hoisted.axiosGet).toHaveBeenCalledTimes(1)
    expect(vm.policyDetails).toBeTruthy()
    expect(vm.policyDetails.percentage).toBe(90)
    expect(vm.isLoading).toBe(false)
  })

  it('catálogos: navega a servicios cubiertos y hospitales', async () => {
    // Sin necesidad de usuario; las tarjetas de catálogos siempre se muestran
    const wrapper = mount(Home)
    await flushPromises()

    const insuranceCard = wrapper.find('.bg-blue-50')
    const hospitalsCard = wrapper.find('.bg-green-50')
    expect(insuranceCard.exists()).toBe(true)
    expect(hospitalsCard.exists()).toBe(true)

    await insuranceCard.trigger('click')
    await hospitalsCard.trigger('click')

    expect(hoisted.pushSpy).toHaveBeenCalledWith('/catalog/insurance-services')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/catalog/hospitals')
  })

  it('admin: navega en tarjetas adicionales (servicios, hospitales, pólizas)', async () => {
    localStorage.setItem('user', JSON.stringify({
      name: 'Admin',
      email: 'admin@test.com',
      role: 'admin',
    }))

    const wrapper = mount(Home)
    await flushPromises()

    const svcCard = wrapper.find('.bg-green-100')
    const hospSvcCard = wrapper.find('.bg-purple-100')
    const policiesCard = wrapper.find('.bg-amber-100')
    expect(svcCard.exists()).toBe(true)
    expect(hospSvcCard.exists()).toBe(true)
    expect(policiesCard.exists()).toBe(true)

    await svcCard.trigger('click')
    await hospSvcCard.trigger('click')
    await policiesCard.trigger('click')

    expect(hoisted.pushSpy).toHaveBeenCalledWith('/admin/insurance-services')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/admin/hospital-services')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/admin/policies')
  })
})

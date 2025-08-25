import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// axios mock: callable + get/delete/post methods used by component
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

import UserServices from '../user-services.vue'

describe('user-services.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    // @ts-ignore
    window.alert = vi.fn()
  })

  it('muestra mensaje de requerimiento de login cuando no hay usuario', async () => {
    const wrapper = mount(UserServices)
    await flushPromises()

    expect(wrapper.text()).toContain('Debe iniciar sesión para ver sus servicios')
    expect(wrapper.text()).toContain('Debe iniciar sesión para ver sus servicios médicos.')
  })

  it('carga transacciones vacías y muestra mensaje de vacío', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 5, email: 'u@test.com', name: 'U' }))

    // tryMultipleIPs -> axios({ .. })
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    // hospital user info -> not found
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    const wrapper = mount(UserServices)
    await flushPromises()

    expect(wrapper.text()).toContain('No se encontraron servicios cubiertos por el seguro.')

    // Switch to hospital tab
    const buttons = wrapper.findAll('button')
    await buttons[1].trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('No se encontró su información en el sistema del hospital')
  })

  it('muestra tabla con transacción cuando API devuelve datos', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 7, email: 'u2@test.com', name: 'U2' }))

    // insurance transactions -> one item
    hoisted.axios.mockResolvedValueOnce({
      data: [
        {
          idTransaction: 1,
          transDate: '2025-01-01',
          total: 150,
          copay: 30,
          transactionComment: 'OK',
          result: 'Aprobado',
          covered: 1,
          auth: 'X',
          hospital: { idHospital: 10, name: 'Hospital Central' },
        },
      ],
    })
    // hospital user info -> skip found
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    const wrapper = mount(UserServices)
    await flushPromises()

    expect(wrapper.text()).toContain('Hospital Central')
    expect(wrapper.text()).toContain('Cubierto')
    expect(wrapper.text()).toContain('Q150.00')
    expect(wrapper.text()).toContain('Q30.00')
  })

  it('maneja error al cargar transacciones mostrando mensaje de error', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 8, email: 'u3@test.com', name: 'U3' }))

    hoisted.axios.mockRejectedValueOnce(new Error('Network'))
    // hospital info still called, but won't matter; provide default
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })

    const wrapper = mount(UserServices)
    await flushPromises()

    expect(wrapper.text()).toContain('Error al cargar los servicios. Por favor, intente de nuevo más tarde.')
  })

  it('cuando encuentra usuario de hospital, lista servicios del hospital', async () => {
    localStorage.setItem('user', JSON.stringify({ idUser: 9, email: 'hos@test.com', name: 'Hos User' }))

    // insurance transactions -> empty
    hoisted.axios.mockResolvedValueOnce({ data: [] })
    // hospital users?email -> found array
    hoisted.axios.get.mockResolvedValueOnce({ data: [{ _id: 'hu1', email: 'hos@test.com' }] })
    // hospital appointments for patient -> two items
    hoisted.axios.get.mockResolvedValueOnce({
      data: [
        { _id: 'a1', doctor: { name: 'Dr. A' }, start: '2025-01-02T10:00:00Z', cost: 100, reason: 'Check' },
        { _id: 'a2', doctor: { name: 'Dr. B' }, start: '2025-01-03T11:00:00Z', cost: 200, reason: 'Lab' },
      ],
    })

    const wrapper = mount(UserServices)
    await flushPromises()

    // Switch to hospital tab
    const buttons = wrapper.findAll('button')
    await buttons[1].trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Cita con Dr. A')
    expect(wrapper.text()).toContain('Cita con Dr. B')
    expect(wrapper.text()).toContain('Q100.00')
    expect(wrapper.text()).toContain('Q200.00')
  })
})

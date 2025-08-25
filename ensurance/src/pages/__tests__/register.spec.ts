import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// axios needs to be callable (axios({...})) and have get/post methods
const hoisted = vi.hoisted(() => {
  const axiosFn: any = vi.fn()
  axiosFn.get = vi.fn()
  axiosFn.post = vi.fn()
  return { axios: axiosFn }
})

vi.mock('axios', () => ({
  default: hoisted.axios,
}))

// Component under test
import Register from '../register.vue'

// Helper: stub router-link
const globalStubs = {
  RouterLink: {
    template: '<a><slot /></a>',
  },
}

describe('register.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('carga pólizas al montar y renderiza el formulario', async () => {
    // GET /policy
    hoisted.axios.get.mockResolvedValueOnce({
      data: [
        { idPolicy: 10, percentage: 70, creationDate: '2025-01-01', expDate: '2026-01-01', cost: 300, enabled: 1 },
      ],
    })

    const wrapper = mount(Register, { global: { stubs: globalStubs } })
    await flushPromises()

    expect(hoisted.axios.get).toHaveBeenCalled()
    expect(wrapper.text()).toContain('Registrar Nuevo Usuario')
  })

  it('muestra error si las contraseñas no coinciden', async () => {
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })
    const wrapper = mount(Register, { global: { stubs: globalStubs } })
    await flushPromises()

    // set only passwords to trigger early validation
    ;(wrapper.vm as any).password = 'a'
    ;(wrapper.vm as any).confirmPassword = 'b'

    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Las contraseñas no coinciden')
    expect(hoisted.axios.post).not.toHaveBeenCalled()
  })

  it('muestra error si CUI no es numérico', async () => {
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })
    const wrapper = mount(Register, { global: { stubs: globalStubs } })
    await flushPromises()

    ;(wrapper.vm as any).password = 'p'
    ;(wrapper.vm as any).confirmPassword = 'p'
    ;(wrapper.vm as any).cui = 'abc' // inválido

    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('El CUI debe ser un número')
  })

  it('registro exitoso usando póliza existente y envía emails', async () => {
    // onMounted -> GET /policy returns a 70% enabled policy with id
    hoisted.axios.get.mockResolvedValueOnce({
      data: [
        { idPolicy: 10, percentage: 70, creationDate: '2025-01-01', expDate: '2026-01-01', cost: 300, enabled: 1 },
      ],
    })

    const wrapper = mount(Register, { global: { stubs: globalStubs } })
    await flushPromises()

    // Fill form data
    ;(wrapper.vm as any).name = 'Usuario Test'
    ;(wrapper.vm as any).cui = '123456'
    ;(wrapper.vm as any).phone = '5555'
    ;(wrapper.vm as any).email = 'user@test.com'
    ;(wrapper.vm as any).address = 'Calle 1'
    ;(wrapper.vm as any).birthDate = '2000-01-01'
    ;(wrapper.vm as any).password = 'pass'
    ;(wrapper.vm as any).confirmPassword = 'pass'

    // POST /users -> 201
    hoisted.axios.post.mockResolvedValueOnce({ status: 201, data: { idUser: 1 } })
    // sendWelcomeEmail -> two POSTs to /notifications/email
    hoisted.axios.post.mockResolvedValueOnce({ status: 200, data: {} })
    hoisted.axios.post.mockResolvedValueOnce({ status: 200, data: {} })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('¡Registro exitoso!')
    // At least 3 posts: users + 2 emails
    expect(hoisted.axios.post).toHaveBeenCalledTimes(3)
  })

  it('muestra mensaje de error cuando API de registro falla', async () => {
    hoisted.axios.get.mockResolvedValueOnce({ data: [] })
    const wrapper = mount(Register, { global: { stubs: globalStubs } })
    await flushPromises()

    ;(wrapper.vm as any).name = 'Usuario Test'
    ;(wrapper.vm as any).cui = '123456'
    ;(wrapper.vm as any).phone = '5555'
    ;(wrapper.vm as any).email = 'user@test.com'
    ;(wrapper.vm as any).address = 'Calle 1'
    ;(wrapper.vm as any).birthDate = '2000-01-01'
    ;(wrapper.vm as any).password = 'pass'
    ;(wrapper.vm as any).confirmPassword = 'pass'

    // First POST will be to create policy -> succeed
    hoisted.axios.post.mockResolvedValueOnce({ status: 201, data: { idPolicy: 99 } })
    // Second POST will be to /users -> fail
    hoisted.axios.post.mockRejectedValueOnce({ response: { data: { message: 'Correo ya existe' } } })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('Correo ya existe')
  })
})

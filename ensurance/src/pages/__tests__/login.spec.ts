import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// Hoisted spies to satisfy Vitest hoisting rules
const hoisted = vi.hoisted(() => ({
  pushSpy: vi.fn(),
  emitSpy: vi.fn(),
  checkMissingRequiredFields: vi.fn().mockReturnValue(false),
  axiosPost: vi.fn(),
}))

vi.mock('../../router', () => ({
  default: { push: hoisted.pushSpy },
}))

vi.mock('../../eventBus', () => ({
  default: { emit: hoisted.emitSpy },
}))

vi.mock('../../utils/profile-utils', () => ({
  checkMissingRequiredFields: hoisted.checkMissingRequiredFields,
}))

vi.mock('axios', () => ({
  default: { post: hoisted.axiosPost },
}))

// Component under test
import Login from '../login.vue'

const setInputValue = async (wrapper: ReturnType<typeof mount>, selector: string, value: string) => {
  const input = wrapper.find(selector)
  await input.setValue(value)
}

describe('login.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('redirige a /inactive-account cuando el usuario no está habilitado', async () => {
    hoisted.axiosPost.mockResolvedValueOnce({
      status: 200,
      data: { email: 'user@test.com', role: 'patient', enabled: 0 },
    })

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'user@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(hoisted.emitSpy).toHaveBeenCalledWith('login')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/inactive-account')
  })

  it('redirige a /profile-completion cuando faltan campos requeridos', async () => {
    // Force profile-utils to require completion
    hoisted.checkMissingRequiredFields.mockReturnValueOnce(true)

    hoisted.axiosPost.mockResolvedValueOnce({
      status: 200,
      data: { email: 'user2@test.com', role: 'patient', enabled: 1 },
    })

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'user2@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(hoisted.emitSpy).toHaveBeenCalledWith('login')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/profile-completion')
  })

  it('redirige a /home cuando el usuario está habilitado y perfil completo', async () => {
    hoisted.axiosPost.mockResolvedValueOnce({
      status: 200,
      data: { email: 'ok@test.com', role: 'patient', enabled: 1 },
    })

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'ok@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(hoisted.emitSpy).toHaveBeenCalledWith('login')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/home')
    // localStorage se actualiza con los datos del usuario
    expect(localStorage.getItem('user')).toContain('ok@test.com')
  })

  it('deshabilita el botón y muestra "Logging in..." durante la petición y re-habilita después', async () => {
    // Crear promesa controlada para mantener el loading activo
    let resolveFn: (v: any) => void
    const pending = new Promise((res) => { resolveFn = res as (v: any) => void })
    hoisted.axiosPost.mockReturnValueOnce(pending as unknown as Promise<any>)

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'loading@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    await wrapper.find('form').trigger('submit.prevent')
    // esperar a que reactive actualice loading
    await Promise.resolve()

    const btn = wrapper.find('button[type="submit"]')
    expect(btn.attributes('disabled')).toBeDefined()
    expect(btn.text()).toContain('Logging in...')

    // Resolver petición
    resolveFn!({ status: 200, data: { email: 'loading@test.com', role: 'patient', enabled: 1 } })
    await flushPromises()

    // Botón re-habilitado y texto restaurado
    expect(btn.attributes('disabled')).toBeUndefined()
    expect(btn.text()).toContain('Login')
  })

  it('muestra error 401 cuando las credenciales son inválidas', async () => {
    hoisted.axiosPost.mockRejectedValueOnce({ response: { status: 401 } })

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'bad@test.com')
    await setInputValue(wrapper, '#password', 'wrong')

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('Usuario o contraseña incorrectos')
    expect(hoisted.pushSpy).not.toHaveBeenCalled()
    // Botón re-habilitado tras error
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeUndefined()
  })

  it('muestra mensaje genérico cuando la respuesta no es 200', async () => {
    // Respuesta exitosa a nivel de red, pero status != 200
    hoisted.axiosPost.mockResolvedValueOnce({ status: 500, data: { ok: false } })

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'x@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('Error en el inicio de sesión. Por favor intente de nuevo.')
    expect(hoisted.pushSpy).not.toHaveBeenCalled()
    expect(hoisted.emitSpy).not.toHaveBeenCalled()
    // Botón re-habilitado
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeUndefined()
  })

  it('muestra la vista de usuario y permite hacer Logout', async () => {
    const wrapper = mount(Login)
    // Asegurar que no redirige por onMounted
    await flushPromises()

    // Forzar estado de usuario logueado en el componente
    ;(wrapper.vm as any).user = {
      id: 1,
      name: 'Ana',
      email: 'ana@test.com',
      role: 'patient',
      enabled: 1,
    }
    await flushPromises()

    // Verifica que renderiza la tarjeta de usuario
    expect(wrapper.text()).toContain('Welcome, Ana!')
    expect(wrapper.text()).toContain('Email:')
    expect(wrapper.text()).toContain('ana@test.com')
    expect(wrapper.text()).toContain('Role:')
    expect(wrapper.text()).toContain('patient')

    // Click en Logout
    await wrapper.get('button').trigger('click')
    await flushPromises()

    // Debe volver a mostrar el formulario de Login
    expect(wrapper.text()).toContain('Login')
    expect(wrapper.find('form').exists()).toBe(true)
  })

  it('muestra error de conexión cuando hay fallo de red', async () => {
    hoisted.axiosPost.mockRejectedValueOnce(new Error('Network Error'))

    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'user@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('Error de conexión. Por favor intente de nuevo.')
    expect(hoisted.pushSpy).not.toHaveBeenCalled()
    // Botón re-habilitado tras error
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeUndefined()
  })

  it('redirige a /home en onMounted si ya hay usuario en localStorage', async () => {
    localStorage.setItem('user', JSON.stringify({ email: 'cached@test.com', role: 'patient', enabled: 1 }))

    mount(Login)
    await flushPromises()

    expect(hoisted.pushSpy).toHaveBeenCalledWith('/home')
  })

  it('tolera JSON previo inválido y continúa el flujo de login', async () => {
    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'ok@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    // Establecer JSON inválido en localStorage después del mount para no afectar onMounted
    localStorage.setItem('user', '{"email":"ok@test.com", invalid json')

    hoisted.axiosPost.mockResolvedValueOnce({
      status: 200,
      data: { email: 'ok@test.com', role: 'patient', enabled: 1 },
    })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    // A pesar del JSON inválido, no debe romper y debe navegar a /home
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/home')
  })

  it('combina profile_completed y profile_data previos cuando el email coincide', async () => {
    const wrapper = mount(Login)
    await setInputValue(wrapper, '#email', 'ok@test.com')
    await setInputValue(wrapper, '#password', 'secret')

    // Establecer datos previos DESPUÉS del mount para evitar redirección onMounted
    localStorage.setItem('user', JSON.stringify({
      email: 'ok@test.com',
      role: 'patient',
      enabled: 1,
      profile_completed: true,
      profile_data: { allergies: ['penicillin'] },
    }))

    hoisted.axiosPost.mockResolvedValueOnce({
      status: 200,
      data: { email: 'ok@test.com', role: 'patient', enabled: 1 },
    })

    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    const saved = JSON.parse(localStorage.getItem('user')!)
    expect(saved.profile_completed).toBe(true)
    expect(saved.profile_data).toEqual({ allergies: ['penicillin'] })
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/home')
  })
})

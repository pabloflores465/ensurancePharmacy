import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'

// Hoisted spies to satisfy Vitest hoisting rules
const hoisted = vi.hoisted(() => ({
  pushSpy: vi.fn(),
  emitSpy: vi.fn(),
}))

// inactive-account.vue uses useRouter from vue-router
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: hoisted.pushSpy }),
}))

vi.mock('../../eventBus', () => ({
  default: { emit: hoisted.emitSpy },
}))

// Component under test
import InactiveAccount from '../inactive-account.vue'

describe('inactive-account.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    vi.useRealTimers()
  })

  it('prefill del email desde localStorage en onMounted', async () => {
    localStorage.setItem('user', JSON.stringify({ email: 'prefill@test.com' }))

    const wrapper = mount(InactiveAccount)
    await flushPromises()

    const input = wrapper.get('#email').element as HTMLInputElement
    expect(input.value).toBe('prefill@test.com')
  })

  it('muestra error de validación si el email está vacío al enviar', async () => {
    const wrapper = mount(InactiveAccount)

    // Asegurar que el input esté vacío y enviar el formulario
    await wrapper.find('#email').setValue('')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('Por favor ingrese un correo electrónico válido')
    // No debería estar en loading
    const btn = wrapper.get('button[type="submit"]')
    expect(btn.attributes('disabled')).toBeUndefined()
  })

  it('muestra mensaje de éxito tras solicitar activación y manejar timers', async () => {
    vi.useFakeTimers()

    const wrapper = mount(InactiveAccount)
    await wrapper.find('#email').setValue('user@example.com')

    await wrapper.find('form').trigger('submit.prevent')
    // Esperar a que reactive aplique loading
    await Promise.resolve()

    const btn = wrapper.get('button[type="submit"]')
    expect(btn.attributes('disabled')).toBeDefined()
    expect(btn.text()).toContain('Enviando...')

    // Ejecutar el setTimeout de 1000ms
    vi.runAllTimers()
    await flushPromises()

    // Debe mostrar el mensaje de éxito y re-habilitar el botón
    expect(wrapper.text()).toContain('Solicitud enviada correctamente')
    expect(btn.attributes('disabled')).toBeUndefined()
    expect(btn.text()).toContain('Solicitar activación')

    vi.useRealTimers()
  })

  it('realiza logout: limpia localStorage, emite evento y navega a /login', async () => {
    localStorage.setItem('user', JSON.stringify({ email: 'x@y.z' }))

    const wrapper = mount(InactiveAccount)

    const logoutBtn = wrapper.findAll('button').find(b => b.text().includes('Cerrar sesión'))
    expect(logoutBtn).toBeTruthy()

    await logoutBtn!.trigger('click')
    await flushPromises()

    expect(localStorage.getItem('user')).toBeNull()
    expect(hoisted.emitSpy).toHaveBeenCalledWith('logout')
    expect(hoisted.pushSpy).toHaveBeenCalledWith('/login')
  })
})

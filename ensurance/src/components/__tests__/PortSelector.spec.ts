import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import PortSelector from '../PortSelector.vue'

vi.mock('../../utils/api', () => ({
  configureApiPorts: vi.fn(),
  loadPortConfiguration: vi.fn(),
}))

// Re-import mocked fns to assert calls
import { configureApiPorts, loadPortConfiguration } from '../../utils/api'

const getInputs = (wrapper: ReturnType<typeof mount>) => {
  const ensuranceInput = wrapper.get('input#ensurancePort')
  const pharmacyInput = wrapper.get('input#pharmacyPort')
  return { ensuranceInput, pharmacyInput }
}

describe('PortSelector.vue', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('renderiza con valores por defecto y llama a loadPortConfiguration en mounted', async () => {
    const wrapper = mount(PortSelector)
    const { ensuranceInput, pharmacyInput } = getInputs(wrapper)

    expect((ensuranceInput.element as HTMLInputElement).value).toBe('8080')
    expect((pharmacyInput.element as HTMLInputElement).value).toBe('8081')
    expect(loadPortConfiguration).toHaveBeenCalledTimes(1)
  })

  it('muestra alerta y no guarda si los puertos son inválidos', async () => {
    const wrapper = mount(PortSelector)
    const { ensuranceInput } = getInputs(wrapper)

    await ensuranceInput.setValue('80') // inválido (< 1024)

    const saveBtn = wrapper.get('button')
    await saveBtn.trigger('click')

    expect(window.alert).toHaveBeenCalled()
    expect(configureApiPorts).not.toHaveBeenCalled()
  })

  it('guarda configuración válida, persiste preferencia y cierra el diálogo', async () => {
    const wrapper = mount(PortSelector)
    const { ensuranceInput, pharmacyInput } = getInputs(wrapper)

    await ensuranceInput.setValue('8085')
    await pharmacyInput.setValue('8090')

    const saveBtn = wrapper.get('button')
    await saveBtn.trigger('click')

    expect(configureApiPorts).toHaveBeenCalledWith({
      ensurance: '8085',
      pharmacy: '8090',
    })

    // Debe marcar skipPortSelector si la casilla está marcada por defecto
    expect(localStorage.getItem('skipPortSelector')).toBe('true')

    // El diálogo debe desaparecer
    expect(wrapper.html()).not.toContain('Configuración de Puertos')
  })
})

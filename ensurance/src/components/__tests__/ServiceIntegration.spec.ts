import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'

// Hoisted mocks to avoid Vitest hoisting pitfalls
const hoisted = vi.hoisted(() => {
  return {
    mockGetApprovedHospitals: vi.fn(),
    mockGetHospitalServices: vi.fn(),
    mockGetInsuranceCategories: vi.fn(),
    mockGetMedicationsList: vi.fn(),
    mockSyncHospitalServices: vi.fn(),
    mockRequestServiceApproval: vi.fn(),
    mockRegisterApprovedService: vi.fn(),
    mockRegisterApprovedMedication: vi.fn(),
  }
})

vi.mock('../../utils/api-integration', () => ({
  getApprovedHospitals: hoisted.mockGetApprovedHospitals,
  getHospitalServices: hoisted.mockGetHospitalServices,
  getInsuranceCategories: hoisted.mockGetInsuranceCategories,
  getMedicationsList: hoisted.mockGetMedicationsList,
  syncHospitalServices: hoisted.mockSyncHospitalServices,
  requestServiceApproval: hoisted.mockRequestServiceApproval,
  registerApprovedService: hoisted.mockRegisterApprovedService,
  registerApprovedMedication: hoisted.mockRegisterApprovedMedication,
}))

import ServiceIntegration from '../ServiceIntegration.vue'

describe('ServiceIntegration.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    // Default implementations (overridden per test)
    hoisted.mockGetMedicationsList.mockResolvedValue([])
  })

  it('fetchData fallback: usa hospital y categorías por defecto cuando APIs fallan', async () => {
    // Force fallbacks
    hoisted.mockGetApprovedHospitals.mockRejectedValueOnce(new Error('fail-hospitals'))
    hoisted.mockGetInsuranceCategories.mockRejectedValueOnce(new Error('fail-categories'))
    // Provide hospital services so table renders
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([
      {
        _id: 's1',
        name: 'Ecografía',
        category: { idCategory: 2, name: 'Diagnóstico', description: '', enabled: 1 },
        subcategory: 'Imagen',
        description: 'Eco abdominal',
        price: 300,
        approved: false,
      },
    ])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Default hospital name should be present
    expect(wrapper.text()).toContain('Hospital Central')
    // Service from mocked API should be visible
    expect(wrapper.text()).toContain('Ecografía')
    // Default categories should be present in the filter select
    expect(wrapper.text()).toContain('Consultas')
    expect(wrapper.text()).toContain('Diagnóstico')
    expect(wrapper.text()).toContain('Laboratorio')
  })

  it('approveService marca servicio como Aprobado y llama a registerApprovedService', async () => {
    // Happy-path data load
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([
      {
        _id: '1',
        name: 'Consulta General',
        category: { idCategory: 1, name: 'Consultas', description: '', enabled: 1 },
        subcategory: 'Medicina General',
        description: 'Consulta médica general',
        price: 250,
        approved: false,
      },
    ])
    hoisted.mockRegisterApprovedService.mockResolvedValueOnce(undefined)

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Find first "Aprobar" button in the services table and click
    const approveBtn = wrapper.findAll('button').find(b => b.text().includes('Aprobar'))
    expect(approveBtn).toBeTruthy()

    await (approveBtn as any)!.trigger('click')
    await flushPromises()

    // After approving, status should display "Aprobado" and API called
    expect(wrapper.text()).toContain('Aprobado')
    expect(hoisted.mockRegisterApprovedService).toHaveBeenCalledTimes(1)

    // Alert success message (mocked in global setup)
    expect((window as any).alert).toHaveBeenCalled()
  })

  it('syncHospitalData llama syncHospitalServices y refresca servicios', async () => {
    // Initial load to have selectedHospital and services
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 2, name: 'Hospital Demo', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices
      .mockResolvedValueOnce([
        { _id: 'a', name: 'Inicial', category: { idCategory: 1, name: 'Consultas', description: '', enabled: 1 }, price: 100, approved: false },
      ]) // first fetch during mounted
      .mockResolvedValueOnce([
        { _id: 'b', name: 'Refrescado', category: { idCategory: 1, name: 'Consultas', description: '', enabled: 1 }, price: 120, approved: true },
      ]) // refresh after sync

    hoisted.mockSyncHospitalServices.mockResolvedValueOnce(undefined)

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Click "Sincronizar Servicios"
    const syncBtn = wrapper.findAll('button').find(b => b.text().includes('Sincronizar Servicios'))
    expect(syncBtn).toBeTruthy()

    await (syncBtn as any)!.trigger('click')
    await flushPromises()

    // Verify sync called with hospitalId and "1" as insuranceId
    expect(hoisted.mockSyncHospitalServices).toHaveBeenCalledTimes(1)
    const args = hoisted.mockSyncHospitalServices.mock.calls[0]
    expect(args[0]).toBe(2)
    expect(args[1]).toBe('1')

    // Services should have been refreshed (second call to getHospitalServices)
    expect(hoisted.mockGetHospitalServices).toHaveBeenCalledTimes(2)
    expect(wrapper.text()).toContain('Refrescado')

    // Success alert shown
    expect((window as any).alert).toHaveBeenCalled()
  })

  it('syncPharmacyData: mapea datos correctamente y muestra alert de éxito', async () => {
    // Hospitals and categories minimal to pass mounted
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([])

    // Medications with partial fields to validate fallbacks in mapping
    hoisted.mockGetMedicationsList.mockResolvedValueOnce([
      { id: 10, name: 'Ibuprofeno 400mg', category: { id: 1, name: 'Analgésicos' }, activeIngredient: 'Ibuprofeno', description: 'Antiinflamatorio', price: 15, approved: true },
      { name: 'Genérico', price: 5 }, // missing fields should fallback
    ])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Cambiar a la pestaña de farmacia para ver la tabla
    await wrapper.findAll('button').find(b => b.text().includes('Farmacia Central'))!.trigger('click')

    // Debe mostrar ambos medicamentos y los fallbacks de texto
    expect(wrapper.text()).toContain('Ibuprofeno 400mg')
    expect(wrapper.text()).toContain('Genérico')
    // Fallbacks visibles en UI para el segundo medicamento
    expect(wrapper.text()).toContain('Sin categoría')
    expect(wrapper.text()).toContain('No especificado')
    expect(wrapper.text()).toContain('Sin descripción')

    // Precio formateado
    expect(wrapper.text()).toContain('Q15.00')

    // Alert de éxito
    expect((window as any).alert).toHaveBeenCalledWith('Sincronización de medicamentos completada')
  })

  it('syncPharmacyData: deshabilita botón mientras sincroniza (pending promise)', async () => {
    // Arrange: complete initial mounted sync, then start a second pending sync via button
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([])
    // First call resolves so loading ends and UI renders
    hoisted.mockGetMedicationsList.mockResolvedValueOnce([])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Go to pharmacy tab
    const pharmacyTab = wrapper.findAll('button').find(b => b.text().includes('Farmacia Central'))
    await (pharmacyTab as any)!.trigger('click')

    // Next sync will be pending
    let resolveFn: (v: any) => void
    const pending = new Promise(res => { resolveFn = res })
    hoisted.mockGetMedicationsList.mockReturnValueOnce(pending)

    const syncBtn = wrapper.findAll('button').find(b => b.text().includes('Sincronizar Medicamentos'))
    expect(syncBtn).toBeTruthy()
    await (syncBtn as any)!.trigger('click')
    await flushPromises()

    // Button should be disabled while syncingPharmacies is true
    expect((syncBtn as any)!.attributes('disabled')).toBeDefined()

    // Cleanup: resolve pending promise
    resolveFn!([])
  })

  it('syncPharmacyData: en error muestra mensaje y usa fallback sin alert de éxito', async () => {
    // Cause medications API to fail on mounted sync
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([])
    hoisted.mockGetMedicationsList.mockRejectedValueOnce(new Error('pharmacy down'))

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // La UI entra en estado de error general
    expect(wrapper.text()).toContain('Error al sincronizar datos de la farmacia. Por favor, intente nuevamente.')

    // No se muestra alert de éxito
    expect((window as any).alert).not.toHaveBeenCalledWith('Sincronización de medicamentos completada')
  })

  it('rejectService: cambia estado a Pendiente y muestra alert', async () => {
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    // Servicio aprobado inicialmente para mostrar botón Rechazar
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([
      { _id: 'ok', name: 'Rayos X', category: { idCategory: 2, name: 'Diagnóstico', description: '', enabled: 1 }, price: 100, approved: true },
    ])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    const rejectBtn = wrapper.findAll('button').find(b => b.text().includes('Rechazar'))
    expect(rejectBtn).toBeTruthy()
    await (rejectBtn as any)!.trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Pendiente')
    expect((window as any).alert).toHaveBeenCalledWith('Servicio rechazado exitosamente')
  })

  it('rejectMedication: cambia estado a Pendiente y muestra alert', async () => {
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([])
    // Medicamento aprobado para mostrar botón Rechazar
    hoisted.mockGetMedicationsList.mockResolvedValueOnce([
      { id: 5, name: 'Amoxicilina 500mg', category: { id: 2, name: 'Antibióticos' }, activeIngredient: 'Amoxicilina', description: 'Antibiótico', price: 35, approved: true },
    ])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Ir a pestaña Farmacia
    await wrapper.findAll('button').find(b => b.text().includes('Farmacia Central'))!.trigger('click')

    const rejectBtn = wrapper.findAll('button').find(b => b.text().includes('Rechazar'))
    expect(rejectBtn).toBeTruthy()
    await (rejectBtn as any)!.trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Pendiente')
    expect((window as any).alert).toHaveBeenCalledWith('Medicamento rechazado exitosamente')
  })

  it('filteredHospitalServices: filtra por término y categoría', async () => {
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    // Categories empty => component sets defaults (Consultas, Diagnóstico, Laboratorio)
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([
      { _id: '1', name: 'Ecografía', category: { idCategory: 2, name: 'Diagnóstico', description: '', enabled: 1 }, description: 'Eco abdominal', price: 300, approved: false },
      { _id: '2', name: 'Hemograma', category: { idCategory: 3, name: 'Laboratorio', description: '', enabled: 1 }, description: 'Examen de sangre', price: 80, approved: false },
    ])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Search for "Eco" should leave only Ecografía
    const search = wrapper.find('input[placeholder="Buscar servicios..."]')
    await search.setValue('Eco')
    await flushPromises()

    // Select category Laboratorio (id 3) should then show none
    const select = wrapper.find('select')
    await select.setValue('3')
    await flushPromises()

    // No rows containing Ecografía after mismatched category
    expect(wrapper.text()).not.toContain('Ecografía')
    // Change search to empty and category to 3 -> should show Hemograma only
    await search.setValue('')
    await select.setValue('3')
    await flushPromises()
    expect(wrapper.text()).toContain('Hemograma')
    expect(wrapper.text()).not.toContain('Ecografía')
  })

  it('filteredPharmacyMedications: filtra por término y categoría', async () => {
    hoisted.mockGetApprovedHospitals.mockResolvedValueOnce([
      { idHospital: 1, name: 'Hospital Central', address: '', phone: '', email: '', enabled: 1 },
    ])
    hoisted.mockGetInsuranceCategories.mockResolvedValueOnce([])
    hoisted.mockGetHospitalServices.mockResolvedValueOnce([])
    hoisted.mockGetMedicationsList.mockResolvedValueOnce([
      { id: 1, name: 'Paracetamol 500mg', category: { id: 1, name: 'Analgésicos' }, activeIngredient: 'Paracetamol', description: 'Analgésico', price: 25, approved: false },
      { id: 2, name: 'Amoxicilina 500mg', category: { id: 2, name: 'Antibióticos' }, activeIngredient: 'Amoxicilina', description: 'Antibiótico', price: 35, approved: true },
    ])

    const wrapper = mount(ServiceIntegration)
    await flushPromises()

    // Ir a pestaña Farmacia
    await wrapper.findAll('button').find(b => b.text().includes('Farmacia Central'))!.trigger('click')

    // Buscar por "moxi" -> debe mantener Amoxicilina
    const search = wrapper.find('input[placeholder="Buscar medicamentos..."]')
    await search.setValue('moxi')
    await flushPromises()
    expect(wrapper.text()).toContain('Amoxicilina 500mg')
    expect(wrapper.text()).not.toContain('Paracetamol 500mg')

    // Limpiar búsqueda y filtrar por categoría Analgésicos (1) -> debe mostrar Paracetamol
    const select = wrapper.findAll('select').find(s => s.element instanceof HTMLSelectElement)!
    await search.setValue('')
    await select.setValue('1')
    await flushPromises()
    expect(wrapper.text()).toContain('Paracetamol 500mg')
  })
})

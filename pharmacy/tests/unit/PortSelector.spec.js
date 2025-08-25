import { mount } from '@vue/test-utils';
import PortSelector from '@/components/PortSelector.vue';

// Mock ApiService used inside the component
const mockConfigureSpy = jest.fn();
const mockLoadSpy = jest.fn();

jest.mock('@/services/ApiService', () => {
  const mod = {
    configureApiPorts: (...args) => mockConfigureSpy(...args),
    loadPortConfiguration: (...args) => mockLoadSpy(...args),
    getPharmacyApiUrl: jest.fn(),
    getEnsuranceApiUrl: jest.fn(),
    defaultPortConfig: { pharmacy: '8081', ensurance: '8080' },
  };
  return { __esModule: true, default: mod, ...mod };
});

describe('PortSelector.vue', () => {
  let alertSpy;
  beforeEach(() => {
    localStorage.clear();
    mockConfigureSpy.mockClear();
    mockLoadSpy.mockClear();
    alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
  });

  afterEach(() => {
    alertSpy.mockRestore();
  });

  it('carga configuración al montar', () => {
    mount(PortSelector);
    expect(mockLoadSpy).toHaveBeenCalled();
  });

  it('valida puertos inválidos y muestra alerta', async () => {
    const wrapper = mount(PortSelector);
    await wrapper.setData({ pharmacyPort: '80', ensurancePort: '8080' });

    await wrapper.find('button.btn-primary').trigger('click');

    expect(alertSpy).toHaveBeenCalled();
    expect(mockConfigureSpy).not.toHaveBeenCalled();
    expect(wrapper.emitted('close')).toBeFalsy();
  });

  it('guarda configuración válida, persiste preferencia y emite close', async () => {
    const wrapper = mount(PortSelector);
    await wrapper.setData({ pharmacyPort: '8081', ensurancePort: '8080', savePreference: true });

    await wrapper.find('button.btn-primary').trigger('click');

    expect(mockConfigureSpy).toHaveBeenCalledWith({ pharmacy: '8081', ensurance: '8080' });
    expect(localStorage.getItem('skipPortSelector')).toBe('true');
    expect(wrapper.emitted('close')).toBeTruthy();
  });

  it('cuando la preferencia está desmarcada elimina skipPortSelector', async () => {
    // Preexistente como true
    localStorage.setItem('skipPortSelector', 'true');

    const wrapper = mount(PortSelector);
    await wrapper.setData({ pharmacyPort: '8081', ensurancePort: '8080', savePreference: false });

    await wrapper.find('button.btn-primary').trigger('click');

    expect(mockConfigureSpy).toHaveBeenCalledWith({ pharmacy: '8081', ensurance: '8080' });
    expect(localStorage.getItem('skipPortSelector')).toBeNull();
    expect(wrapper.emitted('close')).toBeTruthy();
  });

  it('montado: JSON inválido en apiPortConfig genera warning y mantiene defaults', () => {
    const warnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {});
    localStorage.setItem('apiPortConfig', '{invalid json');

    const wrapper = mount(PortSelector);

    expect(warnSpy).toHaveBeenCalled();
    // Defaults de ApiService.defaultPortConfig
    expect(wrapper.vm.pharmacyPort).toBe('8081');
    expect(wrapper.vm.ensurancePort).toBe('8080');

    warnSpy.mockRestore();
  });

  it('isValidPort valida correctamente rangos y tipos', () => {
    const wrapper = mount(PortSelector);
    expect(wrapper.vm.isValidPort('8081')).toBe(true);
    expect(wrapper.vm.isValidPort(8081)).toBe(true);
    expect(wrapper.vm.isValidPort('1023')).toBe(false);
    expect(wrapper.vm.isValidPort('65536')).toBe(false);
    expect(wrapper.vm.isValidPort('abc')).toBe(false);
    expect(wrapper.vm.isValidPort('65535')).toBe(true);
  });
});

import { mount } from '@vue/test-utils';
import Footer from '@/components/Footer.vue';

describe('Footer.vue', () => {
  it('renderiza el pie de página con el texto esperado', () => {
    const wrapper = mount(Footer);
    const footer = wrapper.find('footer');
    expect(footer.exists()).toBe(true);
    expect(footer.classes()).toContain('bg-blue-600');
    expect(footer.text()).toContain('Farmacia Salud');
    expect(footer.text()).toContain('2025');
  });
});

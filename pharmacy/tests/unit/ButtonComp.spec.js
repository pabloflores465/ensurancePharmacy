import { mount } from '@vue/test-utils';
import ButtonComp from '@/components/button-comp.vue';

describe('button-comp.vue', () => {
  it('muestra contador inicial y lo incrementa al hacer click', async () => {
    const wrapper = mount(ButtonComp);

    expect(wrapper.text()).toContain('Count is 0');
    expect(wrapper.find('button').exists()).toBe(true);

    await wrapper.find('button').trigger('click');
    expect(wrapper.text()).toContain('Count is 1');

    await wrapper.find('button').trigger('click');
    expect(wrapper.text()).toContain('Count is 2');
  });
});

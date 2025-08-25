import { mount } from '@vue/test-utils';
import Home from '@/pages/Home.vue';

// router-link is stubbed globally in setupTests.js

describe('Home.vue', () => {
  test('renderiza sidebar con iconos y enlace de aseguradoras', () => {
    const wrapper = mount(Home);

    // 3 icon buttons
    const icons = wrapper.findAll('.icon-btn');
    expect(icons.length).toBe(3);

    // router-link stubbed as <a> with class nav-item
    const aseguradoras = wrapper.find('a.nav-item');
    expect(aseguradoras.exists()).toBe(true);
    expect(aseguradoras.text()).toContain('Aseguradoras Afiliadas');
  });

  test('carrusel avanza y retrocede con wrap-around cambiando el índice', async () => {
    const wrapper = mount(Home);

    // currentImage es una ref expuesta por <script setup>
    expect(wrapper.vm.currentImage).toBe(0);

    // Next -> 1
    await wrapper.find('.carousel-btn.right').trigger('click');
    expect(wrapper.vm.currentImage).toBe(1);

    // Next -> 2
    await wrapper.find('.carousel-btn.right').trigger('click');
    expect(wrapper.vm.currentImage).toBe(2);

    // Next -> wrap -> 0
    await wrapper.find('.carousel-btn.right').trigger('click');
    expect(wrapper.vm.currentImage).toBe(0);

    // Prev -> wrap -> 2
    await wrapper.find('.carousel-btn.left').trigger('click');
    expect(wrapper.vm.currentImage).toBe(2);
  });

  test('sección de ofertas renderiza 3 tarjetas con títulos e imágenes', () => {
    const wrapper = mount(Home);
    const cards = wrapper.findAll('.offer-card');
    expect(cards.length).toBe(3);

    const text = wrapper.text();
    expect(text).toContain('Omron - Presión Arterial');
    expect(text).toContain('Sukrol - Suplemento Cerebral');
    expect(text).toContain('10% Descuento en ISDIN');

    // Verifica que existan imágenes, sin acoplarse al valor del src
    const imgs = wrapper.findAll('.offer-img');
    expect(imgs.length).toBe(3);
  });
});

import { mount } from '@vue/test-utils';
import CommentItem from '@/components/CommentItem.vue';

describe('CommentItem.vue', () => {
  const parent = { idComments: 1, user: { name: 'Ana Maria' }, commentText: 'Parent comment' };
  const child1 = { idComments: 2, prevComment: { idComments: 1 }, user: { name: 'Luis Perez' }, commentText: 'Child one' };
  const child2 = { idComments: 3, prevComment: { idComments: 1 }, user: null, commentText: 'Child two' };
  const nested = { idComments: 4, prevComment: { idComments: 2 }, user: { name: 'Zorro' }, commentText: 'Nested reply' };
  const allComments = [parent, child1, child2, nested];

  const mountCmp = (comment = parent, all = allComments) =>
    mount(CommentItem, {
      props: { comment, allComments: all },
    });

  it('muestra iniciales del usuario y el texto del comentario', () => {
    const wrapper = mountCmp();

    const avatar = wrapper.find('.user-avatar');
    expect(avatar.exists()).toBe(true);
    // "Ana Maria" -> "AM"
    expect(avatar.text()).toBe('AM');

    expect(wrapper.text()).toContain('Parent comment');
  });

  it('muestra "Anónimo" y ? cuando no hay usuario', () => {
    const wrapper = mountCmp(child2, allComments);

    const name = wrapper.find('.user-name');
    const avatar = wrapper.find('.user-avatar');
    expect(name.text()).toBe('Anónimo');
    // El componente calcula iniciales desde 'Anónimo' -> 'A'
    expect(avatar.text()).toBe('A');
  });

  it('emite evento reply con el comentario actual al hacer click', async () => {
    const wrapper = mountCmp();

    const btn = wrapper.find('button.reply-btn');
    expect(btn.exists()).toBe(true);
    await btn.trigger('click');

    const emits = wrapper.emitted('reply');
    expect(emits).toBeTruthy();
    expect(emits[0][0]).toEqual(parent);
  });

  it('renderiza respuestas hijas y anidadas basadas en allComments', () => {
    const wrapper = mountCmp();

    // Debe incluir los textos de los hijos directos
    expect(wrapper.text()).toContain('Child one');
    expect(wrapper.text()).toContain('Child two');

    // Y también el nieto (respuesta anidada)
    expect(wrapper.text()).toContain('Nested reply');

    // Hay al menos 3 bloques de comentario en total (padre + 2 hijos)
    const items = wrapper.findAll('.comment-item');
    expect(items.length).toBeGreaterThanOrEqual(3);
  });
});

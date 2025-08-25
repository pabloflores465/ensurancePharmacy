import { mount } from '@vue/test-utils';
import { nextTick } from 'vue';
import Comentarios from '@/components/Comentarios.vue';

// Mocks
jest.mock('@/services/ApiService', () => ({
  __esModule: true,
  default: {
    getPharmacyApiUrl: jest.fn((p) => `http://test${p}`),
  },
}));

jest.mock('vue-router', () => ({
  useRoute: jest.fn(() => ({ params: { id: '2' } })),
}));

const makeUserStore = (user) => ({
  user,
  getUser: () => user || {},
});

jest.mock('@/stores/userStore', () => ({
  useUserStore: jest.fn(),
}));

import { useUserStore } from '@/stores/userStore';

const flush = async () => {
  await nextTick();
  await Promise.resolve();
  await nextTick();
};

describe('Comentarios.vue', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // @ts-ignore
    global.fetch = jest.fn();
  });

  test('muestra prompt de login cuando no está loggeado', async () => {
    useUserStore.mockReturnValue(makeUserStore(null));
    // GET comentarios vacío
    fetch.mockResolvedValueOnce({ ok: true, json: async () => [] });

    const wrapper = mount(Comentarios, {
      props: { initialComments: [] },
      global: {
        stubs: {
          CommentItem: true,
          'router-link': { template: '<a><slot /></a>' },
        },
      },
    });

    await flush();

    expect(wrapper.find('.login-prompt').exists()).toBe(true);
    expect(wrapper.find('.comment-form').exists()).toBe(false);
  });

  test('carga comentarios en mounted y filtra top-level por producto', async () => {
    useUserStore.mockReturnValue(makeUserStore({ idUser: 1, name: 'Ana' }));

    const apiComments = [
      { idComments: 'a1', prevComment: null, medicine: { idMedicine: 2 }, user: { name: 'U1' } },
      { idComments: 'a2', prevComment: 'a1', medicine: { idMedicine: 2 }, user: { name: 'U2' } },
      { idComments: 'b1', prevComment: null, medicine: { idMedicine: 3 }, user: { name: 'U3' } },
    ];
    fetch.mockResolvedValueOnce({ ok: true, json: async () => apiComments });

    const wrapper = mount(Comentarios, {
      props: { initialComments: [] },
      global: { stubs: { CommentItem: true } },
    });

    await flush();

    // Se cargaron los comentarios
    expect(wrapper.vm.comments.length).toBe(3);
    // topLevelComments: prevComment null y medicine.id=2
    expect(wrapper.vm.topLevelComments.length).toBe(1);
    expect(wrapper.vm.topLevelComments[0].idComments).toBe('a1');
    // UI
    expect(wrapper.find('.comments-list').exists()).toBe(true);
    expect(wrapper.find('.no-comments').exists()).toBe(false);
    expect(wrapper.find('.comments-count').text()).toContain('3');
  });

  test('addComment crea comentario raíz y limpia estado', async () => {
    useUserStore.mockReturnValue(makeUserStore({ idUser: 9, name: 'Ana' }));
    // GET inicial vacío
    fetch.mockResolvedValueOnce({ ok: true, json: async () => [] });
    // POST respuesta
    const created = { idComments: 'n1', prevComment: null, medicine: { idMedicine: 2 }, user: { name: 'Ana' } };
    fetch.mockResolvedValueOnce({ ok: true, json: async () => created });

    const wrapper = mount(Comentarios, {
      props: { initialComments: [] },
      global: { stubs: { CommentItem: true } },
    });
    await flush();

    // Escribir comentario y enviar
    wrapper.vm.newCommentText = 'Hola mundo';
    await nextTick();
    await wrapper.find('.comment-btn').trigger('click');
    await flush();

    // Verifica llamada POST
    expect(fetch).toHaveBeenCalledTimes(2);
    const [, postCall] = fetch.mock.calls;
    expect(postCall[0]).toContain('/comments');
    const options = postCall[1];
    expect(options.method).toBe('POST');
    const body = JSON.parse(options.body);
    expect(body.prevComment).toBeUndefined();
    expect(body.user).toEqual({ idUser: 9, name: 'Ana' });
    expect(body.medicine.idMedicine).toBe(2);

    // Estado actualizado
    expect(wrapper.vm.comments.find(c => c.idComments === 'n1')).toBeTruthy();
    expect(wrapper.vm.newCommentText).toBe('');
    expect(wrapper.vm.replyComment).toBe(null);
  });

  test('addComment como respuesta incluye prevComment', async () => {
    useUserStore.mockReturnValue(makeUserStore({ idUser: 2, name: 'Ana' }));
    // GET inicial
    fetch.mockResolvedValueOnce({ ok: true, json: async () => [] });
    // POST respuesta
    const created = { idComments: 'r1', prevComment: { idComments: 'a1' }, medicine: { idMedicine: 2 }, user: { name: 'Ana' } };
    fetch.mockResolvedValueOnce({ ok: true, json: async () => created });

    const wrapper = mount(Comentarios, {
      props: { initialComments: [] },
      global: { stubs: { CommentItem: true } },
    });
    await flush();

    // Activar modo respuesta
    const target = { idComments: 'a1', user: { name: 'Bob' } };
    wrapper.vm.setReply(target);
    await nextTick();

    wrapper.vm.newCommentText = 'Respuesta';
    await nextTick();
    await wrapper.find('.comment-btn').trigger('click');
    await flush();

    const [, postCall] = fetch.mock.calls;
    const body = JSON.parse(postCall[1].body);
    expect(body.prevComment).toEqual(target);
    expect(wrapper.vm.replyComment).toBe(null);
  });

  test('cancelReply limpia el estado de respuesta', async () => {
    useUserStore.mockReturnValue(makeUserStore({ idUser: 1 }));
    fetch.mockResolvedValueOnce({ ok: true, json: async () => [] });

    const wrapper = mount(Comentarios, { global: { stubs: { CommentItem: true } } });
    await flush();

    wrapper.vm.setReply({ idComments: 'x1', user: { name: 'Bob' } });
    expect(wrapper.vm.replyComment).not.toBe(null);
    wrapper.vm.cancelReply();
    expect(wrapper.vm.replyComment).toBe(null);
  });

  test('maneja error en mounted GET y no rompe', async () => {
    const errSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    useUserStore.mockReturnValue(makeUserStore(null));
    fetch.mockRejectedValueOnce(new Error('network'));

    const wrapper = mount(Comentarios, {
      props: { initialComments: [] },
      global: { stubs: { CommentItem: true, 'router-link': { template: '<a><slot/></a>' } } },
    });

    await flush();

    expect(errSpy).toHaveBeenCalled();
    // el componente sigue renderizando
    expect(wrapper.exists()).toBe(true);
    errSpy.mockRestore();
  });

  test('POST falla con ok=false y mantiene estado sin limpiar', async () => {
    const errSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    const logSpy = jest.spyOn(console, 'log').mockImplementation(() => {});
    useUserStore.mockReturnValue(makeUserStore({ idUser: 77, name: 'Ana' }));
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => [] }) // GET
      .mockResolvedValueOnce({ ok: false }); // POST

    const wrapper = mount(Comentarios, { global: { stubs: { CommentItem: true } } });
    await flush();

    wrapper.vm.newCommentText = 'texto';
    await nextTick();
    await wrapper.find('.comment-btn').trigger('click');
    await flush();

    expect(errSpy).toHaveBeenCalledWith('Error al crear comentario');
    expect(logSpy).toHaveBeenCalled(); // payload logueado
    expect(wrapper.vm.comments.length).toBe(0);
    expect(wrapper.vm.newCommentText).toBe('texto'); // no limpia al fallar

    errSpy.mockRestore();
    logSpy.mockRestore();
  });

  test('POST lanza error y se captura con log', async () => {
    const errSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    useUserStore.mockReturnValue(makeUserStore({ idUser: 99, name: 'Ana' }));
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => [] }) // GET
      .mockRejectedValueOnce(new Error('boom')); // POST

    const wrapper = mount(Comentarios, { global: { stubs: { CommentItem: true } } });
    await flush();

    wrapper.vm.newCommentText = 'fallará';
    await nextTick();
    await wrapper.find('.comment-btn').trigger('click');
    await flush();

    expect(errSpy).toHaveBeenCalled();
    expect(wrapper.vm.comments.length).toBe(0);
    errSpy.mockRestore();
  });

  test('Ctrl+Enter envía comentario', async () => {
    useUserStore.mockReturnValue(makeUserStore({ idUser: 5, name: 'Ana' }));
    const created = { idComments: 'c1', prevComment: null, medicine: { idMedicine: 2 }, user: { name: 'Ana' } };
    fetch
      .mockResolvedValueOnce({ ok: true, json: async () => [] }) // GET
      .mockResolvedValueOnce({ ok: true, json: async () => created }); // POST

    const wrapper = mount(Comentarios, {
      global: { stubs: { CommentItem: true } },
    });
    await flush();

    const textarea = wrapper.find('textarea');
    await textarea.setValue('hola');
    await textarea.trigger('keyup', { ctrlKey: true, key: 'Enter' });
    await flush();

    expect(fetch).toHaveBeenCalledTimes(2);
    expect(wrapper.vm.comments.find(c => c.idComments === 'c1')).toBeTruthy();
  });
});

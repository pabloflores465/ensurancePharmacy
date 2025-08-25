import { setActivePinia, createPinia } from 'pinia';
import { useUserStore } from '@/stores/userStore';

const resetStorage = () => {
  localStorage.clear();
};

describe('userStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    resetStorage();
    jest.spyOn(console, 'log').mockImplementation(() => {});
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  test('setUser sets state and localStorage (including role)', () => {
    const store = useUserStore();
    const user = { idUser: 1, name: 'Ana', role: 'admin' };

    store.setUser(user);

    expect(store.user).toEqual(user);
    expect(localStorage.getItem('session')).toEqual(JSON.stringify(user));
    expect(localStorage.getItem('user')).toEqual(JSON.stringify(user));
    expect(localStorage.getItem('role')).toEqual('admin');
  });

  test('getUser returns from state when already set', () => {
    const store = useUserStore();
    const user = { idUser: 2, name: 'Bob', role: 'user' };
    store.user = user;

    const result = store.getUser();
    expect(result).toEqual(user);
  });

  test('getUser retrieves from localStorage.session and updates state', () => {
    const store = useUserStore();
    const user = { idUser: 3, name: 'Carla', role: 'user' };
    localStorage.setItem('session', JSON.stringify(user));

    const result = store.getUser();
    expect(result).toEqual(user);
    expect(store.user).toEqual(user);
  });

  test('getUser retrieves from localStorage.user when session missing', () => {
    const store = useUserStore();
    const user = { idUser: 4, name: 'Diego', role: 'admin' };
    localStorage.setItem('user', JSON.stringify(user));

    const result = store.getUser();
    expect(result).toEqual(user);
    expect(store.user).toEqual(user);
  });

  test('getUser returns empty object when nothing stored', () => {
    const store = useUserStore();
    const result = store.getUser();
    expect(result).toEqual({});
  });

  test('isAdmin true only when role is admin', () => {
    const store = useUserStore();
    store.setUser({ idUser: 5, role: 'admin' });
    expect(store.isAdmin()).toBe(true);

    store.setUser({ idUser: 6, role: 'user' });
    expect(store.isAdmin()).toBe(false);
  });

  test('logout clears state and localStorage keys', () => {
    const store = useUserStore();
    store.setUser({ idUser: 7, name: 'Eva', role: 'user' });
    expect(store.user).not.toBeNull();

    store.logout();

    expect(store.user).toBeNull();
    expect(localStorage.getItem('session')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
  });

  test('setUser without role does not set localStorage role', () => {
    const store = useUserStore();
    const user = { idUser: 11, name: 'NoRole' };
    store.setUser(user);
    expect(localStorage.getItem('session')).toEqual(JSON.stringify(user));
    expect(localStorage.getItem('user')).toEqual(JSON.stringify(user));
    expect(localStorage.getItem('role')).toBeNull();
  });

  test('getUser handles invalid JSON in session and returns {}', () => {
    const store = useUserStore();
    const errSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    localStorage.setItem('session', '{not-json');
    const result = store.getUser();
    expect(result).toEqual({});
    expect(store.user).toBeNull();
    expect(errSpy).toHaveBeenCalled();
    errSpy.mockRestore();
  });

  test('getUser handles invalid JSON in user and returns {}', () => {
    const store = useUserStore();
    const errSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    // no session
    localStorage.removeItem('session');
    localStorage.setItem('user', '{bad');
    const result = store.getUser();
    expect(result).toEqual({});
    expect(store.user).toBeNull();
    expect(errSpy).toHaveBeenCalled();
    errSpy.mockRestore();
  });

  test('getUser ignores "undefined" and empty string values', () => {
    const store = useUserStore();
    localStorage.setItem('session', 'undefined');
    expect(store.getUser()).toEqual({});
    localStorage.setItem('session', '');
    expect(store.getUser()).toEqual({});
    // also for user key
    localStorage.removeItem('session');
    localStorage.setItem('user', 'undefined');
    expect(store.getUser()).toEqual({});
    localStorage.setItem('user', '');
    expect(store.getUser()).toEqual({});
  });

  test('isAdmin returns false when no user', () => {
    const store = useUserStore();
    expect(store.isAdmin()).toBe(false);
  });
});

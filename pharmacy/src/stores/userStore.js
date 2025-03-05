// src/stores/userStore.js
import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null, // Aquí guardaremos el objeto de usuario (role, email, etc.)
  }),
  actions: {
    // Por si más adelante necesitas un método de login dentro del store
    setUser(userData) {
      this.user = userData;
    },

    logout() {
      this.user = null;
    },
  }
});
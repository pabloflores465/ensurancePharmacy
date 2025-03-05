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
      console.log("Guardando sesión en localStorage:", userData);
      if (typeof window !== 'undefined') {
        localStorage.setItem('session', JSON.stringify(userData));
      }
    },
    getUser(){
      return this.user;
    },

    logout() {
      this.user = null;
      if (import.meta.client){
        localStorage.setItem('session', '')
      }
    },
  }
});
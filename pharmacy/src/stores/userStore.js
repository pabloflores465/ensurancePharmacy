// src/stores/userStore.js
import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: {}, // Aquí guardaremos el objeto de usuario (role, email, etc.)
  }),
  actions: {
    // Por si más adelante necesitas un método de login dentro del store
    setUser(userData) {
      this.user = userData;
      console.log("Guardando sesión en localStorage:", userData);
      if (typeof window !== "undefined") {
        localStorage.setItem("session", JSON.stringify(userData));
      }
    },
    getUser() {
      try {
        const session = localStorage.getItem("session");
        if (session) {
          return JSON.parse(session);
        }
      } catch (error) {
        console.error("Error parsing session data:", error);
      }
      return this.user;
    },

    logout() {
      this.user = {};
      if (import.meta.client) {
        localStorage.setItem("session", "");
      }
    },
  },
});

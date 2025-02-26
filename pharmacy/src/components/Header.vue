<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <header class="navbar">
    <div class="navbar-content">
      <!-- Logo -->
      <img src="@/assets/logo.png" alt="Logo" class="logo" />

      <!-- Menú de Navegación -->
      <nav class="nav-links hidden md:flex">
        <router-link to="/" class="nav-item">Inicio</router-link>
        <router-link to="/offers" class="nav-item">Ofertas</router-link>
        <router-link to="/catalogo" class="nav-item">Catálogo de Productos</router-link>
        <router-link to="/contact" class="nav-item">Contacto</router-link>
        <template v-if="isLoggedIn">
          <button @click="logout" class="login-button">Cerrar Sesión</button>
        </template>
        <template v-else>
          <router-link to="/login" class="login-button">🔑 Iniciar Sesión</router-link>
        </template>
      </nav>

      <!-- Botón de menú hamburguesa (móvil) -->
      <button @click="toggleMenu" class="menu-button md:hidden">☰</button>
    </div>

    <!-- Menú desplegable en móviles -->
    <div v-if="mobileMenuOpen" class="mobile-menu">
      <router-link to="/" class="mobile-item" @click="toggleMenu">Inicio</router-link>
      <router-link to="/offers" class="mobile-item" @click="toggleMenu">Ofertas</router-link>
      <router-link to="/catalogo" class="mobile-item" @click="toggleMenu">Catálogo de Productos</router-link>
      <router-link to="/contact" class="mobile-item" @click="toggleMenu">Contacto</router-link>
      <template v-if="isLoggedIn">
        <button @click="logout; toggleMenu()" class="mobile-login">Cerrar Sesión</button>
      </template>
      <template v-else>
        <router-link to="/login" class="mobile-login" @click="toggleMenu">🔑 Iniciar Sesión</router-link>
      </template>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';

const mobileMenuOpen = ref(false);
const toggleMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
};

const userStore = useUserStore();
const router = useRouter();
const isLoggedIn = computed(() => userStore.user !== null);

const logout = () => {
  userStore.logout();
  router.push('/');
};
</script>

<style scoped>
/* Estilos del Navbar */
.navbar {
  background: #1e40af; /* Azul fuerte */
  padding: 15px 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Contenedor del navbar */
.navbar-content {
  width: 100%;
  max-width: 1200px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Logo */
.logo {
  height: 50px;
}

/* Enlaces del menú */
.nav-links {
  display: flex;
  gap: 20px;
}

.nav-item {
  color: white;
  font-size: 16px;
  font-weight: bold;
  text-decoration: none;
  transition: color 0.3s;
}

.nav-item:hover {
  color: yellow;
}

/* Botón de iniciar sesión */
.login-button {
  background: white;
  color: #1e40af;
  padding: 8px 15px;
  border-radius: 5px;
  font-weight: bold;
  transition: background 0.3s, color 0.3s;
  text-decoration: none;
}

.login-button:hover {
  background: #f3f3f3;
}

/* Menú hamburguesa */
.menu-button {
  font-size: 24px;
  color: white;
  border: none;
  background: transparent;
  cursor: pointer;
}

/* Menú desplegable en móviles */
.mobile-menu {
  background: #1e40af;
  position: absolute;
  top: 60px;
  left: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  text-align: center;
  padding: 15px 0;
}

.mobile-item {
  color: white;
  font-size: 18px;
  padding: 10px;
  text-decoration: none;
}

.mobile-item:hover {
  background: rgba(255, 255, 255, 0.2);
}

.mobile-login {
  background: white;
  color: #1e40af;
  padding: 10px;
  text-decoration: none;
  font-weight: bold;
}
</style>
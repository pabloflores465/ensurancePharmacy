<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <header class="navbar">
    <div class="navbar-content">
      <!-- Logo -->
      <img src="@/assets/logo.png" alt="Logo" class="logo" />

      <!-- Menú de Navegación (versión desktop) -->
      <nav class="nav-links hidden md:flex">
        <router-link to="/" class="nav-item">Inicio</router-link>
        <router-link to="/ofertas" class="nav-item">Ofertas</router-link>
        <router-link to="/catalogo" class="nav-item">Catálogo de Productos</router-link>
        <router-link to="/receta" class="nav-item">Receta</router-link>
        <router-link to="/contact" class="nav-item">Contacto</router-link>

        <!-- Si el usuario está loggeado -->
        <template v-if="isLoggedIn">
          <!-- Muestra el rol -->
          <span class="logged-user-message">
            Usted está loggeado como: {{ userRole }}
          </span>
          <!-- Botón para cerrar sesión -->
          <button @click="logout" class="login-button">Cerrar Sesión</button>
        </template>

        <!-- Si NO está loggeado, muestra el botón de Iniciar Sesión -->
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
      <router-link to="/ofertas" class="mobile-item" @click="toggleMenu">Ofertas</router-link>
      <router-link to="/catalogo" class="mobile-item" @click="toggleMenu">Catálogo de Productos</router-link>
      <router-link to="/contact" class="mobile-item" @click="toggleMenu">Contacto</router-link>

      <!-- Si está loggeado, muestra rol y botón de logout en el menú móvil -->
      <template v-if="isLoggedIn">
        <span class="logged-user-message" style="color:white;">
          Usted está loggeado como: {{ userRole }}
        </span>
        <button @click="logout; toggleMenu()" class="mobile-login">Cerrar Sesión</button>
      </template>
      <!-- Si NO está loggeado, muestra botón de login en el menú móvil -->
      <template v-else>
        <router-link to="/login" class="mobile-login" @click="toggleMenu">🔑 Iniciar Sesión</router-link>
      </template>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';
import axios from 'axios';

// Control del menú móvil
const mobileMenuOpen = ref(false);
const toggleMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value;
};

// Store de usuario
const userStore = useUserStore();
const router = useRouter();

// Verifica si hay usuario loggeado
const isLoggedIn = computed(() => userStore.user !== null);

// Variable local para mostrar el rol del usuario
const userRole = ref('');

// Cuando se monta el componente, si el usuario está loggeado, llama al API para obtener el rol
onMounted(() => {
  if (isLoggedIn.value) {
    getUserRole();
  }
});

/**
 * Ejemplo de función que llama a la API para obtener el rol del usuario.
 * Ajusta la URL y la forma en que recibes el rol según tu backend.
 */
async function getUserRole() {
  try {
    // Si tu backend requiere token, agrégalo en los headers
    const response = await axios.get('http://localhost:8080/api2/getUserRole');
    // Asume que la respuesta viene como { role: "admin" } o similar
    userRole.value = response.data.role;
  } catch (error) {
    console.error('Error obteniendo rol del usuario:', error);
    // En caso de error, podrías mostrar un mensaje o manejarlo según tu lógica
  }
}

// Cerrar sesión
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
  margin-left: 1rem;
}

.login-button:hover {
  background: #f3f3f3;
}

/* Mensaje de usuario logueado */
.logged-user-message {
  color: white;
  font-weight: bold;
  margin-right: 1rem;
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
  margin-top: 0.5rem;
}
</style>
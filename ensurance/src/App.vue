<script setup lang="ts">
import { ref, provide, onMounted } from "vue";
import { useRouter } from "vue-router";
import eventBus from './eventBus';

const router = useRouter();
const isLoggedIn = ref(false);

// Función para verificar el estado de autenticación
const checkAuth = () => {
  const profile = JSON.parse(localStorage.getItem("user") || "null");
  isLoggedIn.value = profile !== null && profile !== "null";
};

// Verificar autenticación inicial
checkAuth();

// Escuchar eventos de login y logout
onMounted(() => {
  eventBus.on('login', () => {
    checkAuth();
  });
  
  eventBus.on('logout', () => {
    checkAuth();
  });
});

function logout() {
  localStorage.removeItem("user");
  isLoggedIn.value = false;
  eventBus.emit('logout');
  router.push("/login");
}
</script>
<template>
  <header
    class="flex justify-between items-center px-8 py-4 bg-gray-50 shadow-sm"
  >
    <div class="text-2xl font-bold">Ensurance</div>
    <nav class="flex gap-4">
      <div v-if="!isLoggedIn">
        <router-link
          to="/login"
          class="px-4 py-2 me-2 bg-blue-600 text-white rounded hover:bg-blue-800 transition-colors"
        >
          Login
        </router-link>
        <router-link
          to="/register"
          class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-800 transition-colors"
        >
          Register
        </router-link>
      </div>
      <div v-if="isLoggedIn">
        <router-link
          to="/home"
          class="px-4 py-2 me-2 bg-blue-600 text-white rounded hover:bg-blue-800 transition-colors"
        >
          Home
        </router-link>
        <button
          @click="logout"
          class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-800 transition-colors"
        >
          Logout
        </button>
      </div>
    </nav>
  </header>
  <main class="min-h-[calc(100vh-136px)] p-4">
    <RouterView />
  </main>
  <footer
    class="py-4 px-8 text-center bg-gray-50 mt-auto border-t border-gray-200"
  >
    <p>&copy; 2025 Ensurance. All rights reserved and lefts too.</p>
  </footer>
</template>

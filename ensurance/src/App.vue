<script setup lang="ts">
import { ref, provide, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import eventBus from './eventBus';

const router = useRouter();
const isLoggedIn = ref(false);
const userProfile = ref<any>(null);


const checkAuth = () => {
  const profile = JSON.parse(localStorage.getItem("user") || "null");
  isLoggedIn.value = profile !== null && profile !== "null";
  userProfile.value = profile;
  console.log("Usuario:", profile);
  console.log("Es admin:", isAdmin.value);
};

// Computed para verificar si el usuario es administrador
const isAdmin = computed(() => {
  return userProfile.value && userProfile.value.role === "admin";
});

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
  userProfile.value = null;
  eventBus.emit('logout');
  router.push("/login");
}

function navigateToUsers() {
  router.push("/admin/users");
}

function navigateToServices() {
  router.push("/admin/insurance-services");
}

function navigateToHospitals() {
  router.push("/admin/hospital-services");
}

function navigateToInsuranceServicesCatalog() {
  router.push("/catalog/insurance-services");
}

function navigateToHospitalsCatalog() {
  router.push("/catalog/hospitals");
}

function navigateToPolicies() {
  router.push("/admin/policies");
}

function navigateToRegisterClient() {
  router.push("/employee/register-client");
}

// Computed para verificar si el usuario es empleado
const isEmployee = computed(() => {
  return userProfile.value && userProfile.value.role === "employee";
});
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
      <div v-if="isLoggedIn" class="flex items-center">
        <router-link
          to="/home"
          class="px-4 py-2 me-2 bg-blue-600 text-white rounded hover:bg-blue-800 transition-colors"
        >
          Home
        </router-link>
        
        <!-- Menú de catálogos para todos los usuarios -->
        <div class="relative mx-2 group">
          <button class="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-800 transition-colors flex items-center">
            Catálogos ▼
          </button>
          <div class="absolute hidden group-hover:block bg-white mt-2 py-2 rounded shadow-lg z-10 w-64 right-0">
            <button 
              @click="navigateToInsuranceServicesCatalog" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Servicios Cubiertos
            </button>
            <button 
              @click="navigateToHospitalsCatalog" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Hospitales Aprobados
            </button>
          </div>
        </div>
        
        <!-- Menú de empleado -->
        <div v-if="isEmployee || isAdmin" class="relative mx-2 group">
          <button class="px-4 py-2 bg-teal-600 text-white rounded hover:bg-teal-800 transition-colors flex items-center">
            Clientes ▼
          </button>
          <div class="absolute hidden group-hover:block bg-white mt-2 py-2 rounded shadow-lg z-10 w-64 right-0">
            <button 
              @click="navigateToRegisterClient" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Registrar Nuevo Cliente
            </button>
          </div>
        </div>
        
        <!-- Menú de administración para admins -->
        <div v-if="isAdmin" class="relative mx-2 group">
          <button class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-800 transition-colors flex items-center">
            Admin ▼
          </button>
          <div class="absolute hidden group-hover:block bg-white mt-2 py-2 rounded shadow-lg z-10 w-64 right-0">
            <button 
              @click="navigateToUsers" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Gestión de Usuarios
            </button>
            <button 
              @click="navigateToServices" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Catálogo de Servicios
            </button>
            <button 
              @click="navigateToHospitals" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Hospitales y Servicios
            </button>
            <button 
              @click="navigateToPolicies" 
              class="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-100"
            >
              Gestión de Pólizas
            </button>
          </div>
        </div>
        
        <button
          @click="logout"
          class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-800 transition-colors"
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

<style>
.group:hover .group-hover\:block {
  display: block;
}
</style>

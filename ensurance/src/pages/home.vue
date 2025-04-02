<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const user = ref<any>(null);
const isAdmin = ref(false);

onMounted(() => {
  // Obtener datos de usuario del localStorage
  const userData = localStorage.getItem("user");
  if (userData) {
    try {
      user.value = JSON.parse(userData);
      isAdmin.value = user.value.role === "ADMIN";
    } catch (e) {
      console.error("Error al parsear datos de usuario:", e);
    }
  }
});
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold mb-6">Bienvenido a Ensurance</h1>
    
    <div v-if="user" class="bg-white shadow-md rounded-lg p-6 mb-8">
      <h2 class="text-xl font-semibold mb-2">{{ user.name }}</h2>
      <p class="text-gray-600 mb-1">Email: {{ user.email }}</p>
      <p class="text-gray-600 mb-1">Rol: {{ user.role || 'Sin rol asignado' }}</p>
    </div>
    
    <!-- Catálogos disponibles para todos los usuarios -->
    <div class="bg-white shadow-md rounded-lg p-6 mb-8">
      <h2 class="text-xl font-semibold mb-4">Catálogos de Seguros</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div 
          @click="router.push('/catalog/insurance-services')" 
          class="bg-blue-50 p-4 rounded-lg cursor-pointer hover:bg-blue-100 transition-colors"
        >
          <h3 class="font-semibold mb-2">Servicios Cubiertos</h3>
          <p class="text-sm text-gray-600">Consulta todos los servicios cubiertos por el seguro y sus precios</p>
        </div>
        
        <div 
          @click="router.push('/catalog/hospitals')" 
          class="bg-green-50 p-4 rounded-lg cursor-pointer hover:bg-green-100 transition-colors"
        >
          <h3 class="font-semibold mb-2">Hospitales Aprobados</h3>
          <p class="text-sm text-gray-600">Consulta los hospitales que tienen convenio con el seguro</p>
        </div>
      </div>
    </div>
    
    <div v-if="isAdmin" class="bg-white shadow-md rounded-lg p-6 mb-8">
      <h2 class="text-xl font-semibold mb-4">Panel de Administración</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div 
          @click="router.push('/admin/users')" 
          class="bg-blue-100 p-4 rounded-lg cursor-pointer hover:bg-blue-200 transition-colors"
        >
          <h3 class="font-semibold mb-2">Gestión de Usuarios</h3>
          <p class="text-sm text-gray-600">Administrar usuarios, asignar roles y activar cuentas</p>
        </div>
        
        <div 
          @click="router.push('/admin/insurance-services')" 
          class="bg-green-100 p-4 rounded-lg cursor-pointer hover:bg-green-200 transition-colors"
        >
          <h3 class="font-semibold mb-2">Catálogo de Servicios</h3>
          <p class="text-sm text-gray-600">Gestionar servicios cubiertos por el seguro</p>
        </div>
        
        <div 
          @click="router.push('/admin/hospital-services')" 
          class="bg-purple-100 p-4 rounded-lg cursor-pointer hover:bg-purple-200 transition-colors"
        >
          <h3 class="font-semibold mb-2">Hospitales y Servicios</h3>
          <p class="text-sm text-gray-600">Aprobar servicios para hospitales específicos</p>
        </div>
      </div>
    </div>
  </div>
</template>

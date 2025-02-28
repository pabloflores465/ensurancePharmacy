<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="login-container">
    <!-- Sección izquierda con imagen de fondo -->
    <div class="login-image"></div>

    <!-- Sección derecha con formulario -->
    <div class="login-box">
      <div class="logo-container">
        <img src="@/assets/logo.png" alt="Farmacia Logo" class="logo" />
      </div>

      <h2 class="text-2xl font-bold text-center text-blue-800 mb-4">Iniciar Sesión</h2>

      <!-- Selector de tipo de usuario -->
      <div class="mb-4">
        <label class="block text-gray-700">Selecciona tu rol:</label>
        <select v-model="role" class="w-full p-2 border rounded-lg">
          <option value="user">Usuario</option>
          <option value="employee">Empleado</option>
          <option value="admin">Administrador</option>
        </select>
      </div>

      <!-- Formulario de inicio de sesión -->
      <form @submit.prevent="login">
        <!-- Para usuarios y empleados -->
        <div v-if="role === 'user' || role === 'employee'" class="mb-4">
          <label class="block text-gray-700">
            {{ role === 'user' ? "Número de Usuario" : "Número de Empleado" }}
          </label>
          <div class="input-group">
            <input v-model="identifier" type="text" class="input-field" required />
            <span class="icon">👤</span>
          </div>
          <div class="mb-4">
            <label class="block text-gray-700">Contraseña</label>
            <div class="input-group">
              <input v-model="adminPassword" type="password" class="input-field" required />
              <span class="icon">🔒</span>
            </div>
          </div>
        </div>
              <!-- Botón de inicio de sesión -->
              <button type="submit" class="login-button">
          Iniciar sesión →
        </button>
      </form>
      <!-- Enlace para registrarse -->
      <div class="text-center mt-4">
        <router-link to="/register" class="text-blue-500 hover:underline">Regístrarse aquí</router-link>
      </div>
      
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
const ip = process.env.VUE_APP_IP;
console.log("tu puta madre " + ip);

// Manejo de rutas para la imagen de fondo


const role = ref('user'); 
const identifier = ref('');
const adminUsername = ref('');
const adminPassword = ref('');
const errorMessage = ref('');
const router = useRouter();

const login = async () => {
  errorMessage.value = '';

  if (role.value === 'user' || role.value === 'employee') {
    if (identifier.value.trim() === '') {
      errorMessage.value = "El número de usuario/empleado es obligatorio.";
      return;
    }
    console.log(`Autenticando ${role.value} con ID: ${identifier.value}`);
    router.push('/');

  } else if (role.value === 'admin') {
    if (adminUsername.value.trim() === '' || adminPassword.value.trim() === '') {
      errorMessage.value = "Usuario y contraseña son obligatorios.";
      return;
    }
    if (adminUsername.value === 'admin' && adminPassword.value === '123') {
      console.log("Administrador autenticado");
      router.push('/'); // Redirigir al inicio de la plataforma
    } else {
      errorMessage.value = "Credenciales incorrectas.";
    }
  }
};
</script>

<style scoped>
/* Diseño de dos columnas */
.login-container {
  display: flex;
  height: 100vh;
  background-color: #f8f9fa;
}

/* Sección izquierda con imagen de fondo */
.login-image {
  flex: 1;
  background-image: url(@/assets/farmacialog.jpg);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Sección derecha (Formulario) */
.login-box {
  flex: 1;
  background: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 50px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border-radius: 15px;
}

/* Logo */
.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.logo {
  max-width: 150px;
}

/* Inputs estilizados */
.input-group {
  position: relative;
  width: 100%;
}
.input-field {
  width: 100%;
  padding: 12px 40px 12px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 16px;
}
.icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
}

/* Botón estilizado */
.login-button {
  width: 100%;
  background: #1e40af;
  color: white;
  padding: 12px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
}
.login-button:hover {
  background: #1e3a8a;
}

/* Responsivo */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  .login-image {
    display: none; /* Ocultar imagen en pantallas pequeñas */
  }
}
</style>
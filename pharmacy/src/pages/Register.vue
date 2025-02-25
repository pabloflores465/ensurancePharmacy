<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="register-container">
    <!-- Sección izquierda con imagen de fondo -->
    <div class="register-image"></div>

    <!-- Sección derecha con el formulario -->
    <div class="register-box">
      <h1 class="text-2xl font-bold text-center text-blue-800 mb-4">Registro</h1>
      <img src="@/assets/logo.png" alt="Pharmacy Registration" class="logo" />

      <form @submit.prevent="register">
        <div class="form-group">
          <label for="name">Nombre</label>
          <div class="input-group">
            <input type="text" id="name" v-model="name" required />
            <span class="icon">👤</span>
          </div>
        </div>

        <div class="form-group">
          <label for="email">Correo Electrónico</label>
          <div class="input-group">
            <input type="email" id="email" v-model="email" required />
            <span class="icon">📧</span>
          </div>
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <div class="input-group">
            <input type="password" id="password" v-model="password" required />
            <span class="icon">🔒</span>
          </div>
        </div>

        <!-- Botón de registro -->
        <button type="submit" class="register-button">
          Registrarse →
        </button>

        <!-- Redirección a Login -->
        <p class="text-center text-sm mt-4">
          ¿Ya tienes una cuenta?
          <a @click="goToLogin" class="text-blue-600 hover:underline cursor-pointer">Iniciar sesión</a>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const name = ref('');
const email = ref('');
const password = ref('');

const register = async () => {
  console.log('Registrando:', name.value, email.value, password.value);
  // Aquí puedes manejar la lógica del registro
  // Después de registrar, redirigir al login
  router.push('/login');
};

const goToLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
/* Contenedor principal */
.register-container {
  display: flex;
  height: 100vh;
  background-color: #f8f9fa;
}

/* Sección izquierda con imagen */
.register-image {
  flex: 1;
  background: url('@/assets/farmaciareg.jpeg') no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Sección derecha con el formulario */
.register-box {
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
.logo {
  max-width: 120px;
  margin-bottom: 20px;
}

/* Formulario */
form {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 400px;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

/* Campos de entrada */
.form-group {
  margin-bottom: 15px;
}

label {
  font-weight: bold;
  color: #495057;
}

/* Input con iconos */
.input-group {
  position: relative;
  width: 100%;
}

input {
  width: 100%;
  padding: 12px 40px 12px 12px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  font-size: 16px;
}

.icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #6c757d;
}

/* Botón de registro */
.register-button {
  width: 100%;
  background: #1e40af;
  color: white;
  padding: 12px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
  text-align: center;
}

.register-button:hover {
  background: #1e3a8a;
}

/* Responsivo */
@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
  }
  .register-image {
    display: none;
  }
}
</style>
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
        <!-- Nombre -->
        <div class="form-group">
          <label for="name">Nombre</label>
          <div class="input-group">
            <input type="text" id="name" v-model="name" required />
            <span class="icon">👤</span>
          </div>
        </div>

        <!-- CUI -->
        <div class="form-group">
          <label for="cui">CUI</label>
          <div class="input-group">
            <input type="text" id="cui" v-model="cui" required />
            <span class="icon">🆔</span>
          </div>
        </div>

        <!-- Teléfono -->
        <div class="form-group">
          <label for="phone">Teléfono</label>
          <div class="input-group">
            <input type="tel" id="phone" v-model="phone" required />
            <span class="icon">☎️</span>
          </div>
        </div>

        <!-- Correo Electrónico -->
        <div class="form-group">
          <label for="email">Correo Electrónico</label>
          <div class="input-group">
            <input type="email" id="email" v-model="email" required />
            <span class="icon">📧</span>
          </div>
        </div>

        <!-- Dirección -->
        <div class="form-group">
          <label for="address">Dirección</label>
          <div class="input-group">
            <input type="text" id="address" v-model="address" required />
            <span class="icon">📍</span>
          </div>
        </div>

        <!-- Fecha de Nacimiento -->
        <div class="form-group">
          <label for="birthdate">Fecha de Nacimiento</label>
          <div class="input-group">
            <input type="date" id="birthdate" v-model="birthdate" required />
            <span class="icon">📅</span>
          </div>
        </div>



        <!-- ID_POLICY (puede ser un número o texto según tu lógica) -->
        <div class="form-group">
          <label for="idPolicy">ID de Póliza</label>
          <div class="input-group">
            <input type="text" id="idPolicy" v-model="idPolicy" />
            <span class="icon">📑</span>
          </div>
        </div>

 

        <!-- Contraseña -->
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

// Campos
const name = ref('');
const cui = ref('');
const phone = ref('');
const email = ref('');
const address = ref('');
const birthdate = ref('');
const role = ref('');
const idPolicy = ref('');
const enabled = ref(true);
const password = ref('');

// Manejo del registro
const register = async () => {
  // Aquí puedes manejar la lógica del registro
  // por ejemplo, llamar a tu API enviando estos datos

  console.log('Registrando:', {
    name: name.value,
    cui: cui.value,
    phone: phone.value,
    email: email.value,
    address: address.value,
    birthdate: birthdate.value,
    role: role.value,
    idPolicy: idPolicy.value,
    enabled: enabled.value,
    password: password.value
  });

  // Después de registrar, redirigir al login
  router.push('/login');
};

// Función para ir a la pantalla de login
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

input, select {
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
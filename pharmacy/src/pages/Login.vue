<!-- src/pages/LoginPage.vue -->
<template>
  <div class="login-container">
    <form @submit.prevent="handleLogin" class="login-form">
      <h2>Iniciar Sesión</h2>
      
      <div class="form-group">
        <input
          v-model="username"
          type="text"
          placeholder="Usuario"
          required
          :class="{ 'error': errors.username }"
        />
        <span v-if="errors.username" class="error-message">{{ errors.username }}</span>
      </div>

      <div class="form-group">
        <input
          v-model="password"
          type="password"
          placeholder="Contraseña"
          required
          :class="{ 'error': errors.password }"
        />
        <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
      </div>

      <div v-if="errorMessage" class="alert alert-danger">
        {{ errorMessage }}
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? 'Cargando...' : 'Iniciar Sesión' }}
      </button>
    </form>
  </div>
</template>

<script>
import { authService } from '@/services/authService';

export default {
  name: 'LoginPage', // Nombre multi-palabra para cumplir con ESLint
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      errorMessage: '',
      errors: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    validateForm() {
      let isValid = true;
      this.errors = { username: '', password: '' };

      if (!this.username) {
        this.errors.username = 'El usuario es requerido';
        isValid = false;
      }

// Ejemplo: permitir contraseñas de 3 caracteres
if (!this.password) {
  this.errors.password = 'La contraseña es requerida';
  isValid = false;
} else if (this.password.length < 3) {
  this.errors.password = 'La contraseña debe tener al menos 3 caracteres';
  isValid = false;
}

      return isValid;
    },

    async handleLogin() {
      if (!this.validateForm()) return;

      this.loading = true;
      this.errorMessage = '';

      try {
        // Se realiza la conexión a la API enviando username y password
        await authService.login({
          username: this.username,
          password: this.password
        });
        // Si el login es exitoso, redirige al dashboard u otra ruta
        this.$router.push('/dashboard');
      } catch (error) {
        this.errorMessage = 'Usuario o contraseña incorrectos';
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.login-form {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 5px;
}

input.error {
  border-color: #dc3545;
}

.error-message {
  color: #dc3545;
  font-size: 0.8em;
}

.alert {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 4px;
}

.alert-danger {
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
}

button {
  width: 100%;
  padding: 10px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
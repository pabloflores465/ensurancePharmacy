<script setup lang="ts">
import { ref } from "vue";
import axios from "axios";

defineProps<{ msg: string }>();

interface LoginResponse {
  user: {
    id: number;
    name: string;
    email: string;
    role: string;
  };
  success: boolean;
  message: string;
}

const email = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");
const user = ref<LoginResponse["user"] | null>(null);

const login = async () => {
  try {
    loading.value = true;
    error.value = "";

    const response = await axios.post("http://192.168.1.21:8080/api/login", {
      email: email.value,
      password: password.value,
    });

    const data: LoginResponse = response.data;

    if (data.success) {
      user.value = data.user;
    } else {
      error.value = data.message || "Login failed";
    }
  } catch (err: any) {
    error.value =
      err.response?.data?.message || "Connection error. Please try again.";
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <h1 class="text-2xl font-bold mb-6">{{ msg }}</h1>

  <div
    v-if="!user"
    class="card bg-white shadow-md rounded-lg p-6 max-w-md mx-auto"
  >
    <h2 class="text-xl font-semibold mb-4">Login</h2>

    <form @submit.prevent="login" class="space-y-4">
      <div>
        <label for="email" class="block text-sm font-medium text-gray-700"
          >Email</label
        >
        <input
          id="email"
          v-model="email"
          type="email"
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
          placeholder="your@email.com"
        />
      </div>

      <div>
        <label for="password" class="block text-sm font-medium text-gray-700"
          >Password</label
        >
        <input
          id="password"
          v-model="password"
          type="password"
          required
          class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
        />
      </div>

      <div v-if="error" class="text-red-500 text-sm">{{ error }}</div>

      <button
        type="submit"
        class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        :disabled="loading"
      >
        <span v-if="loading">Logging in...</span>
        <span v-else>Login</span>
      </button>
    </form>
  </div>

  <div v-else class="card bg-white shadow-md rounded-lg p-6 max-w-md mx-auto">
    <h2 class="text-xl font-semibold mb-4">Welcome, {{ user.name }}!</h2>
    <p class="mb-2"><strong>Email:</strong> {{ user.email }}</p>
    <p class="mb-4"><strong>Role:</strong> {{ user.role }}</p>
    <button
      @click="user = null"
      class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
    >
      Logout
    </button>
  </div>
</template>

<style scoped>
.card {
  @apply transition-all duration-300;
}
</style>

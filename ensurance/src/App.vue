<script setup lang="ts">
import { ref } from "vue";

const isLoggedIn = ref(false);
const profile = JSON.parse(localStorage.getItem("user") || "null");
console.log(profile);
if (profile !== null) {
  isLoggedIn.value = true;
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
    </nav>
  </header>
  <main class="min-h-[calc(100vh-136px)] p-4">
    <RouterView />
  </main>
  <footer
    class="py-4 px-8 text-center bg-gray-50 mt-auto border-t border-gray-200"
  >
    <p>&copy; 2023 Ensurance. All rights reserved.</p>
  </footer>
</template>

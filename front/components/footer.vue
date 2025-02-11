<script setup>
const links = [
  { name: "About Us", href: "/about" },
  { name: "Services", href: "/services" },
  { name: "Contact", href: "/contact" },
  { name: "Privacy Policy", href: "/privacy" },
];

const currentYear = new Date().getFullYear();
</script>
<template>
  <footer class="bg-gray-900 py-8 text-gray-200">
    <div
      class="container mx-auto flex flex-col items-center justify-between px-4 md:flex-row"
    >
      <div class="mb-4 md:mb-0">
        <h1 class="text-xl font-bold">My Insurance S.A.</h1>
        <p class="text-sm">
          &copy; {{ currentYear }} My Website. All rights reserved.
        </p>
      </div>
      <nav>
        <ul class="flex flex-nowrap space-x-6">
          <li class="text-center" v-for="link in links" :key="link.href">
            <a
              :href="link.href"
              class="transition-colors duration-200 hover:text-white"
            >
              {{ link.name }}
            </a>
          </li>
        </ul>
      </nav>
    </div>
  </footer>
</template>

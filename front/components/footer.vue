<script setup lang="ts">
import { darkMode } from "~/composables/darkMode.ts";

const links = [
  { name: "About Us", href: "/about" },
  { name: "Services", href: "/services" },
  { name: "Contact", href: "/contact" },
  { name: "Privacy Policy", href: "/privacy" },
];

const currentYear = new Date().getFullYear();

const dark = darkMode();
</script>
<template>
  <footer :class="['bg-background py-8', dark ? 'dark' : '']">
    <section
      class="text-primary hover:text-h-primary container mx-auto flex flex-col items-center justify-between px-4 md:flex-row"
    >
      <div class="mb-4 md:mb-0">
        <h1 class="text-primary flex justify-center text-xl font-bold">
          My Insurance S.A.
        </h1>
        <p class="text-primary hover:text-h-secondary text-sm">
          &copy; {{ currentYear }} My Website. All rights reserved.
        </p>
      </div>
      <nav>
        <ul class="flex flex-nowrap space-x-6">
          <li
            class="hover:text-h-accent text-center"
            v-for="link in links"
            :key="link.href"
          >
            <a
              :href="link.href"
              class="text-primary hover:text-accent transition-colors duration-200"
            >
              {{ link.name }}
            </a>
          </li>
        </ul>
      </nav>
    </section>
  </footer>
</template>

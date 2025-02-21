<script setup lang="ts">
const links = [
  { name: "About Us", href: "/about" },
  { name: "Services", href: "/services" },
  { name: "Contact", href: "/contact" },
  { name: "Privacy Policy", href: "/privacy" },
];

const edit = useEdit();

const currentYear = new Date().getFullYear();
</script>
<template>
  <footer v-if="!edit" class="bg-background py-8">
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

  <footer v-if="edit" class="bg-background py-8">
    <section class="responsive-grid mx-8">
      <div class="mb-8">
        <span class="text-primary font-semibold">Title</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Rights</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">About Us</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Services</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Contact</span>
        <input type="text" class="field" />
      </div>
      <div class="mb-8">
        <span class="text-primary font-semibold">Privacy</span>
        <input type="text" class="field" />
      </div>
    </section>
  </footer>
</template>

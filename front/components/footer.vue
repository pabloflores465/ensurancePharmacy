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
  <footer class="bg-[var(--primary-color)] py-8 text-[var(--second)]">
    <section
      class="text-primary hover:text-h-primary container mx-auto flex flex-col items-center justify-between px-4 md:flex-row"
    >
      <div class="mb-4 md:mb-0">
        <h1 class="text-xl font-bold ">My Insurance S.A.</h1>
        <p class="text-sm hover:text-[var(--hover-second)]">
          &copy; {{ currentYear }} My Website. All rights reserved.
        </p>
      </div>
      <nav>
        <ul class="flex flex-nowrap space-x-6">
          <li class="text-center hover:text-h-accent" v-for="link in links" :key="link.href">
            <a
              :href="link.href"
              class="transition-colors duration-200 hover:text-[var(--hover-accent)]"
            >
              {{ link.name }}
            </a>
          </li>
        </ul>
      </nav>
    </section>
  </footer>
</template>

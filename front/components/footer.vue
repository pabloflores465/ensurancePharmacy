<template>
    <footer class="bg-gray-900 text-gray-200 py-8">
      <div class="container mx-auto px-4 flex flex-col md:flex-row justify-between items-center">
        <div class="mb-4 md:mb-0">
          <h1 class="text-xl font-bold">My Insurance S.A.</h1>
          <p class="text-sm">&copy; {{ currentYear }} My Website. All rights reserved.</p>
        </div>
        <nav>
          <ul class="flex space-x-6">
            <li v-for="link in links" :key="link.href">
              <a :href="link.href" class="hover:text-white transition-colors duration-200">
                {{ link.name }}
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </footer>
  </template>
  
  <script setup>
  const links = [
    { name: 'About Us', href: '/about' },
    { name: 'Services', href: '/services' },
    { name: 'Contact', href: '/contact' },
    { name: 'Privacy Policy', href: '/privacy' },
  ];
  
  const currentYear = new Date().getFullYear();
  </script>
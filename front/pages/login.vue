<template>
  <div
      class="bg-[url('/sign.jpg')] bg-cover bg-center w-full flex justify-center items-center"
  >
    <div
        class="bg-[var(--primary-color)]/80 backdrop-blur-md rounded-lg shadow-xl py-6 px-8 m-2 max-w-md w-full"
    >
      <h2 class="text-2xl font-bold text-white mb-8 text-center">
        Hi, Please Enter Your Credentials
      </h2>
      <form>
        <div class="mb-6">
          <label for="email" class="block text-md font-medium text-white"
          >E-Mail</label
          >
          <input
              type="email"
              id="email"
              required
              placeholder="email@example.com"
              class="block w-full text-white outline-0 border-b-2 border-b-gray-200 hover:border-b-[var(--hover-color)] py-2"
          />
        </div>
        <div class="mb-6">
          <label for="password" class="block text-md font-medium text-white"
          >Password</label
          >
          <input
              type="password"
              id="password"
              required
              placeholder="********"
              class="block w-full border-b-2 outline-0 text-white border-b-gray-200 hover:border-b-[var(--hover-color)] py-2"
          />
        </div>
        <button class="button flex mx-auto justify-center text-[#121827]">
          <svg
              class="me-2"
              xmlns="http://www.w3.org/2000/svg"
              height="24px"
              viewBox="0 -960 960 960"
              width="24px"
              fill="--primary-color"
          >
            <path
                d="m424-296 282-282-56-56-226 226-114-114-56 56 170 170Zm56 216q-83 0-156-31.5T197-197q-54-54-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T763-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Zm0-80q134 0 227-93t93-227q0-134-93-227t-227-93q-134 0-227 93t-93 227q0 134 93 227t227 93Zm0-320Z"
            />
          </svg>
          Complete
        </button>
      </form>
    </div>
  </div>
</template>
<script setup lang="ts">
</script>
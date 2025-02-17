<script setup lang="ts">
const dark = darkMode();
</script>
<template>
  <main
    :class="[
      'bg-image-[url(/sign.jpg)] flex w-full items-center justify-center',
      dark ? 'dark' : '',
    ]"
  >
    <section class="card min-w-[80%]">
      <h2 class="title mb-6">Hi, please Sign Up</h2>
      <form>
        <label for="nombre" class="label">Name</label>
        <input
          type="text"
          id="nombre"
          required
          placeholder="Name"
          class="field mb-6"
        />
        <label for="email" class="label">E-Mail</label>
        <input
          type="email"
          id="email"
          placeholder="email@example.com"
          required
          class="field mb-6"
        />
        <label for="password" class="label">Password</label>
        <input
          type="password"
          id="password"
          required
          placeholder="********"
          class="field mb-6"
        />
        <button class="btn flex">
          <svg
            class="me-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="--color-primary"
          >
            <path
              d="m424-296 282-282-56-56-226 226-114-114-56 56 170 170Zm56 216q-83 0-156-31.5T197-197q-54-54-85.5-127T80-480q0-83 31.5-156T197-763q54-54 127-85.5T480-880q83 0 156 31.5T763-763q54 54 85.5 127T880-480q0 83-31.5 156T763-197q-54 54-127 85.5T480-80Zm0-80q134 0 227-93t93-227q0-134-93-227t-227-93q-134 0-227 93t-93 227q0 134 93 227t227 93Zm0-320Z"
            />
          </svg>
          Complete
        </button>
      </form>
    </section>
  </main>
</template>

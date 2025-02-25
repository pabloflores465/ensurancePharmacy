<script setup lang="ts">
const changes = useChanges();
</script>

<template>
  <div class="from-s-background to-sh-background h-full w-full bg-linear-65">
    <div class="responsive-grid">
      <div v-for="change in changes" class="max-sm:flex-col md:flex">
        <div class="card outline-error/30 mb-6 outline-4">
          <div
            v-for="(key, index) in Object.keys(change.oldValue)"
            class="label mb-4 flex"
          >
            <div class="me-2 font-semibold">
              {{ change.fieldNames[index] }}
            </div>
            {{ change.oldValue[key] }}
          </div>
        </div>
        <svg
          class="text-primary flex w-full justify-center"
          xmlns="http://www.w3.org/2000/svg"
          height="60px"
          viewBox="0 -960 960 960"
          width="40px"
          fill="currentColor"
        >
          <path
            d="M440-800v487L216-537l-56 57 320 320 320-320-56-57-224 224v-487h-80Z"
          />
        </svg>
        <div class="card outline-success/30 mb-6 outline-4">
          <div v-for="value in change.newValue" class="label mb-4">
            {{ value }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

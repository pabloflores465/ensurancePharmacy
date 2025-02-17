<script setup lang="ts">
const notifications: Ref<notification[]> = useNotifications();
</script>

<template>
  <section class="fixed top-4 right-4 space-y-4">
    <div
      v-for="(notification, index) in notifications"
      class="bg-background appear-short rounded-lg"
      :key="index"
    >
      <h2
        :class="[
          'bg-h-background mb-2 flex rounded-t-lg border-b-2 px-4 pt-2 pb-2 text-lg font-bold',
          notification.type === 'loading'
            ? 'text-accent hover:text-h-accent'
            : '',
          notification.type === 'success'
            ? 'text-success hover:text-h-success'
            : '',
          notification.type === 'error' ? 'text-error hover:text-h-error' : '',
        ]"
      >
        {{ notification.title }}
      </h2>
      <p class="text-secondary hover:text-h-secondary px-4 pb-2">
        {{ notification.description }}
      </p>
    </div>
  </section>
</template>

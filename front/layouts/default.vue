<script setup lang="ts">
const toggleSidebar = useToggleSidebar()
</script>
<template>
    <div class="grid min-h-[100dvh] grid-rows-[1fr_auto]">
        <div class="flex w-full h-full">
            <Sidebar/>
            <div :class="toggleSidebar ? 'max-sm:hidden flex w-full h-full appear':'flex w-full h-full appear'">
              <slot ></slot>
            </div>
        </div>
        <Footer/>
    </div>
</template>
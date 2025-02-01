<script setup lang="ts">
withDefaults(
  defineProps<{
    styles?: string;
    label?: string;
    layout?: string;
    onchange?: (event: Event) => void;
    disabled?: boolean;
    checked?: boolean;
    reverse?: boolean;
  }>(),
  {
    styles: "",
    label: "Click me",
    layout: "",
    onchange: () => {},
    disabled: false,
    checked: false,
    reverse: false,
  }
);
</script>

<template>
  <div :class="['inline-flex items-center', layout]">
    <span
      v-if="reverse && label"
      class="mr-2 text-base text-[#212529] select-none"
    >
      <strong>{{ label }}</strong>
    </span>
    <label class="relative inline-flex items-center gap-2 cursor-pointer">
      <input
        :checked="checked"
        type="checkbox"
        :disabled="disabled"
        @change="onchange"
        class="peer absolute w-0 h-0 opacity-0"
      />
      <span
        :class="[
          `relative inline-block w-10 h-5 bg-gray-300 border-2 border-gray-400 rounded-full box-border transition-all duration-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-200 before:content-[''] before:absolute before:top-[1px] before:left-[1px] before:w-[14px] before:h-[14px] before:bg-white before:rounded-full before:transition-transform before:duration-200 peer-checked:bg-blue-500 peer-checked:border-blue-500 peer-checked:before:translate-x-[20px] peer-disabled:bg-gray-200 peer-disabled:border-gray-300`,
          styles]
        "
      ></span>
      <span
        v-if="label && !reverse"
        class="inline-flex items-center text-base text-[#212529] select-none"
      >
        <strong>{{ label }}</strong>
      </span>
    </label>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from "vue";

const props = defineProps({
  modelValue: String,
  label: String,
  id: String,
  type: { type: String, default: "text" },
  placeholder: String,
  required: { type: Boolean, default: false },
  errorMessage: { type: String, default: "Por favor, llena este campo" },
});

const emit = defineEmits(["update:modelValue"]);
const inputValue = ref(props.modelValue);
const showTooltip = ref(false);

const validateInput = () => {
  if (props.required && !inputValue.value) {
    showTooltip.value = true;
  }
};

const hideTooltip = () => {
  showTooltip.value = false;
};

</script>

<template>
  <div class="relative w-full">
    <label :for="id" class="block text-sm font-semibold text-gray-700">{{ label }}</label>
    <input
        :id="id"
        v-model="inputValue"
        :type="type"
        :placeholder="placeholder"
        class="mt-1 block w-full rounded-md border border-gray-300 p-2 text-gray-900 focus:border-blue-500 focus:ring-blue-500"
        :class="{ 'border-red-500': showTooltip }"
        @blur="validateInput"
        @input="hideTooltip"
    />
    <transition name="fade">
      <div
          v-if="showTooltip"
          class="absolute left-0 top-full mt-1 w-full bg-red-500 text-white text-xs p-2 rounded-md shadow-md"
      >
        {{ errorMessage }}
      </div>
    </transition>
  </div>
</template>
<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
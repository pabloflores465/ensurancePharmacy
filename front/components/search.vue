<script setup lang="ts">
interface Props {
  fieldNames: string[];
  searchFields: (boolean | string | number)[];
  output: object[];
}
const props = defineProps<Props>();
const emit = defineEmits(["update:output"]);
const checkedFields = ref<boolean[]>([]);
const textSearchFields: string[] = [];
const searchValue = ref<string>("");
const initialValue = props.output;

onMounted(() => {
  props.searchFields.forEach((element) => {
    checkedFields.value.push(false);
    textSearchFields.push(element.toString());
  });
});

watch([searchValue, checkedFields], () => {
  if (!searchValue.value) {
    emit("update:output", initialValue);
    return;
  }

  const activeFields = textSearchFields.filter(
    (_, i) => checkedFields.value[i],
  );

  if (activeFields.length === 0) {
    const filtered = props.output.filter((item) =>
      Object.values(item).some((val) =>
        val?.toString().toLowerCase().includes(searchValue.value.toLowerCase()),
      ),
    );
    emit("update:output", filtered);
    return;
  }

  const filtered = props.output.filter((item) =>
    activeFields.some((field) =>
      item[field]
        ?.toString()
        .toLowerCase()
        .includes(searchValue.value.toLowerCase()),
    ),
  );
  emit("update:output", filtered);
});

function updateSearchValue(event: Event) {
  const target = event.target as HTMLInputElement;
  searchValue.value = target.value;
}
</script>

<template>
  <div class="bg-background px-4 pt-2 pb-4">
    <div
      class="border-primary flex w-full flex-row items-center rounded-full border-2 border-solid bg-white px-2 py-1"
    >
      <svg
        stroke="currentColor"
        fill="currentColor"
        stroke-width="0"
        viewBox="0 0 512 512"
        height="30px"
        width="30px"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M505 442.7L405.3 343c-4.5-4.5-10.6-7-17-7H372c27.6-35.3 44-79.7 44-128C416
          93.1 322.9 0 208 0S0 93.1 0 208s93.1 208 208 208c48.3 0 92.7-16.4
          128-44v16.3c0 6.4 2.5 12.5 7 17l99.7 99.7c9.4 9.4 24.6 9.4
          33.9 0l28.3-28.3c9.4-9.4 9.4-24.6.1-34zM208
          336c-70.7 0-128-57.2-128-128 0-70.7 57.2-128 128-128
          70.7 0 128 57.2 128 128 0 70.7-57.2 128-128 128z"
        ></path>
      </svg>
      <input
        @input="updateSearchValue"
        class="w-full rounded-l-full px-4 py-2 leading-tight text-gray-700 focus:outline-none"
        id="search"
        type="text"
        placeholder="Please Search Something..."
      />
    </div>
  </div>
  <div class="bg-background px-4 py-2">
    <div class="label pb-4">Filter By</div>
    <div v-for="(name, index) in props.fieldNames" :key="index" class="mb-4">
      <label class="label text-primary">
        <input type="checkbox" class="me-2" v-model="checkedFields[index]" />
        {{ name }}
      </label>
    </div>
  </div>
</template>

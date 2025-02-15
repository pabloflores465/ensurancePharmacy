<script setup lang="ts">
const toggleSidebar: Ref<boolean> = useToggleSidebar();
const currentId: Ref<number> = ref(0);
const categories = useCategories();
const showSubcategories: Ref<boolean[]> = ref([]);
const search: Ref<boolean> = useSearch();
const edit: Ref<boolean> = useEdit();

onMounted((): void => {
  categories.value.forEach(() => {
    showSubcategories.value.push(true);
  });
});

function toggle(id: number): void {
  if (currentId.value === id) {
    toggleSidebar.value = false;
    currentId.value = 0;
  } else {
    toggleSidebar.value = true;
    currentId.value = id;
  }
}
</script>
<template>
  <div class="flex flex-col border-b-1 border-b-gray-400 py-2">
    <NuxtLink to="/" class="mb-6 flex justify-center">
      <img
        class="rounded-full border-2 border-[var(--hover-color)] hover:scale-105"
        src="../public/logo.png"
        height="40px"
        width="40px"
      />
    </NuxtLink>
    <button
      @click="
        (): void => {
          search = !search;
        }
      "
      :class="
        search
          ? 'bg-[var(--accent-color)] px-2 py-2 hover:bg-[var(--hover-color)]'
          : 'px-2 py-2 hover:bg-[var(--hover-color-secondary)]'
      "
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"
        :fill="search ? '--primary-color' : 'white'"
      >
        <path
          d="M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z"
        />
      </svg>
    </button>
    <button
      @click="toggle(1)"
      :class="
        currentId === 1
          ? 'bg-[var(--accent-color)] px-2 py-2 hover:bg-[var(--hover-color)]'
          : 'px-2 py-2 hover:bg-[var(--hover-color-secondary)]'
      "
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"
        :fill="currentId === 1 ? '--primary-color' : 'white'"
      >
        <path
          d="M480-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T560-640q0-33-23.5-56.5T480-720q-33 0-56.5 23.5T400-640q0 33 23.5 56.5T480-560Zm0-80Zm0 400Z"
        />
      </svg>
    </button>
    <button
      @click="toggle(2)"
      :class="
        currentId === 2
          ? 'bg-[var(--accent-color)] px-2 py-2 hover:bg-[var(--hover-color)]'
          : 'px-2 py-2 hover:bg-[var(--hover-color-secondary)]'
      "
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"
        :fill="currentId === 2 ? '--primary-color' : 'white'"
      >
        <path
          d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z"
        />
      </svg>
    </button>
    <button
      @click="toggle(3)"
      :class="
        currentId === 3
          ? 'bg-[var(--accent-color)] px-2 py-2 hover:bg-[var(--hover-color)]'
          : 'px-2 py-2 hover:bg-[var(--hover-color-secondary)]'
      "
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"
        :fill="currentId === 3 ? '--primary-color' : 'white'"
      >
        <path
          d="M440-160q-17 0-28.5-11.5T400-200v-240L168-736q-15-20-4.5-42t36.5-22h560q26 0 36.5 22t-4.5 42L560-440v240q0 17-11.5 28.5T520-160h-80Zm40-308 198-252H282l198 252Zm0 0Z"
        />
      </svg>
    </button>
    <button
      @click="():void=>{edit=!edit}"
      :class="
        edit
          ? 'bg-[var(--accent-color)] px-2 py-2 hover:bg-[var(--hover-color)]'
          : 'px-2 py-2 hover:bg-[var(--hover-color-secondary)]'
      "
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"

        :fill="edit ? '--primary-color' : 'white'"
      >
        <path
          d="M200-200h57l391-391-57-57-391 391v57Zm-80 80v-170l528-527q12-11 26.5-17t30.5-6q16 0 31 6t26 18l55 56q12 11 17.5 26t5.5 30q0 16-5.5 30.5T817-647L290-120H120Zm640-584-56-56 56 56Zm-141 85-28-29 57 57-29-28Z"
        />
      </svg>
    </button>
  </div>
  <div
    class="min-w-[200px] justify-center border-b-1 border-l-1 border-b-gray-400 border-l-gray-400 bg-[var(--primary-color)] p-2 py-4 max-sm:w-full sm:w-[20%]"
    v-if="toggleSidebar && currentId === 1"
  >
    <div class="flex flex-col items-center justify-center">
      <NuxtLink
        to="/signup"
        class="mb-2 flex w-full transform justify-center rounded-full bg-[var(--accent-color)] px-4 py-2 font-bold text-[var(--primary-color)] shadow transition duration-300 ease-in-out hover:scale-105 hover:bg-[var(--hover-color)]"
      >
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="--primary-color"
        >
          <path
            d="M720-400v-120H600v-80h120v-120h80v120h120v80H800v120h-80Zm-360-80q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM40-160v-112q0-34 17.5-62.5T104-378q62-31 126-46.5T360-440q66 0 130 15.5T616-378q29 15 46.5 43.5T680-272v112H40Zm80-80h480v-32q0-11-5.5-20T580-306q-54-27-109-40.5T360-360q-56 0-111 13.5T140-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T440-640q0-33-23.5-56.5T360-720q-33 0-56.5 23.5T280-640q0 33 23.5 56.5T360-560Zm0-80Zm0 400Z"
          />
        </svg>
        Sign Up
      </NuxtLink>
      <NuxtLink
        to="/login"
        class="mb-2 flex w-full transform justify-center rounded-full bg-[var(--accent-color)] px-4 py-2 font-bold text-[var(--primary-color)] shadow transition duration-300 ease-in-out hover:scale-105 hover:bg-[var(--hover-color)]"
      >
        <svg
          class="me-2"
          xmlns="http://www.w3.org/2000/svg"
          height="24px"
          viewBox="0 -960 960 960"
          width="24px"
          fill="--primary-color"
        >
          <path
            d="M480-120v-80h280v-560H480v-80h280q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H480Zm-80-160-55-58 102-102H120v-80h327L345-622l55-58 200 200-200 200Z"
          />
        </svg>
        Log In</NuxtLink
      >
    </div>
  </div>
  <div
    class="min-w-[200px] justify-center border-b-1 border-l-1 border-b-gray-400 border-l-gray-400 bg-[var(--primary-color)] p-2 py-4 max-sm:w-full sm:w-[20%]"
    v-if="toggleSidebar && currentId === 2"
  >
    <NuxtLink
      to="/policies"
      class="mb-2 flex w-full transform items-center justify-center rounded-full bg-[var(--accent-color)] px-4 py-2 font-bold text-[var(--primary-color)] shadow transition duration-300 ease-in-out hover:scale-105 hover:bg-[var(--hover-color)]"
    >
      <svg
        class="pe-2"
        xmlns="http://www.w3.org/2000/svg"
        height="30px"
        viewBox="0 -960 960 960"
        width="30px"
        fill="--primary-color"
      >
        <path
          d="M200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h168q13-36 43.5-58t68.5-22q38 0 68.5 22t43.5 58h168q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm80-80h280v-80H280v80Zm0-160h400v-80H280v80Zm0-160h400v-80H280v80Zm200-190q13 0 21.5-8.5T510-820q0-13-8.5-21.5T480-850q-13 0-21.5 8.5T450-820q0 13 8.5 21.5T480-790ZM200-200v-560 560Z"
        />
      </svg>
      Policies</NuxtLink
    >
  </div>
  <div
    class="min-w-[200px] justify-center border-b-1 border-l-1 border-b-gray-400 border-l-gray-400 bg-[var(--primary-color)] p-2 max-sm:w-full sm:w-[20%]"
    v-if="toggleSidebar && currentId === 3"
  >
    <div
      class="flex max-h-[calc(100vh-8rem)] flex-col overflow-auto max-sm:items-center"
    >
      <div
        class="my-4 ms-2 mb-4 font-bold text-white"
        v-for="(category, catIndex) in categories"
        :key="category.id"
      >
        <button
          class="mb-2 flex"
          @click="
            () => {
              showSubcategories[catIndex] = !showSubcategories[catIndex];
            }
          "
        >
          {{ category.name }}
          <svg
            v-if="showSubcategories[catIndex]"
            class="ms-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="white"
          >
            <path d="M480-360 280-560h400L480-360Z" />
          </svg>
          <svg
            v-if="!showSubcategories[catIndex]"
            class="ms-2"
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="white"
          >
            <path d="m280-400 200-200 200 200H280Z" />
          </svg>
        </button>
        <div
          v-for="subcategory in category.subcategories"
          :key="subcategory.id"
          class="ms-2 border-s-2 ps-2 font-semibold text-gray-300"
          v-if="showSubcategories[catIndex]"
        >
          {{ subcategory.name }}
        </div>
      </div>
    </div>
  </div>
</template>

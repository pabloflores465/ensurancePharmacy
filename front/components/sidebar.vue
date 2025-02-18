<script setup lang="ts">
import { darkMode, toggleDark } from "~/composables/darkMode";

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

const router = useRoute();
const dark = darkMode();

const sidebarItems: Ref<
  {
    type: "link" | "button";
    conditional: () => boolean;
    click: () => void;
    path: string;
    iconPath: string | (() => string);
  }[]
> = ref([
  {
    type: "link",
    path: "/signup",
    conditional: () => router.path === "/signup",
    click: () => {},
    iconPath:
      "M720-400v-120H600v-80h120v-120h80v120h120v80H800v120h-80Zm-360-80q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM40-160v-112q0-34 17.5-62.5T104-378q62-31 126-46.5T360-440q66 0 130 15.5T616-378q29 15 46.5 43.5T680-272v112H40Zm80-80h480v-32q0-11-5.5-20T580-306q-54-27-109-40.5T360-360q-56 0-111 13.5T140-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T440-640q0-33-23.5-56.5T360-720q-33 0-56.5 23.5T280-640q0 33 23.5 56.5T360-560Zm0-80Zm0 400Z",
  },
  {
    type: "link",
    path: "/login",
    conditional: () => router.path === "/login",
    click: () => {},
    iconPath:
      "M480-120v-80h280v-560H480v-80h280q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H480Zm-80-160-55-58 102-102H120v-80h327L345-622l55-58 200 200-200 200Z",
  },
  {
    type: "link",
    path: "/policies",
    conditional: () => router.path === "/policies",
    click: () => {},
    iconPath:
      "M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z",
  },
  {
    type: "link",
    path: "/calendar",
    conditional: () => router.path === "/calendar",
    click: () => {},
    iconPath:
      "M200-80q-33 0-56.5-23.5T120-160v-560q0-33 23.5-56.5T200-800h40v-80h80v80h320v-80h80v80h40q33 0 56.5 23.5T840-720v560q0 33-23.5 56.5T760-80H200Zm0-80h560v-400H200v400Zm0-480h560v-80H200v80Zm0 0v-80 80Zm280 240q-17 0-28.5-11.5T440-440q0-17 11.5-28.5T480-480q17 0 28.5 11.5T520-440q0 17-11.5 28.5T480-400Zm-160 0q-17 0-28.5-11.5T280-440q0-17 11.5-28.5T320-480q17 0 28.5 11.5T360-440q0 17-11.5 28.5T320-400Zm320 0q-17 0-28.5-11.5T600-440q0-17 11.5-28.5T640-480q17 0 28.5 11.5T680-440q0 17-11.5 28.5T640-400ZM480-240q-17 0-28.5-11.5T440-280q0-17 11.5-28.5T480-320q17 0 28.5 11.5T520-280q0 17-11.5 28.5T480-240Zm-160 0q-17 0-28.5-11.5T280-280q0-17 11.5-28.5T320-320q17 0 28.5 11.5T360-280q0 17-11.5 28.5T320-240Zm320 0q-17 0-28.5-11.5T600-280q0-17 11.5-28.5T640-320q17 0 28.5 11.5T680-280q0 17-11.5 28.5T640-240Z",
  },
  {
    type: "link",
    path: "/hospital",
    conditional: () => router.path === "/hospital",
    click: () => {},
    iconPath:
      "M420-280h120v-140h140v-120H540v-140H420v140H280v120h140v140ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm0-560v560-560Z",
  },
  {
    type: "button",
    conditional: () => search.value,
    click: () => {
      search.value = !search.value;
    },
    path: "",
    iconPath:
      "M784-120 532-372q-30 24-69 38t-83 14q-109 0-184.5-75.5T120-580q0-109 75.5-184.5T380-840q109 0 184.5 75.5T640-580q0 44-14 83t-38 69l252 252-56 56ZM380-400q75 0 127.5-52.5T560-580q0-75-52.5-127.5T380-760q-75 0-127.5 52.5T200-580q0 75 52.5 127.5T380-400Z",
  },
  {
    type: "button",
    conditional: () => currentId.value === 1,
    click: () => toggle(1),
    path: "",
    iconPath:
      "M480-480q-66 0-113-47t-47-113q0-66 47-113t113-47q66 0 113 47t47 113q0 66-47 113t-113 47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm240-320q33 0 56.5-23.5T560-640q0-33-23.5-56.5T480-720q-33 0-56.5 23.5T400-640q0 33 23.5 56.5T480-560Zm0-80Zm0 400Z",
  },
  {
    type: "button",
    conditional: () => currentId.value === 3,
    click: () => toggle(3),
    path: "",
    iconPath:
      "M440-160q-17 0-28.5-11.5T400-200v-240L168-736q-15-20-4.5-42t36.5-22h560q26 0 36.5 22t-4.5 42L560-440v240q0 17-11.5 28.5T520-160h-80Zm40-308 198-252H282l198 252Zm0 0Z",
  },
  {
    type: "button",
    conditional: () => edit.value,
    click: () => {
      edit.value = !edit.value;
    },
    path: "",
    iconPath:
      "M200-200h57l391-391-57-57-391 391v57Zm-80 80v-170l528-527q12-11 26.5-17t30.5-6q16 0 31 6t26 18l55 56q12 11 17.5 26t5.5 30q0 16-5.5 30.5T817-647L290-120H120Zm640-584-56-56 56 56Zm-141 85-28-29 57 57-29-28Z",
  },
  {
    type: "button",
    conditional: () => dark.value,
    click: () => toggleDark(),
    path: "",
    iconPath: () =>
      !dark.value
        ? "M480-360q50 0 85-35t35-85q0-50-35-85t-85-35q-50 0-85 35t-35 85q0 50 35 85t85 35Zm0 80q-83 0-141.5-58.5T280-480q0-83 58.5-141.5T480-680q83 0 141.5 58.5T680-480q0 83-58.5 141.5T480-280ZM200-440H40v-80h160v80Zm720 0H760v-80h160v80ZM440-760v-160h80v160h-80Zm0 720v-160h80v160h-80ZM256-650l-101-97 57-59 96 100-52 56Zm492 496-97-101 53-55 101 97-57 59Zm-98-550 97-101 59 57-100 96-56-52ZM154-212l101-97 55 53-97 101-59-57Zm326-268Z"
        : "M480-120q-150 0-255-105T120-480q0-150 105-255t255-105q14 0 27.5 1t26.5 3q-41 29-65.5 75.5T444-660q0 90 63 153t153 63q55 0 101-24.5t75-65.5q2 13 3 26.5t1 27.5q0 150-105 255T480-120Zm0-80q88 0 158-48.5T740-375q-20 5-40 8t-40 3q-123 0-209.5-86.5T364-660q0-20 3-40t8-40q-78 32-126.5 102T200-480q0 116 82 198t198 82Zm-10-270Z",
  },
]);

const linkItems = computed(() =>
  sidebarItems.value.filter((item) => item.type === "link"),
);

const buttonItems = computed(() =>
  sidebarItems.value.filter((item) => item.type === "button"),
);
</script>
<template>
  <main
    :class="[
      'bg-background border-b-secondary flex flex-col border-b-1 py-2',
      dark ? 'dark' : '',
    ]"
  >
    <NuxtLink to="/" class="mb-6 flex justify-center">
      <img
        class="border-h-color flex h-12 rounded-full border-2"
        src="/logo.png"
        alt="company logo"
      />
    </NuxtLink>
    <NuxtLink
      v-for="(item, index) in linkItems"
      :to="item.path"
      :key="index"
      :class="[
        'px-2 py-2',
        item.conditional()
          ? 'bg-accent hover:bg-h-accent'
          : 'hover:bg-h-background',
      ]"
    >
      <svg
        :class="[
          item.conditional() ? 'text-light dark:text-dark' : 'text-primary',
        ]"
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"
        fill="currentColor"
      >
        <path
          :d="
            typeof item.iconPath === 'function'
              ? item.iconPath()
              : item.iconPath
          "
        />
      </svg>
    </NuxtLink>
    <button
      v-for="item in buttonItems"
      @click="() => item.click()"
      :class="[
        'px-2 py-2',
        item.conditional()
          ? 'bg-accent hover:bg-h-accent'
          : 'hover:bg-h-background',
      ]"
    >
      <svg
        :class="[
          item.conditional() ? 'text-light dark:text-dark' : 'text-primary',
        ]"
        xmlns="http://www.w3.org/2000/svg"
        height="40px"
        viewBox="0 -960 960 960"
        width="40px"
        fill="currentColor"
      >
        <path
          :d="
            typeof item.iconPath === 'function'
              ? item.iconPath()
              : item.iconPath
          "
        />
      </svg>
    </button>
  </main>
  <section
    :class="[
      'border-b-secondary min-w-[200px] justify-center border-b-1 border-l-1 border-l-gray-400 p-2 max-sm:w-full sm:w-[20%]',
      dark ? 'dark' : '',
    ]"
    v-if="toggleSidebar && currentId === 3"
  >
    <div
      class="flex max-h-[calc(100vh-8rem)] flex-col overflow-auto max-sm:items-center"
    >
      <div
        class="text-primary hover:text-h-accent my-4 ms-2 mb-4 font-bold"
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
            xmlns="http://www.w3.org/2000/svg"
            height="24px"
            viewBox="0 -960 960 960"
            width="24px"
            fill="currentColor"
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
            fill="currentColor"
          >
            <path d="m280-400 200-200 200 200H280Z" />
          </svg>
        </button>
        <div
          v-for="subcategory in category.subcategories"
          :key="subcategory.id"
          class="text-secondary hover:text-h-accent ms-2 border-s-2 ps-2 font-semibold"
          v-if="showSubcategories[catIndex]"
        >
          {{ subcategory.name }}
        </div>
      </div>
    </div>
  </section>
</template>

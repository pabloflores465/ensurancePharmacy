import { createMemoryHistory, createRouter } from "vue-router";
import Login from "./pages/login.vue";
import Home from "./pages/home.vue";

const routes = [
  { path: "/", component: Login },
  { path: "/home", component: Home },
];

const router = createRouter({
  history: createMemoryHistory(),
  routes,
});

export default router;

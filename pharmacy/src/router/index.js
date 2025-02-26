import { createRouter, createWebHistory } from 'vue-router';
import Home from '../pages/Home.vue'; // Ajustar rutas relativas
import Login from '../pages/Login.vue';
import Dashboard from '../pages/Dashboard.vue';
import Register from '../pages/Register.vue';
import Catalogo from '../pages/Catalogo.vue';
import Aseguradoras from '@/pages/Aseguradoras.vue';

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path : '/register', component: Register },
  { path : '/catalogo', component: Catalogo },
  { path : '/aseguradoras', component: Aseguradoras }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
import { createRouter, createWebHistory } from 'vue-router';
import Home from '../pages/Home.vue'; // Ajustar rutas relativas
import Login from '../pages/Login.vue';
import Dashboard from '../pages/Dashboard.vue';
import Register from '../pages/Register.vue';
import Catalogo from '../pages/Catalogo.vue';
import Aseguradoras from '@/pages/Aseguradoras.vue';
import Ofertas from '@/pages/Ofertas.vue';
import ProductoDetalle from '@/pages/ProductoDetalle.vue'; // new import
import Receta from '@/components/Receta.vue';

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path : '/register', component: Register },
  { path : '/catalogo', component: Catalogo },
  { path : '/aseguradoras', component: Aseguradoras },
  { path : '/ofertas', component: Ofertas },
  { path: '/producto/:id', name: 'ProductoDetalle', component: ProductoDetalle},
  { path: '/receta', component: Receta},
  
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
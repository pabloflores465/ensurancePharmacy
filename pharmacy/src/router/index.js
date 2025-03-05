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
import CreateProduct from '@/pages/CreateProduct.vue';
import { authService } from '@/services/authService';

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
  { path: '/create-product', component: CreateProduct},
  {
    path: '/admin/create-product',
    name: 'CreateProduct',
    component: CreateProduct,
  },
  {
    path: '/create-product',
    name: 'CreateProduct',
    component: CreateProduct,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const currentUser = authService.getCurrentUser();

  if (requiresAuth && !currentUser) {
    next('/login');
  } else {
    next();
  }
});

export default router;
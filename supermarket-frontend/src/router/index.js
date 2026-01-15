import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由配置文件
 * 定义应用的路由规则，将 URL 路径映射到对应的组件
 */
const router = createRouter({
  // 使用 HTML5 History 模式，URL 不带 # 号
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      // 路由懒加载：只有访问该路由时才加载组件，提升首屏加载速度
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    }
  ]
})

export default router

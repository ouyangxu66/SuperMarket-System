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
      component: () => import('../views/HomeView.vue'),
      children: [
        {
          path: '/user/list',
          name: 'user-list',
          component: () => import('../views/user/UserListView.vue'),
          meta: { title: '用户列表' }
        },
        {
          path: '/product/list',
          name: 'product-list',
          component: () => import('../views/product/ProductListView.vue'),
          meta: { title: '商品列表' }
        },
        {
          path: '/product/category',
          name: 'product-category',
          component: () => import('../views/product/CategoryView.vue'),
          meta: { title: '商品分类' }
        },
        {
          path: '/inventory/list',
          name: 'inventory-list',
          component: () => import('../views/inventory/InventoryListView.vue'),
          meta: { title: '库存列表' }
        },
        {
          path: '/inventory/count',
          name: 'inventory-count',
          component: () => import('../views/inventory/InventoryCountView.vue'),
          meta: { title: '库存盘点' }
        },
        {
          path: '/inventory/count/:id',
          name: 'inventory-count-detail',
          component: () => import('../views/inventory/InventoryCountDetailView.vue'),
          meta: { title: '盘点详情' }
        },
        {
          path: '/profile',
          name: 'profile',
          component: () => import('../views/ProfileView.vue'),
          meta: { title: '个人中心' }
        }
      ]
    }
  ]
})

export default router

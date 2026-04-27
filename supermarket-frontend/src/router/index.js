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
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: () => import('../views/dashboard/DashboardView.vue'),
          meta: { title: '首页看板' }
        },
        {
          path: '/sale/cashier',
          name: 'sale-cashier',
          component: () => import('../views/sale/CashierView.vue'),
          meta: { title: '收银台' }
        },
        {
          path: '/sale/order-list',
          name: 'sale-order-list',
          component: () => import('../views/sale/SaleOrderListView.vue'),
          meta: { title: '销售记录' }
        },
        {
          path: '/member/list',
          name: 'member-list',
          component: () => import('../views/member/MemberListView.vue'),
          meta: { title: '会员管理' }
        },
        {
          path: '/user/list',
          name: 'user-list',
          component: () => import('../views/user/UserListView.vue'),
          meta: { title: '员工列表' }
        },
        {
          path: '/employee/list',
          redirect: '/user/list'
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
          path: '/inventory/restock',
          name: 'inventory-restock',
          component: () => import('../views/inventory/RestockSuggestionView.vue'),
          meta: { title: '智能补货建议' }
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

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
  // 获取本地存储的 token
  const token = localStorage.getItem('token')

  // 检查路由是否需要登录
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      // 未登录，重定向到登录页
      next({ path: '/login', query: { redirect: to.fullPath } })
    } else {
      // 已登录，正常放行
      next()
    }
  } else {
    // 如果是前往登录页，且已经有 token，跳转到首页
    if (to.path === '/login' && token) {
      next('/')
    } else {
      // 不需要登录的页面，直接放行
      next()
    }
  }
})

export default router

<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="240px" class="aside">
        <div class="logo">
          <img src="https://element-plus.org/images/element-plus-logo.svg" alt="logo" style="height: 28px; margin-right: 10px;">
          <span>超市管理后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          background-color="#fff"
          text-color="#444"
          active-text-color="#1a73e8"
          router
          :border-right="null"
        >
          <el-menu-item index="/dashboard" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER'])">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>


          <el-sub-menu index="2" v-if="hasPermission(['ROLE_ADMIN'])">
            <template #title>
              <el-icon><User /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/user/list">员工列表</el-menu-item>
          </el-sub-menu>


          <el-sub-menu index="member" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER', 'ROLE_CASHIER'])">
            <template #title>
              <el-icon><User /></el-icon>
              <span>会员管理</span>
            </template>
            <el-menu-item index="/member/list">会员列表</el-menu-item>
          </el-sub-menu>


          <el-sub-menu index="sale" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER', 'ROLE_CASHIER'])">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>销售管理</span>
            </template>
            <el-menu-item index="/sale/cashier" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER', 'ROLE_CASHIER'])">收银台</el-menu-item>
            <el-menu-item index="/sale/order-list" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER'])">销售记录</el-menu-item>
          </el-sub-menu>


          <el-sub-menu index="3" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER', 'ROLE_WAREHOUSE_KEEPER', 'ROLE_PURCHASER'])">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/product/list">商品列表</el-menu-item>
            <el-menu-item index="/product/category" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER'])">分类管理</el-menu-item>
          </el-sub-menu>


          <el-sub-menu index="4" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER', 'ROLE_WAREHOUSE_KEEPER'])">
            <template #title>
              <el-icon><Box /></el-icon>
              <span>库存管理</span>
            </template>
            <el-menu-item index="/inventory/list">库存列表</el-menu-item>
            <el-menu-item index="/inventory/count" v-if="hasPermission(['ROLE_ADMIN', 'ROLE_STORE_MANAGER', 'ROLE_WAREHOUSE_KEEPER'])">库存盘点</el-menu-item>
            <el-menu-item index="/inventory/restock">智能补货建议</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="5" v-if="hasPermission(['ROLE_PURCHASER'])">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>采购管理</span>
            </template>
            <el-menu-item index="/inventory/list">库存预警查看</el-menu-item>
            <el-menu-item index="/inventory/restock">补货建议</el-menu-item>
            <el-menu-item index="/purchase/plan">做采购计划</el-menu-item>
            <el-menu-item index="/purchase/order">下采购单</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-button link icon="Fold" style="font-size: 20px; color: #333; margin-right: 16px;"></el-button>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="route.path !== '/dashboard'">{{ route.meta?.title || '当前页面' }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" style="margin-right: 8px"></el-avatar>
                {{ userInfo.nickname || 'Admin' }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ArrowDown, Box, Goods, HomeFilled, User, ShoppingCart } from '@element-plus/icons-vue'
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { logout, getInfo } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.path)
const userInfo = ref({})
const userRoles = ref([])

// 从后端获取用户信息
onMounted(async () => {
  try {
    const res = await getInfo()
    userInfo.value = res.data
    userRoles.value = res.data.roles || []
  } catch (e) {
    console.error('获取用户信息失败', e)
  }
})

// 判断是否有权限
const hasPermission = (roles) => {
  if (!roles || roles.length === 0) return true
  return roles.some(role => userRoles.value.includes(role))
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await logout()
    } catch (e) {
      console.error(e)
    }
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>


<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #fff;
  border-right: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a73e8;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid #f0f0f0;
}

.el-menu-vertical {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #5f6368;
  display: flex;
  align-items: center;
}

.main {
  background: linear-gradient(180deg, #f5f7fb 0%, #eef3fb 100%);
  padding: 24px;
  overflow: auto;
}
</style>

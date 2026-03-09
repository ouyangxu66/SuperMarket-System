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
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><User /></el-icon>
              <span>员工管理</span>
            </template>
            <el-menu-item index="/user/list">员工列表</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="member">
            <template #title>
              <el-icon><User /></el-icon>
              <span>会员管理</span>
            </template>
            <el-menu-item index="/member/list">会员列表</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="sale">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>销售管理</span>
            </template>
            <el-menu-item index="/sale/cashier">收银台</el-menu-item>
            <el-menu-item index="/sale/order-list">销售记录</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/product/list">商品列表</el-menu-item>
            <el-menu-item index="/product/category">分类管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="4">
            <template #title>
              <el-icon><Box /></el-icon>
              <span>库存管理</span>
            </template>
            <el-menu-item index="/inventory/list">库存列表</el-menu-item>
            <el-menu-item index="/inventory/count">库存盘点</el-menu-item>
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
                Admin
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
import { ArrowDown, Box, Goods, HomeFilled, User } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { logout } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.path)

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await logout()
    } catch (e) {
      console.error(e)
    }
    localStorage.removeItem('token')
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

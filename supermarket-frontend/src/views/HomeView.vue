<template>
  <div class="common-layout">
    <el-container class="layout-container">
      <el-aside width="240px" class="aside">
        <div class="logo">
          <img src="https://element-plus.org/images/element-plus-logo.svg" alt="logo" style="height: 28px; margin-right: 10px;">
          <span>超市管理后台</span>
        </div>
        <el-menu
          default-active="/"
          class="el-menu-vertical"
          background-color="#fff"
          text-color="#444"
          active-text-color="#1a73e8"
          router
          :border-right="null"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user/list">用户列表</el-menu-item>
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
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" style="margin-right: 8px"></el-avatar>
                Admin
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
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
          <!-- 路由出口 -->
          <router-view v-if="$route.path !== '/'" />

          <!-- Default Dashboard Content if on Home -->
          <div v-else>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-card shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span>总销售额</span>
                    </div>
                  </template>
                  <div class="card-value">￥ 120,000</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span>订单数</span>
                    </div>
                  </template>
                  <div class="card-value">1,200</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span>用户数</span>
                    </div>
                  </template>
                  <div class="card-value">5,000</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover">
                  <template #header>
                    <div class="card-header">
                      <span>商品总数</span>
                    </div>
                  </template>
                  <div class="card-value">800</div>
                </el-card>
              </el-col>
            </el-row>

            <el-card style="margin-top: 20px;">
              <template #header>
                 <span>系统公告</span>
              </template>
              <p>欢迎使用超市管理系统，前端基于 Vue 3 + Element Plus 构建，遵循 Material Design 设计规范。</p>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
/**
 * 首页视图 (Layout)
 * 包含侧边栏菜单、顶部导航栏和主要内容区域
 * 这里采用了常见的后台管理系统布局
 */
import { useRouter } from 'vue-router'
import { logout } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()

/**
 * 处理下拉菜单指令
 * 目前主要用于处理退出登录
 * @param {String} command - 下拉菜单项的 command 属性值
 */
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      // 调用后端退出接口（可选，视后端是否需要处理服务端 session/token 无效化）
       await logout()
    } catch(e) {
      console.error(e)
    }
    // 清除本地存储的 Token
    localStorage.removeItem('token')
    ElMessage.success('已退出登录')
    // 跳转回登录页
    router.push('/login')
  }
}
</script>

<style scoped>
/* 布局样式 */
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
  background-color: #fafafa;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}
</style>

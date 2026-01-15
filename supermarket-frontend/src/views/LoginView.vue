<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>超市管理系统</h2>
          <p class="subtitle">登录</p>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top" size="large">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-button" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="login-links">
          <router-link to="/register">还没有账号？去注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 登录页面视图
 * 处理用户登录逻辑，包含表单验证和 API 调用
 */
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'

// 路由实例，用于页面跳转
const router = useRouter()
// 表单引用，用于调用表单验证方法
const loginFormRef = ref(null)
// 加载状态，用于按钮防抖和 loading 展示
const loading = ref(false)

// 登录表单数据模型
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则配置
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/**
 * 处理登录点击事件
 * 1. 校验表单
 * 2. 调用登录 API
 * 3. 存储 Token
 * 4. 跳转首页
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        // 存储 Token 到本地存储 (实际项目中可结合 Pinia 状态管理)
        localStorage.setItem('token', res.data.token)
        ElMessage.success('登录成功')
        // 登录成功后跳转到首页
        router.push('/')
      } catch (error) {
        console.error(error)
        // 错误提示已在 request.js 拦截器中统一处理
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
/* 样式定义 */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  margin: 0;
  color: #1a73e8; /* Google Blue */
  font-weight: 500;
}

.subtitle {
  color: #666;
  margin-top: 8px;
  font-size: 14px;
}

.login-button {
  width: 100%;
  margin-top: 10px;
  background-color: #1a73e8;
  border-color: #1a73e8;
}

.login-button:hover {
  background-color: #1557b0;
  border-color: #1557b0;
}

.login-links {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}

.login-links a {
  color: #1a73e8;
  text-decoration: none;
}

.login-links a:hover {
  text-decoration: underline;
}
</style>

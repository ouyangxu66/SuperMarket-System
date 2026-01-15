<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>超市管理系统</h2>
          <p class="subtitle">注册新账号</p>
        </div>
      </template>

      <el-alert
        title="注意"
        description="注册功能暂未开放API，请联系系统管理员获取账号。"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      />

      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-position="top" size="large">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-button" @click="handleRegister">注册</el-button>
        </el-form-item>
        <div class="register-links">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 注册页面视图
 * 目前仅作为 UI 展示，暂无实际注册功能
 */
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const registerFormRef = ref(null)
// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

/**
 * 自定义验证函数：校验两次密码是否一致
 */
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

// 处理注册提交
const handleRegister = () => {
  if (!registerFormRef.value) return
  registerFormRef.value.validate((valid) => {
    if (valid) {
      ElMessage.warning('注册功能暂未开放，请联系管理员。')
    }
  })
}
</script>

<style scoped>
/* 注册页面样式 */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.register-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
}

.register-header h2 {
  margin: 0;
  color: #1a73e8;
  font-weight: 500;
}

.subtitle {
  color: #666;
  margin-top: 8px;
  font-size: 14px;
}

.register-button {
  width: 100%;
  margin-top: 10px;
  background-color: #1a73e8;
  border-color: #1a73e8;
}

.register-button:hover {
  background-color: #1557b0;
  border-color: #1557b0;
}

.register-links {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}

.register-links a {
  color: #1a73e8;
  text-decoration: none;
}

.register-links a:hover {
  text-decoration: underline;
}
</style>

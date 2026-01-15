<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card user-info-card">
          <div class="user-header">
            <el-avatar :size="100" :src="userInfo.avatar" />
            <h3>{{ userInfo.nickname }}</h3>
            <p class="role-text">{{ userInfo.roles ? userInfo.roles.join(', ') : '普通用户' }}</p>
          </div>
          <div class="user-detail-list">
             <div class="detail-item">
               <el-icon><User /></el-icon>
               <span>账号：{{ userInfo.username }}</span>
             </div>
             <div class="detail-item">
               <el-icon><Calendar /></el-icon>
               <span>注册时间：{{ formatDateTime(userInfo.createTime) || '未知' }}</span>
             </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>个人设置</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="info">
              <el-form :model="infoForm" ref="infoFormRef" :rules="infoRules" label-width="80px" style="margin-top: 20px; max-width: 500px">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="infoForm.nickname" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="infoForm.phone" />
                </el-form-item>
                 <el-form-item label="性别">
                  <el-radio-group v-model="infoForm.gender">
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="0">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="修改密码" name="password">
               <el-form :model="pwdForm" ref="pwdFormRef" :rules="pwdRules" label-width="100px" style="margin-top: 20px; max-width: 500px">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                   <el-button type="primary" @click="handleUpdatePwd">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getInfo, updatePassword } from '@/api/auth'
import { updateUser, getUserById } from '@/api/user'
import { ElMessage } from 'element-plus'

const activeTab = ref('info')
const userInfo = ref({})

// 基本资料表单
const infoForm = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  gender: 1
})
const infoFormRef = ref(null)
const infoRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

// 密码表单
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const pwdFormRef = ref(null)

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }]
}

onMounted(() => {
  loadUserInfo()
})

const formatDateTime = (timeStr) => {
  return timeStr ? timeStr.replace('T', ' ') : ''
}

const loadUserInfo = async () => {
  try {
    const res = await getInfo()
    userInfo.value = res.data

    // 如果想要获取更多详情（如 phone, gender），可能需要调 getUserById
    // 因为 /auth/info 这种只有基本信息
    if (res.data.id) {
       const userDetailRes = await getUserById(res.data.id)
       const detail = userDetailRes.data
       Object.assign(userInfo.value, detail) // 合并详情

       // 回填表单
       infoForm.id = detail.id
       infoForm.username = detail.username
       infoForm.nickname = detail.nickname
       infoForm.phone = detail.phone
       infoForm.gender = detail.gender
    }
  } catch (error) {
    console.error(error)
  }
}

const handleUpdateInfo = async () => {
  if (!infoFormRef.value) return
  await infoFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateUser(infoForm)
        ElMessage.success('个人资料修改成功')
        loadUserInfo()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const handleUpdatePwd = async () => {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updatePassword({
            oldPassword: pwdForm.oldPassword,
            newPassword: pwdForm.newPassword
        })
        ElMessage.success('密码修改成功')
        pwdFormRef.value.resetFields()
      } catch (error) {
        console.error(error)
      }
    }
  })
}
</script>

<style scoped>
.user-info-card {
  text-align: center;
}
.user-header {
  margin-bottom: 20px;
}
.user-header h3 {
  margin: 10px 0 5px;
}
.role-text {
  color: #999;
  font-size: 14px;
}
.user-detail-list {
  text-align: left;
  padding: 0 20px;
}
.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #606266;
}
.detail-item .el-icon {
  margin-right: 10px;
}
</style>


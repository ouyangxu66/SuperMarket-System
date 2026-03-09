<template>
  <div class="user-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="登录账号">
          <el-input v-model="queryParams.username" placeholder="请输入登录账号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="姓名/工号">
          <el-input v-model="queryParams.keyword" placeholder="请输入员工姓名或工号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏与表格 -->
    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增员工</el-button>
        <el-button type="danger" icon="Delete" :disabled="selection.length === 0" @click="handleBatchDelete">批量删除</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="userList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="employeeNo" label="工号" min-width="120" />
        <el-table-column prop="realName" label="员工姓名" min-width="120">
          <template #default="scope">
            {{ scope.row.realName || scope.row.nickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="登录账号" min-width="120" />
        <el-table-column prop="jobTitle" label="岗位" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.gender === 1" type="primary">男</el-tag>
            <el-tag v-else-if="scope.row.gender === 0" type="danger">女</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hireDate" label="入职时间" width="180" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.hireDate) }}
          </template>
        </el-table-column>
        <el-table-column label="角色" min-width="150" align="center">
          <template #default="scope">
            <el-tag v-for="role in scope.row.roles" :key="role.id" size="small" style="margin-right: 5px">
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              disabled
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
           <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
           </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="560px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="92px">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入登录账号" :disabled="dialog.type === 'edit'" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="员工工号" prop="employeeNo">
          <el-input v-model="form.employeeNo" placeholder="请输入员工工号">
            <template #append>
              <el-button @click="generateEmployeeNo">重新生成</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="岗位" prop="jobTitle">
          <el-input v-model="form.jobTitle" placeholder="请输入岗位" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialog.type === 'add'">
          <el-input v-model="form.password" type="password" placeholder="请输入密码 (默认 123456)" show-password />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="入职时间" prop="hireDate">
          <el-date-picker
            v-model="form.hireDate"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择入职时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 员工列表页面
 * 包含搜索、表格展示、分页、新增/编辑/删除员工等功能
 */
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getUserPage, addUser, updateUser, deleteUser, deleteUserBatch } from '@/api/user'
import { getRoleList } from '@/api/role'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const selection = ref([])
const total = ref(0)
const userList = ref([])
const roleOptions = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  keyword: ''
})

const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

const form = ref({
  id: undefined,
  username: '',
  nickname: '',
  realName: '',
  employeeNo: '',
  jobTitle: '',
  phone: '',
  password: '',
  gender: 1,
  status: 1,
  hireDate: '',
  remark: '',
  roleIds: []
})

const formRef = ref(null)

const rules = {
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { max: 20, message: '员工姓名长度不能超过 20 个字符', trigger: 'blur' }
  ],
  employeeNo: [
    { required: true, message: '请输入员工工号', trigger: 'blur' },
    { min: 2, max: 32, message: '员工工号长度需在 2 到 32 个字符之间', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_-]+$/, message: '员工工号仅支持字母、数字、下划线和中划线', trigger: 'blur' }
  ],
  jobTitle: [
    { max: 20, message: '岗位长度不能超过 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^$|^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { min: 6, max: 20, message: '密码长度需在 6 到 20 个字符之间', trigger: 'blur' }
  ],
  roleIds: [
    { type: 'array', required: true, message: '请至少选择一个角色', trigger: 'change' }
  ],
  remark: [
    { max: 200, message: '备注长度不能超过 200 个字符', trigger: 'blur' }
  ]
}

const fetchRoleList = async () => {
  try {
    const res = await getRoleList()
    roleOptions.value = res.data
  } catch (error) {
    console.error('获取角色列表失败', error)
  }
}

onMounted(() => {
  fetchUserList()
  fetchRoleList()
})

const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  return String(timeStr).replace('T', ' ')
}

const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    userList.value = (res.data.records || []).map(item => ({
      ...item,
      realName: item.realName || item.nickname || ''
    }))
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  fetchUserList()
}

const resetQuery = () => {
  queryParams.username = ''
  queryParams.keyword = ''
  queryParams.pageNum = 1
  fetchUserList()
}

const handleSelectionChange = (val) => {
  selection.value = val
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  fetchUserList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  fetchUserList()
}

const handleAdd = () => {
  resetForm()
  dialog.type = 'add'
  dialog.title = '新增员工'
  dialog.visible = true
  generateEmployeeNo()
}

const handleEdit = (row) => {
  resetForm()
  dialog.type = 'edit'
  dialog.title = '编辑员工'
  dialog.visible = true
  nextTick(() => {
    Object.assign(form.value, {
      ...row,
      realName: row.realName || row.nickname || '',
      roleIds: row.roleIds || [],
      hireDate: row.hireDate ? formatDateTime(row.hireDate) : ''
    })
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除员工 "${row.realName || row.nickname || row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUserList()
  })
  .catch(() => {})
}

const handleBatchDelete = () => {
  if (selection.value.length === 0) return

  const ids = selection.value.map(item => item.id)

  ElMessageBox.confirm(
    `确认批量删除 ${ids.length} 名员工吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(async () => {
    await deleteUserBatch(ids)
    ElMessage.success('批量删除成功')
    fetchUserList()
  })
  .catch(() => {})
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const payload = {
          ...form.value,
          username: form.value.username.trim(),
          realName: form.value.realName.trim(),
          employeeNo: form.value.employeeNo.trim(),
          jobTitle: form.value.jobTitle?.trim() || '',
          remark: form.value.remark?.trim() || '',
          nickname: form.value.realName.trim(),
        }
        if (dialog.type === 'add') {
          await addUser(payload)
          ElMessage.success('新增成功')
        } else {
          await updateUser(payload)
          ElMessage.success('修改成功')
        }
        dialog.visible = false
        fetchUserList()
      } catch (error) {
        console.error(error)
        ElMessage.error(error?.response?.data?.message || '保存失败，请检查输入信息后重试')
      }
    }
  })
}

const handleStatusChange = (row) => {
  console.log('Status changed:', row.id, row.status)
}

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields()
  form.value = {
    id: undefined,
    username: '',
    nickname: '',
    realName: '',
    employeeNo: '',
    jobTitle: '',
    phone: '',
    password: '',
    gender: 1,
    status: 1,
    hireDate: '',
    remark: '',
    roleIds: []
  }
}

const buildEmployeeNo = () => {
  const now = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `EMP${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`
}

const generateEmployeeNo = () => {
  form.value.employeeNo = buildEmployeeNo()
  if (formRef.value) {
    formRef.value.validateField('employeeNo', () => {})
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-operations {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

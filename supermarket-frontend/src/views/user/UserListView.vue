<template>
  <div class="user-list-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
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
        <el-button type="primary" icon="Plus" @click="handleAdd">新增用户</el-button>
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
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.gender === 1" type="primary">男</el-tag>
            <el-tag v-else-if="scope.row.gender === 0" type="danger">女</el-tag>
            <el-tag v-else type="info">未知</el-tag>
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
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="dialog.type === 'edit'" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
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
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
 * 用户列表页面
 * 包含搜索、表格展示、分页、新增/编辑/删除用户等功能
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, addUser, updateUser, deleteUser, deleteUserBatch } from '@/api/user'

/**
 * 状态定义
 * 使用 ref 定义基本类型响应式数据，reactive 定义对象类型响应式数据
 */
const loading = ref(false) // 表格加载状态
const selection = ref([]) // 表格选中行数据
const total = ref(0) // 总条数
const userList = ref([]) // 用户列表数据

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: ''
})

// 对话框配置
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add' // 'add' or 'edit'
})

// 表单数据
const form = reactive({
  id: null,
  username: '',
  nickname: '',
  phone: '',
  password: '',
  gender: 1, // 默认男
  status: 1  // 默认启用
})

// 表单引用
const formRef = ref(null)

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

/**
 * 生命周期
 */
onMounted(() => {
  getList()
})

/**
 * 方法定义
 */

// 格式化时间
const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

// 获取用户列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    userList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.username = ''
  queryParams.pageNum = 1
  getList()
}

// 选中项变化
const handleSelectionChange = (val) => {
  selection.value = val
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 新增按钮点击
const handleAdd = () => {
  dialog.title = '新增用户'
  dialog.type = 'add'
  dialog.visible = true
  resetFormState()
}

// 编辑按钮点击
const handleEdit = (row) => {
  dialog.title = '编辑用户'
  dialog.type = 'edit'
  dialog.visible = true
  // 回显数据 (注意深拷贝或逐个赋值)
  Object.assign(form, row)
  // 清空密码，编辑时不展示原密码也不默认修改
  form.password = ''
}

// 删除按钮点击
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除用户 "${row.username}" 吗？`,
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
    getList()
  })
  .catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  if (selection.value.length === 0) return

  const ids = selection.value.map(item => item.id)

  ElMessageBox.confirm(
    `确认批量删除 ${ids.length} 个用户吗？`,
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
    getList()
  })
  .catch(() => {})
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialog.type === 'add') {
          await addUser(form)
          ElMessage.success('新增成功')
        } else {
          await updateUser(form)
          ElMessage.success('修改成功')
        }
        dialog.visible = false
        getList()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

// 仅用于状态开关展示，实际修改状态可以单独加个 API，也可以走 update 接口
const handleStatusChange = (row) => {
  // 当前后端接口未单独提供状态修改，且 update 接口比较重，这里暂不自动保存
  // 若需要，可调用 updateUser(row)
  // 但要注意数据回显和异常处理
  console.log('Status changed:', row.id, row.status)
}

// 重置表单状态
const resetFormState = () => {
  form.id = null
  form.username = ''
  form.nickname = ''
  form.phone = ''
  form.password = ''
  form.gender = 1
  form.status = 1
}

// Dialog 关闭回调：重置校验结果
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.user-list-container {
  /* padding: 20px; */
}

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

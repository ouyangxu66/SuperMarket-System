import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * Axios 请求封装
 * 统一处理请求和响应拦截，简化 API 调用
 */
// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 基础 URL，配合 vite 代理转发请求到后端
  timeout: 5000 // 请求超时时间
})

// 请求拦截器
// 在发送请求之前做一些处理，例如添加 Token
service.interceptors.request.use(
  config => {
    // 每次发送请求之前判断是否存在token
    // 如果存在，则统一在http请求的header都加上token，后端JWT认证需要
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// 响应拦截器
// 在接收到响应之后做一些处理，例如统一错误处理
service.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码不是200，说明业务逻辑报错（根据后端约定的结果结构）
    if (res.code !== 200) {
      ElMessage.error(res.message || '系统错误')

      // 可以在这里处理 token 过期等情况
      // if (res.code === 401) { ... }

      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      // 业务请求成功，返回数据部分
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    // HTTP 状态码错误处理
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service

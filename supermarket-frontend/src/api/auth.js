import request from '@/utils/request'

/**
 * 认证模块 API
 * 包含登录、获取用户信息、退出登录等接口调用
 */

/**
 * 用户登录
 * @param {Object} data - 登录信息 { username, password }
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前登录用户信息
 * @returns {Promise}
 */
export function getInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

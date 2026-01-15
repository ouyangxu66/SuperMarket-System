import request from '@/utils/request'

/**
 * 用户管理模块 API
 */

/**
 * 分页查询用户列表
 * @param {Object} query - 查询参数 { pageNum, pageSize, username }
 * @returns {Promise}
 */
export function getUserPage(query) {
  return request({
    url: '/user/page',
    method: 'get',
    params: query
  })
}

/**
 * 新增用户
 * @param {Object} data - 用户信息
 * @returns {Promise}
 */
export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

/**
 * 修改用户
 * @param {Object} data - 用户信息（必须包含 id）
 * @returns {Promise}
 */
export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 * @param {Number} id - 用户 ID
 * @returns {Promise}
 */
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除用户
 * @param {Array} ids - 用户 ID 数组
 * @returns {Promise}
 */
export function deleteUserBatch(ids) {
  return request({
    url: '/user/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 根据 ID 获取用户详情
 * @param {Number} id
 * @returns {Promise}
 */
export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}


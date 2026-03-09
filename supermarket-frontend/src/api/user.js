import request from '@/utils/request'

/**
 * 员工管理模块 API
 * 说明：当前后端仍沿用 /user 路径，以兼容现有认证与权限链路
 */

/**
 * 分页查询员工列表
 * @param {Object} query - 查询参数 { pageNum, pageSize, username, keyword }
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
 * 新增员工
 * @param {Object} data - 员工信息
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
 * 修改员工
 * @param {Object} data - 员工信息（必须包含 id）
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
 * 删除员工
 * @param {Number} id - 员工 ID
 * @returns {Promise}
 */
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除员工
 * @param {Array} ids - 员工 ID 数组
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
 * 根据 ID 获取员工详情
 * @param {Number} id
 * @returns {Promise}
 */
export function getUserById(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

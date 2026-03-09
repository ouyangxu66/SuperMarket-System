import request from '@/utils/request'

/**
 * 会员管理模块 API
 */

/**
 * 分页查询会员列表
 * @param {Object} query
 * @returns {Promise}
 */
export function getMemberPage(query) {
  return request({
    url: '/member/page',
    method: 'get',
    params: query
  })
}

/**
 * 根据 ID 获取会员详情
 * @param {number} id
 * @returns {Promise}
 */
export function getMemberById(id) {
  return request({
    url: `/member/${id}`,
    method: 'get'
  })
}

/**
 * 新增会员
 * @param {Object} data
 * @returns {Promise}
 */
export function addMember(data) {
  return request({
    url: '/member',
    method: 'post',
    data
  })
}

/**
 * 修改会员
 * @param {Object} data
 * @returns {Promise}
 */
export function updateMember(data) {
  return request({
    url: '/member',
    method: 'put',
    data
  })
}

/**
 * 删除会员
 * @param {number} id
 * @returns {Promise}
 */
export function deleteMember(id) {
  return request({
    url: `/member/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除会员
 * @param {Array<number>} ids
 * @returns {Promise}
 */
export function deleteMemberBatch(ids) {
  return request({
    url: '/member/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 快速查询会员
 * @param {Object} query
 * @returns {Promise}
 */
export function getSimpleMember(query) {
  return request({
    url: '/member/simple',
    method: 'get',
    params: query
  })
}

/**
 * 会员充值
 * @param {Object} data
 * @returns {Promise}
 */
export function rechargeMemberBalance(data) {
  return request({
    url: '/member/balance/recharge',
    method: 'post',
    data
  })
}

/**
 * 会员余额调整
 * @param {Object} data
 * @returns {Promise}
 */
export function adjustMemberBalance(data) {
  return request({
    url: '/member/balance/adjust',
    method: 'post',
    data
  })
}

/**
 * 分页查询会员余额流水
 * @param {Object} query
 * @returns {Promise}
 */
export function getMemberBalanceFlowPage(query) {
  return request({
    url: '/member/balance/flow/page',
    method: 'get',
    params: query
  })
}

/**
 * 会员积分调整
 * @param {Object} data
 * @returns {Promise}
 */
export function adjustMemberPoint(data) {
  return request({
    url: '/member/point/adjust',
    method: 'post',
    data
  })
}

/**
 * 分页查询会员积分流水
 * @param {Object} query
 * @returns {Promise}
 */
export function getMemberPointFlowPage(query) {
  return request({
    url: '/member/point/flow/page',
    method: 'get',
    params: query
  })
}

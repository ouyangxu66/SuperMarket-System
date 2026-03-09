import request from '@/utils/request'

/**
 * 销售收银模块 API
 */

/**
 * 收银台结算
 * @param {Object} data
 * @returns {Promise}
 */
export function checkout(data) {
  return request({
    url: '/sale/checkout',
    method: 'post',
    data
  })
}

/**
 * 分页查询销售订单
 * @param {Object} query
 * @returns {Promise}
 */
export function getSalePage(query) {
  return request({
    url: '/sale/page',
    method: 'get',
    params: query
  })
}

/**
 * 获取销售订单详情
 * @param {number} id
 * @returns {Promise}
 */
export function getSaleDetail(id) {
  return request({
    url: `/sale/${id}`,
    method: 'get'
  })
}

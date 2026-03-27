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
 * 销售流水查询
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
 * 销售详情
 * @param {Number} id
 * @returns {Promise}
 */
export function getSaleDetail(id) {
  return request({
    url: `/sale/${id}`,
    method: 'get'
  })
}

/**
 * 导出销售流水
 * @param {Object} query
 * @returns {Promise}
 */
export function exportSale(query) {
  return request({
    url: '/sale/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

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

/**
 * 商品销售明细分页查询
 * @param {Object} query
 * @returns {Promise}
 */
export function getProductSalePage(query) {
  return request({
    url: '/sale/product/page',
    method: 'get',
    params: query
  })
}

/**
 * 导出商品销售明细
 * @param {Object} query
 * @returns {Promise}
 */
export function exportProductSale(query) {
  return request({
    url: '/sale/product/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

/**
 * 导出商品销售总计
 * @param {Object} query
 * @returns {Promise}
 */
export function exportProductSaleSummary(query) {
  return request({
    url: '/sale/product/export-summary',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
/**
 * 退货处理
 * @param {Object} data - { orderId: number, reason: string }
 * @returns {Promise}
 */
export function refundOrder(data) {
  return request({
    url: '/sale/refund',
    method: 'post',
    data
  })
}
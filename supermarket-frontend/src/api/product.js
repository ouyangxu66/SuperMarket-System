import request from '@/utils/request'

/**
 * 商品管理模块 API
 */

/**
 * 分页查询商品列表
 * @param {Object} query - 查询参数 { pageNum, pageSize, name, categoryId, status }
 * @returns {Promise}
 */
export function getProductPage(query) {
  return request({
    url: '/product/page',
    method: 'get',
    params: query
  })
}

/**
 * 新增商品
 * @param {Object} data - 商品信息
 * @returns {Promise}
 */
export function addProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

/**
 * 修改商品
 * @param {Object} data - 商品信息
 * @returns {Promise}
 */
export function updateProduct(data) {
  return request({
    url: '/product',
    method: 'put',
    data
  })
}

/**
 * 修改商品状态 (上架/下架)
 * @param {Number} id - 商品ID
 * @param {Number} status - 状态 0:下架 1:上架
 * @returns {Promise}
 */
export function updateProductStatus(id, status) {
  return request({
    url: `/product/${id}/status/${status}`,
    method: 'put'
  })
}

/**
 * 删除商品
 * @param {Number} id - 商品ID
 * @returns {Promise}
 */
export function deleteProduct(id) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

/**
 * 根据条码查询商品
 * @param {String} barcode - 商品条码
 * @returns {Promise}
 */
export function getProductByBarcode(barcode) {
  return request({
    url: `/product/barcode/${barcode}`,
    method: 'get'
  })
}

/**
 * 获取即将过期的商品列表
 * @param {Number} days - 临期天数 默认7
 * @returns {Promise}
 */
export function getExpiringProducts(days = 7) {
  return request({
    url: '/product/expiring-soon',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取已过期的商品列表
 * @returns {Promise}
 */
export function getExpiredProducts() {
  return request({
    url: '/product/expired',
    method: 'get'
  })
}

/**
 * 获取分类树形结构 (商品列表筛选用)
 * @returns {Promise}
 */
export function getCategoryTree() {
  return request({
    url: '/product/category/tree',
    method: 'get'
  })
}

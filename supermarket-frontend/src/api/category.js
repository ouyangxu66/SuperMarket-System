import request from '@/utils/request'

/**
 * 获取分类树
 */
export function getCategoryTree() {
  return request({
    url: '/product/category/tree',
    method: 'get'
  })
}

/**
 * 新增分类
 */
export function addCategory(data) {
  return request({
    url: '/product/category',
    method: 'post',
    data
  })
}

/**
 * 修改分类
 */
export function updateCategory(data) {
  return request({
    url: '/product/category',
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request({
    url: `/product/category/${id}`,
    method: 'delete'
  })
}

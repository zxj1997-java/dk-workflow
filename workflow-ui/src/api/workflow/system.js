import request from '@/utils/request'

export const systemApi = {
  // 获取用户列表
  getUsers() {
    return request({
      url: '/api/system/users',
      method: 'get'
    })
  },

  // 获取部门列表
  getDepartments() {
    return request({
      url: '/api/system/departments',
      method: 'get'
    })
  }
}
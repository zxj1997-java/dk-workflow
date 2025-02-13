import request from '@/utils/request'

export function getUsers() {
  return request({
    url: '/api/system/users',
    method: 'get'
  })
}

export function getDepartments() {
  return request({
    url: '/api/system/departments',
    method: 'get'
  })
} 
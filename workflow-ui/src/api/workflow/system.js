import request from '@/utils/request'

// 获取用户列表
export function getUsers() {
    return request({
        url: '/api/system/users',
        method: 'get'
    })
}


// 获取部门列表
export function getDepartments() {
    return request({
        url: '/api/system/departments',
        method: 'get'
    })
}

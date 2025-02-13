import request from '@/utils/request'

export function createTransition(data) {
  return request({
    url: '/api/workflow/transition',
    method: 'post',
    data
  })
}

export function updateTransition(data) {
  return request({
    url: `/api/workflow/transition/${data.id}`,
    method: 'put',
    data
  })
} 
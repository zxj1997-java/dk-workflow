import request from '@/utils/request'

export function createActivity(data) {
  return request({
    url: '/api/workflow/activity',
    method: 'post',
    data
  })
}

export function updateActivity(data) {
  return request({
    url: `/api/workflow/activity/${data.id}`,
    method: 'put',
    data
  })
} 
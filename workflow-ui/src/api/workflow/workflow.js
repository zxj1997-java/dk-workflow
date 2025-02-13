import request from '@/utils/request'

// 工作流基础操作
export function saveWorkflow(data) {
  const url = data.id ? `/api/workflow/${data.id}` : '/api/workflow/save'
  return request({
    url,
    method: 'post',
    data
  })
}

export function getWorkflowList() {
  return request({
    url: '/api/workflow/list',
    method: 'get'
  })
}

export function getWorkflowById(id) {
  return request({
    url: `/api/workflow/${id}`,
    method: 'get'
  })
}

export function publishWorkflow(id) {
  return request({
    url: `/api/workflow/${id}/publish`,
    method: 'post'
  })
} 
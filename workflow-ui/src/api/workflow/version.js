import request from '@/utils/request'

export function getWorkflowVersions(id) {
  return request({
    url: `/api/workflow/${id}/versions`,
    method: 'get'
  })
}

export function getWorkflowVersion(workflowId, version) {
  return request({
    url: `/api/workflow/${workflowId}/version/${version}`,
    method: 'get'
  })
} 
import request from '@/utils/request'

export function getWorkflowInstance(instanceId) {
  return request({
    url: `/api/workflow/instance/${instanceId}`,
    method: 'get'
  })
}

export function getApprovalRecords(instanceId) {
  return request({
    url: `/api/workflow/instance/${instanceId}/approvals`,
    method: 'get'
  })
} 
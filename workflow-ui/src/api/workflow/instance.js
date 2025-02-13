import request from '@/utils/request'

export const instanceApi = {
  // 获取工作流实例
  getWorkflowInstance(instanceId) {
    return request({
      url: `/api/workflow/instance/${instanceId}`,
      method: 'get'
    })
  },

  // 获取工作流的实例列表
  getWorkflowInstances(workflowId) {
    return request({
      url: `/api/workflow/instance/list/${workflowId}`,
      method: 'get'
    })
  },

  // 获取审批记录
  getApprovalRecords(instanceId) {
    return request({
      url: `/api/workflow/instance/${instanceId}/approvals`,
      method: 'get'
    })
  }
}

export default instanceApi 
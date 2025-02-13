// 工作流相关的 API 请求
import request from '@/utils/request'

export const workflowApi = {
  // 保存工作流
  saveWorkflow(data) {
    const url = data.id ? `/api/workflow/${data.id}` : '/api/workflow/save'
    return request({
      url,
      method: 'post',
      data
    })
  },
  
  // 获取工作流列表
  getWorkflowList() {
    return request({
      url: '/api/workflow/list',
      method: 'get'
    })
  },
  
  // 获取工作流详情
  getWorkflowById(id) {
    return request({
      url: `/api/workflow/${id}`,
      method: 'get'
    })
  },
  
  // 测试接口
  test() {
    return request({
      url: '/api/workflow/test',
      method: 'get'
    })
  },

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
  },

  // 发布工作流
  publishWorkflow(id) {
    return request({
      url: `/api/workflow/${id}/publish`,
      method: 'post'
    })
  },

  // 获取工作流版本列表
  getWorkflowVersions(id) {
    return request({
      url: `/api/workflow/${id}/versions`,
      method: 'get'
    })
  },

  // 获取工作流指定版本
  getWorkflowVersion(workflowId, version) {
    return request({
      url: `/api/workflow/${workflowId}/version/${version}`,
      method: 'get'
    })
  },

  // 获取工作流实例
  getWorkflowInstance(instanceId) {
    return request({
      url: `/api/workflow/instance/${instanceId}`,
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
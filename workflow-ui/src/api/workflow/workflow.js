import request from '@/utils/request'

// 工作流相关的 API 请求
// 创建工作流
export function createWorkflow(data) {
    return request({
        url: '/api/workflow/create',
        method: 'post',
        data
    })
}


// 保存工作流
export function saveWorkflow(data) {
    const url = data.id ? `/api/workflow/${data.id}` : '/api/workflow/save'
    return request({
        url,
        method: 'post',
        data
    })
}


// 获取工作流列表
export function getWorkflowList() {
    return request({
        url: '/api/workflow/list',
        method: 'get'
    })
}


// 获取工作流详情
export function getWorkflowById(id) {
    return request({
        url: `/api/workflow/${id}`,
        method: 'get'
    })
}

// 发布工作流
export function publishWorkflow(id) {
    return request({
        url: `/api/workflow/${id}/publish`,
        method: 'post'
    })
}


// 获取工作流版本列表
export function getWorkflowVersions(id) {
    return request({
        url: `/api/workflow/${id}/versions`,
        method: 'get'
    })
}


// 获取工作流指定版本
export function getWorkflowVersion(workflowId, version) {
    return request({
        url: `/api/workflow/${workflowId}/version/${version}`,
        method: 'get'
    })
}

// 根据流程编码获取流程定义
export function getWorkflowByCode(code) {
    return request({
        url: `/api/workflow/code/${code}`,
        method: 'get'
    })
}

// 新增：通过业务ID查询运行时任务（审批记录）
export function getRuntimeTasks(businessId) {
    return request({
        url: `/api/workflow/runtime-tasks/${businessId}`,
        method: 'get'
    })
}


// 处理任务
export function processTasks(data) {
    return request({
        url: `/api/workflow/runtime-tasks/process/${data.id}`,
        method: 'POST',
        data: data
    })
}

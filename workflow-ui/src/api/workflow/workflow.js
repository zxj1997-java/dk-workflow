import request from '@/utils/request'

// 工作流相关的 API 请求
export const workflowApi = {
    // 创建工作流
    createWorkflow(data) {
        return request({
            url: '/api/workflow/create',
            method: 'post',
            data
        })
    },

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
    // 根据流程编码获取流程定义
    getWorkflowByCode(code) {
        return request({
            url: `/api/workflow/code/${code}`,
            method: 'get'
        })
    }
}

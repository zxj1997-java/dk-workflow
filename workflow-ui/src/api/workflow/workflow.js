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
        return request({
            url: '/api/workflow/update',
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
            url: '/api/workflow/detail',
            method: 'get',
            params: { id }
        })
    },

    // 发布工作流
    publishWorkflow(id) {
        return request({
            url: '/api/workflow/publish',
            method: 'post',
            params: { id }
        })
    },

    // 根据流程编码获取流程定义
    getWorkflowByCode(code) {
        return request({
            url: '/api/workflow/code',
            method: 'get',
            params: { code }
        })
    },
    
    // 获取工作流详细信息（用于导出）
    getWorkflowDetail: (id) => {
        return request({
            url: '/api/workflow/export',
            method: 'get',
            params: { id }
        })
    },
     // 删除工作流
     deleteWorkflow: (id) => {
        return request({
            url: '/api/workflow/delete',
            method: 'delete',
            params: { id }
        })
    }
}

import request from '@/utils/request'

// 获取工作流版本
export function getWorkflowVersionByWorkflowIdId(workflowId, version) {
    return request({
        url: `/api/workflow/${workflowId}/version/${version}`,
        method: 'get'
    })
}

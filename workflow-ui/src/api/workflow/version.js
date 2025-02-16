import request from '@/utils/request'

export const versionApi = {
    // 获取工作流版本
    getWorkflowVersionByWorkflowIdId(workflowId, version) {
        return request({
            url: `/api/workflow/version`,
            method: 'get',
            params: {id: workflowId, version: version}
        })
    }
}
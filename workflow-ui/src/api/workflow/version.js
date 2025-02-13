import request from '@/utils/request'
import instanceApi from "@/api/workflow/instance";

export const versionApi={
    // 获取工作流版本
    getWorkflowVersion(id) {
        return request({
            url: `/api/workflow/${id}/versions`,
            method: 'get'
        })
    },

    // 获取工作流版本
    getWorkflowVersionByWorkflowIdId(workflowId, version) {
        return request({
            url: `/api/workflow/${workflowId}/version/${version}`,
            method: 'get'
        })
    }
}
export default instanceApi
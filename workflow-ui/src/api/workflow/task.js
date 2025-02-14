import request from '@/utils/request'

/**
 * 任务相关 API 接口
 */
export const taskApi = {

    // 新增：通过业务ID查询运行时任务（审批记录）
    getRuntimeTasks(businessId) {
        return request({
            url: `/api/workflow/runtime-tasks/${businessId}`,
            method: 'get'
        })
    },

    // 处理任务
    processTasks(data) {
        return request({
            url: `/api/workflow/runtime-tasks/process/${data.id}`,
            method: 'POST',
            data: data
        })
    },
    /**
     * 获取任务操作配置
     * @param {number|string} taskId 任务 ID
     * @returns {Promise} 返回任务操作配置数组，例如：
     * [{ value: "APPROVED", label: "同意" }, { value: "DISAPPROVED", label: "拒绝" }, ...]
     */
    getTaskOperations(taskId) {
        return request({
            url: `/api/workflow/task/operations/${taskId}`,
            method: 'get'
        })
    },

    /**
     * 处理任务
     * @param {object} data 包含任务处理所需的参数，如 { id, action, comment }
     * @returns {Promise}
     */
    processTask(data) {
        return request({
            url: `/api/workflow/task/process/${data.id}`,
            method: 'post',
            data
        })
    }
} 
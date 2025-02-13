import request from '@/utils/request'

// 获取我的申请列表
export function getMyApplications(params) {
  return request({
    url: '/workflow/leave/my-applications',
    method: 'get',
    params
  })
}

// 获取待办任务列表
export function getTodoTasks(params) {
  return request({
    url: '/workflow/leave/todo-tasks',
    method: 'get',
    params
  })
}

// 获取已办任务列表
export function getDoneTasks(params) {
  return request({
    url: '/workflow/leave/done-tasks',
    method: 'get',
    params
  })
}

// 保存草稿
export function saveDraft(data) {
  return request({
    url: '/workflow/leave/save-draft',
    method: 'post',
    data
  })
}

// 提交申请
export function submitLeave(data) {
  return request({
    url: '/workflow/leave/submit',
    method: 'post',
    data
  })
}

// 获取申请详情
export function getLeaveDetail(id) {
  return request({
    url: `/workflow/leave/detail/${id}`,
    method: 'get'
  })
}

// 删除申请
export function deleteLeave(id) {
  return request({
    url: `/workflow/leave/${id}`,
    method: 'delete'
  })
} 
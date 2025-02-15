package vip.lsjscl.flowboot.starter.flow.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.starter.flow.dict.TaskDecision;
import vip.lsjscl.flowboot.starter.flow.dict.TaskStatus;
import vip.lsjscl.flowboot.starter.flow.dto.ProcessDataDto;
import vip.lsjscl.flowboot.starter.flow.entity.Activity;
import vip.lsjscl.flowboot.starter.flow.entity.HistoryTask;
import vip.lsjscl.flowboot.starter.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.starter.flow.repository.HistoryTaskRepository;
import vip.lsjscl.flowboot.starter.flow.repository.RuntimeTaskRepository;
import vip.lsjscl.flowboot.starter.flow.service.ActivityService;
import vip.lsjscl.flowboot.starter.flow.service.TaskService;
import vip.lsjscl.flowboot.starter.common.R;
import vip.lsjscl.flowboot.starter.flow.entity.Workflow;
import vip.lsjscl.flowboot.starter.flow.repository.WorkflowRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务控制器，用于查询运行时任务（审批记录）
 *
 * @author zhangxingju
 * @date 2025/02/14
 */
@RestController
@RequestMapping("/api/workflow")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class TaskController {

    private final RuntimeTaskRepository runtimeTaskRepository;

    private final TaskService taskService;

    private final ActivityService activityService;

    private final WorkflowRepository workflowRepository;

    private final HistoryTaskRepository historyTaskRepository;

    /**
     * 根据业务ID查询运行时任务
     * 请求示例: GET /api/workflow/runtime-tasks/{businessId}
     *
     * @param businessId 请假申请记录ID（业务ID）
     * @return 运行时任务列表封装在 R 对象中
     */
    @GetMapping("/runtime-tasks/{businessId}")
    public R getRuntimeTasks(@PathVariable("businessId") String businessId) {
        List<RuntimeTask> tasks = taskService.getTasksByBusinessId(businessId);
        return R.ok(tasks);
    }


    /**
     * 根据业务ID查询运行时任务
     *
     * @return 运行时任务列表封装在 R 对象中
     */
    @PostMapping("/runtime-tasks/process/{businessId}")
    public R processTasks(@PathVariable String businessId, @RequestBody ProcessDataDto processDataDto) {
        Optional<RuntimeTask> byBusinessIdAndStatus = runtimeTaskRepository.findByBusinessIdAndStatus(businessId, TaskStatus.PENDING);
        RuntimeTask runtimeTask = byBusinessIdAndStatus.get();
        activityService.updateCurrentTaskActivity(processDataDto);
        return R.ok().put("data", runtimeTask);
    }

    /**
     * 获取任务操作配置接口
     * 例如访问: GET /api/workflow/task/operations/2
     * 返回示例数据:
     * {
     * "data": [
     * {"value": "APPROVED", "label": "同意"},
     * {"value": "DISAPPROVED", "label": "拒绝"},
     * {"value": "RETURN_PREVIOUS", "label": "退回上一步"},
     * {"value": "RETURN_APPLICANT", "label": "退回申请人"}
     * ],
     * "code": 0,
     * "msg": "success"
     * }
     */
    @GetMapping("/task/operations/{taskId}")
    public R getTaskOperations(@PathVariable("taskId") Long taskId) {
        // 查找任务，如果不存在则抛出异常
        RuntimeTask runtimeTask = runtimeTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("任务不存在，任务ID: " + taskId));

        // 获取活动操作列表
        Activity activity = runtimeTask.getActivity();
        String operations = activity.getOperations();
        if (StringUtils.isEmpty(operations)) {
            return R.ok().put("data", Collections.emptyList());
        }

        // 解析操作列表并封装返回数据
        List<Map<String, String>> operationList = Arrays.stream(StringUtils.split(operations, ","))
                .map(operation -> {
                    String label = TaskDecision.getOperateNameByName(operation);
                    Map<String, String> opMap = new HashMap<>();
                    opMap.put("value", operation);
                    opMap.put("label", label != null ? label : "未知操作");
                    return opMap;
                })
                .collect(Collectors.toList());

        return R.ok().put("data", operationList);
    }

    /**
     * 查询用户的待办任务
     */
    @GetMapping("/tasks/todo")
    public R getTodoTasks(@RequestParam(required = false) String userId, 
                         @RequestParam(required = false) String deptId) {
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(deptId)) {
            return R.error("用户ID和部门ID不能同时为空");
        }
        return R.ok().put("data", taskService.getTodoTasks(userId, deptId));
    }

    /**
     * 查询用户的已办任务
     */
    @GetMapping("/tasks/done")
    public R getDoneTasks(@RequestParam(required = false) String userId, 
                         @RequestParam(required = false) String deptId) {
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(deptId)) {
            return R.error("用户ID和部门ID不能同时为空");
        }
        return R.ok().put("data", taskService.getDoneTasks(userId, deptId));
    }

    /**
     * 查询用户的所有相关任务（包括待办和已办）
     */
    @GetMapping("/tasks/all")
    public R getAllTasks(@RequestParam(required = false) String userId, 
                        @RequestParam(required = false) String deptId) {
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(deptId)) {
            return R.error("用户ID和部门ID不能同时为空");
        }
        return R.ok().put("data", taskService.getAllTasks(userId, deptId));
    }

    /**
     * 获取流程当前活动的页面路径和操作按钮
     * @param code 流程编码
     * @param businessId 业务ID
     * @return 页面路径和操作按钮信息
     */
    @GetMapping("/activity-info")
    public R getActivityInfo(@RequestParam String code, @RequestParam String businessId) {
        // 1. 根据流程编码获取最新版本的工作流
        Workflow workflow = workflowRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("流程不存在"));
        
        // 2. 获取当前运行时任务
        RuntimeTask currentTask = runtimeTaskRepository
            .findByBusinessIdAndStatus(businessId, TaskStatus.PENDING)
            .orElse(null);

        if (currentTask == null) {
            // 如果没有运行时任务，查询历史任务
            List<HistoryTask> historyTasks = historyTaskRepository.findByBusinessId(businessId);
            if (!historyTasks.isEmpty()) {
                // 返回最后一个历史任务的信息
                HistoryTask lastTask = historyTasks.get(historyTasks.size() - 1);
                return R.ok().put("data", Map.of(
                    "pageUrl", lastTask.getActivity().getPageUrl(),
                    "operations", Collections.emptyList(),
                    "status", lastTask.getStatus()
                ));
            }
            return R.error("未找到相关任务");
        }

        // 3. 获取当前活动节点的页面路径和操作按钮
        Activity activity = currentTask.getActivity();
        List<Map<String, String>> operations = Arrays.stream(activity.getOperations().split(","))
            .map(op -> {
                Map<String, String> opMap = new HashMap<>();
                opMap.put("value", op);
                opMap.put("label", TaskDecision.getOperateNameByName(op));
                return opMap;
            })
            .collect(Collectors.toList());

        return R.ok().put("data", Map.of(
            "pageUrl", activity.getPageUrl(),
            "operations", operations,
            "status", currentTask.getStatus()
        ));
    }
} 
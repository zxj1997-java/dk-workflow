package vip.lsjscl.flowboot.flow.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.dict.TaskDecision;
import vip.lsjscl.flowboot.flow.dict.TaskStatus;
import vip.lsjscl.flowboot.flow.dto.ProcessDataDto;
import vip.lsjscl.flowboot.flow.entity.Activity;
import vip.lsjscl.flowboot.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.flow.repository.RuntimeTaskRepository;
import vip.lsjscl.flowboot.flow.service.ActivityService;
import vip.lsjscl.flowboot.leave.common.utils.R;

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
@CrossOrigin(origins = "http://localhost:8081")  // 根据需要配置允许的跨域访问
public class TaskController {

    @Autowired
    private RuntimeTaskRepository runtimeTaskRepository;

    @Autowired
    private ActivityService activityService;

    /**
     * 根据业务ID查询运行时任务
     * 请求示例: GET /api/workflow/runtime-tasks/{businessId}
     *
     * @param businessId 离职申请记录ID（业务ID）
     * @return 运行时任务列表封装在 R 对象中
     */
    @GetMapping("/runtime-tasks/{businessId}")
    public R getRuntimeTasks(@PathVariable("businessId") String businessId) {
        List<RuntimeTask> tasks = runtimeTaskRepository.findByBusinessId(businessId);
        return R.ok().put("data", tasks);
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

} 
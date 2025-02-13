package vip.lsjscl.flowboot.flow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vip.lsjscl.flowboot.flow.entity.RuntimeTask;
import vip.lsjscl.flowboot.flow.repository.RuntimeTaskRepository;
import vip.lsjscl.flowboot.leave.common.utils.R;

import java.util.List;

/**
 * 任务控制器，用于查询运行时任务（审批记录）
 */
@RestController
@RequestMapping("/api/workflow/runtime-tasks")
@CrossOrigin(origins = "http://localhost:8081")  // 根据需要配置允许的跨域访问
public class TaskController {

    @Autowired
    private RuntimeTaskRepository runtimeTaskRepository;

    /**
     * 根据业务ID查询运行时任务
     * 请求示例: GET /api/workflow/runtime-tasks/{businessId}
     *
     * @param businessId 离职申请记录ID（业务ID）
     * @return 运行时任务列表封装在 R 对象中
     */
    @GetMapping("/{businessId}")
    public R getRuntimeTasks(@PathVariable("businessId") String businessId) {
        List<RuntimeTask> tasks = runtimeTaskRepository.findByBusinessId(businessId);
        return R.ok().put("data", tasks);
    }
} 
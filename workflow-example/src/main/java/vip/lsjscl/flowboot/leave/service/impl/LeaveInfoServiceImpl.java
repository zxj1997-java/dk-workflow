package vip.lsjscl.flowboot.leave.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.lsjscl.flowboot.config.SpringContextUtil;
import vip.lsjscl.flowboot.starter.common.R;
import vip.lsjscl.flowboot.leave.entity.LeaveInfo;
import vip.lsjscl.flowboot.leave.repository.LeaveInfoRepository;
import vip.lsjscl.flowboot.leave.service.LeaveInfoService;
import vip.lsjscl.flowboot.starter.flow.service.WorkflowService;
import vip.lsjscl.flowboot.starter.service.DeleteStrategyService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Transactional
@Service
public class LeaveInfoServiceImpl implements LeaveInfoService {

    @Autowired
    private LeaveInfoRepository leaveInfoRepository;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private DeleteStrategyService deleteStrategyService;

    @Override
    public R getMyApplications(Map<String, Object> params) {
        // 获取当前用户的所有申请
        List<LeaveInfo> applications = leaveInfoRepository.findAll();
        return R.ok().put("data", applications);
    }

    @Override
    public R getTodoTasks(Map<String, Object> params) {
        // 获取待处理的申请
        List<LeaveInfo> todoTasks = leaveInfoRepository.findByStatus("PENDING");
        return R.ok().put("data", todoTasks);
    }

    @Override
    public R getDoneTasks(Map<String, Object> params) {
        // 获取已处理的申请
        List<LeaveInfo> doneTasks = leaveInfoRepository.findByStatusIn(List.of("APPROVED", "REJECTED"));
        return R.ok().put("data", doneTasks);
    }

    @Override
    public R submitLeaveApplication(LeaveInfo leaveInfo) {
        try {
            // 设置状态为待处理
            leaveInfo.setStatus("PENDING");
            LeaveInfo saved = leaveInfoRepository.save(leaveInfo);
            // 初始化工作流，传入业务ID
            String leave = workflowService.startWorkflow("leave", saved.getId().toString());
            saved.setWorkflowVersionId(leave);
            return R.ok().put("data", saved);
        }
        catch (Exception e) {
            return R.error("提交申请失败");
        }
    }


    @Override
    public R saveOrUpdate(LeaveInfo leaveInfo) {
        try {
            LeaveInfo saved = leaveInfoRepository.save(leaveInfo);
            return R.ok().put("data", saved);
        }
        catch (Exception e) {
            return R.error("保存失败");
        }
    }

    @Override
    public R getLeaveDetail(String id) {
        try {
            Optional<LeaveInfo> optionalLeave = leaveInfoRepository.findById(id);
            if (optionalLeave.isEmpty()) {
                return R.error("申请不存在");
            }
            return R.ok().put("data", optionalLeave.get());
        }
        catch (Exception e) {
            return R.error("获取详情失败");
        }
    }

    @Override
    public R deleteLeaveApplication(String id) {
        try {
            Optional<LeaveInfo> optionalLeave = leaveInfoRepository.findById(id);
            if (optionalLeave.isEmpty()) {
                return R.error("申请不存在");
            }

            LeaveInfo leaveInfo = optionalLeave.get();
            // 只能删除草稿状态的申请
            if (!"DRAFT".equals(leaveInfo.getStatus())) {
                return R.error("只能删除草稿状态的申请");
            }

            deleteStrategyService.delete(leaveInfoRepository, id);
            return R.ok();
        }
        catch (Exception e) {
            return R.error("删除失败");
        }
    }
} 
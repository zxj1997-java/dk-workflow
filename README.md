# 轻量级工作流引擎(登科流程引擎)

一个基于 Spring Boot 的轻量级工作流引擎，支持灵活的流程定义、任务处理和流程追踪。

TODO 
1. 写一个通用接口,传入流程编码、业务id返回对应活动里配置的页面路径 
2. 写一个通用的js插件,便于引入到原生的html,可以显示活动配置的按钮,以及流程进度显示

## 技术栈

- 前端：Vue3、Element-plus、Antv-X6
- 后端：Spring Boot、Spring Data JPA
- 数据库：MySQL

## 功能特性

- 支持流程定义和版本管理
- 支持多种任务处理方式（审批、驳回、退回等）
- 支持按用户/部门查询待办和已办任务
- 支持流程历史记录追踪
- 支持自定义表单和业务集成
- 支持定时任务自动同步完成的流程

## 核心概念

### 1. 工作流定义 (Workflow)
- 定义工作流的基本信息，如名称、编码等
- 支持版本控制，可以管理多个版本的流程定义
- 使用 JSON 格式存储流程定义数据

### 2. 活动节点 (Activity)
- 工作流中的处理节点
- 支持配置审批人、审批部门
- 支持配置节点操作权限
- 支持配置后置处理类

### 3. 流转关系 (Transition)
- 定义节点间的流转关系
- 支持配置流转条件
- 支持多条流转路径

### 4. 任务 (Task)
- 运行时任务 (RuntimeTask)：当前正在处理的任务
- 历史任务 (HistoryTask)：已经处理完成的任务记录

## 数据库设计

### 核心表
1. dk_workflow：工作流定义表
2. dk_workflow_version：工作流版本表
3. dk_activity：活动节点表
4. dk_transition：流转关系表
5. dk_runtime_task：运行时任务表
6. dk_history_task：历史任务表

## API 接口

### 任务相关接口
```
# 查询待办任务
GET /api/workflow/tasks/todo?userId={userId}&deptId={deptId}

# 查询已办任务
GET /api/workflow/tasks/done?userId={userId}&deptId={deptId}

# 查询所有任务
GET /api/workflow/tasks/all?userId={userId}&deptId={deptId}

# 获取任务操作配置
GET /api/workflow/task/operations/{taskId}

# 处理任务
POST /api/workflow/runtime-tasks/process/{businessId}
```

## 使用示例

### 1. 定义工作流
```java
Workflow workflow = new Workflow();
workflow.setName("请假申请流程");
workflow.setCode("LEAVE_PROCESS");
workflow.setStatus(1);
```

### 2. 定义活动节点
```java
Activity activity = new Activity();
activity.setName("部门经理审批");
activity.setCode("DEPT_MANAGER_APPROVAL");
activity.setApprovers("1001,1002");
activity.setOperations("APPROVED,DISAPPROVED,RETURN_PREVIOUS");
```

### 3. 创建任务
```java
RuntimeTask task = new RuntimeTask();
task.setActivity(activity);
task.setBusinessId("LEAVE-2024-001");
task.setStatus(TaskStatus.PENDING);
```

## 配置说明

### 1. 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workflow
    username: root
    password: root
```

### 2. 定时任务配置
```yaml
spring:
  task:
    scheduling:
      pool:
        size: 5
```

## 注意事项

1. 活动节点的审批人和部门使用逗号分隔的字符串存储
2. 流程定义数据使用 JSON 格式存储，需要符合指定的格式要求
3. 建议在生产环境中配置适当的数据库连接池参数
4. 建议定期清理历史数据，避免数据量过大影响性能

## 常见问题

1. Q: 如何处理并发任务？
   A: 系统使用数据库事务确保任务处理的原子性

2. Q: 如何查询用户的待办任务？
   A: 使用 `/api/workflow/tasks/todo` 接口，传入 userId 或 deptId 参数

3. Q: 如何实现流程回退？
   A: 使用 `RETURN_PREVIOUS` 或 `RETURN_APPLICANT` 操作类型

## 后续规划

1. 支持图形化流程设计
2. 支持流程模板管理
3. 添加流程统计分析功能
4. 支持自定义节点处理器
5. 支持自定义流转条件
6. 优化性能和可扩展性

## 贡献指南

欢迎提交 Issue 和 Pull Request。在提交代码前，请确保：

1. 遵循现有的代码风格
2. 添加适当的单元测试
3. 更新相关文档

## 许可证

MIT License

## 系统展示
![flow_list.png](example%2Fflow_list.png)
![flow_config.png](example%2Fflow_config.png)
![activity_config.png](example%2Factivity_config.png)
![trans_list.png](example%2Ftrans_list.png)
![leave_list.png](example%2Fleave_list.png)
![process_view.png](example%2Fprocess_view.png)

## 最新更新内容 (2025/02/15)

### 1. 删除策略配置

现在支持通过配置选择使用物理删除或逻辑删除：

```yaml
# 工作流引擎配置
flow-boot:
  # 是否启用物理删除,默认为false(逻辑删除)
  physical-delete: false
```

- 默认使用逻辑删除（is_deleted = true）
- 配置 physical-delete = true 时使用物理删除
- 查询时自动过滤已删除数据

### 2. 用户信息获取机制

新增用户信息提供接口，便于集成系统用户体系：

```java
@Component
public class CustomUserInfoProvider implements UserInfoProvider {
    @Override
    public String getCurrentUserId() {
        // 实现获取当前用户ID的逻辑
        return "当前用户ID";
    }
    
    @Override
    public String getCurrentUsername() {
        // 实现获取当前用户名的逻辑
        return "当前用户名";
    }
}
```

特性：
- 自动记录数据创建和更新人
- 支持自定义用户信息获取逻辑
- 默认提供系统用户实现

### 3. JSON 数据处理优化

为工作流定义数据添加 JSON 转换支持：

```java
@Convert(converter = JsonConverter.class)
private Object flowData;
```

优势：
- 自动处理 JSON 序列化和反序列化
- 支持直接操作对象，无需手动转换
- 统一的异常处理机制

使用示例：
```java
// 保存数据
Workflow workflow = new Workflow();
Map<String, Object> flowData = new HashMap<>();
flowData.put("nodes", Arrays.asList("node1", "node2"));
workflow.setFlowData(flowData);

// 读取数据
Workflow workflow = workflowRepository.findById(id).orElse(null);
JsonNode flowData = (JsonNode) workflow.getFlowData();
String node1 = flowData.get("nodes").get(0).asText();
```

### 4. 其他优化

1. 实体关系优化：
   - 使用懒加载优化性能
   - 优化外键关系配置

2. 配置项统一：
   - 将配置前缀统一为 `flow-boot`
   - 支持更灵活的配置扩展

## 使用说明

### 1. 配置删除策略

在 application.yml 中添加：
```yaml
flow-boot:
  physical-delete: false  # true为物理删除，false为逻辑删除
```

### 2. 集成用户系统

创建自定义用户信息提供者：
```java
@Component
public class CustomUserInfoProvider implements UserInfoProvider {
    @Override
    public String getCurrentUserId() {
        // 实现用户ID获取逻辑
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    @Override
    public String getCurrentUsername() {
        // 实现用户名获取逻辑
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
```

### 3. 使用 JSON 转换

直接使用实体类即可，无需额外配置：
```java
Workflow workflow = new Workflow();
workflow.setFlowData(yourJsonObject);
workflowRepository.save(workflow);
```

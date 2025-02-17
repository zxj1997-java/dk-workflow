# 快速开始

本文将帮助你从零开始安装和使用登科流程引擎。

## 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Node.js 14+

## 安装步骤

### 1. 添加 Maven 依赖

```xml
<dependency>
    <groupId>vip.lsjscl</groupId>
    <artifactId>flowboot-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据库

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/workflow
    username: root
    password: root
```

### 3. 集成用户系统

```java
@Component
public class CustomUserInfoProvider implements UserInfoProvider {
    @Override
    public String getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    
    @Override
    public String getCurrentUsername() {
        return SecurityUtils.getCurrentUsername();
    }
}
```

## 前端集成

### Vue 项目

```bash
# 安装依赖
npm install @lsjscl/workflow-ui
```

```javascript
// main.js
import { createApp } from 'vue'
import WorkflowUI from '@lsjscl/workflow-ui'
import '@lsjscl/workflow-ui/dist/style.css'

const app = createApp(App)
app.use(WorkflowUI)
app.mount('#app')
```

### React 项目

```bash
npm install @lsjscl/workflow-ui
```

```jsx
import { WorkflowDesigner, WorkflowViewer } from '@lsjscl/workflow-ui';
import '@lsjscl/workflow-ui/dist/style.css';

function App() {
  return (
    <div>
      <WorkflowDesigner 
        workflowId="workflow-001"
        onSave={(data) => console.log('保存数据:', data)}
      />
    </div>
  );
}
```

## 下一步

- 了解[核心概念](/guide/workflow.html)
- 查看[配置说明](/config/)
- 浏览[API文档](/api/) 
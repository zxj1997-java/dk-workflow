---
home: true
heroImage: /images/logo.png
heroText: 登科流程引擎
tagline: 一个基于 Spring Boot 的轻量级工作流引擎
actions:
  - text: 快速开始 →
    link: /guide/getting-started.html
    type: primary
  - text: 项目简介
    link: /guide/
    type: secondary
features:
  - title: 简单易用
    details: 提供图形化流程设计器，零代码即可完成流程定义
  - title: 轻量灵活
    details: 基于 Spring Boot 开发，易于集成，支持自定义扩展
  - title: 功能完善
    details: 支持流程版本管理、任务处理、流程追踪等核心功能
footer: MIT Licensed | Copyright © 2024 登科流程引擎
---

## 快速开始

### 1. Maven 依赖

```xml
<dependency>
    <groupId>vip.lsjscl</groupId>
    <artifactId>flowboot-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 数据库配置

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

## 核心功能

- 图形化流程设计器
- 流程版本管理
- 多种任务处理方式
- 待办/已办任务查询
- 流程历史记录追踪
- 自定义表单集成

## 技术支持

- 问题反馈：[Gitee Issues](https://gitee.com/zhang_xing_ju/dk-workflow/issues)
- 交流：QQ 1533195362

## 许可证

本项目采用 MIT 协议开源。 
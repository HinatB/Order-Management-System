外企 Java 3 到 4 个月学习准备总纲

适用目标：每天学习 3 小时，在 3 到 4 个月内准备外企 Java 后端岗位。

核心策略：不要零散学习很多框架，而是围绕一个能讲清楚的后端项目，把 Java、Spring Boot、数据库、Kafka、Redis、测试、Docker/Kubernetes、可观测性和系统设计串起来。

## 当前项目进度

当前已完成第 1 个月的 `order-service` 定版。

服务级说明见 [order-service/README.md](order-service/README.md)。

已包含：

- Maven 多模块项目结构
- Spring Boot REST API
- PostgreSQL 配置
- Flyway 数据库迁移
- Spring Data JPA 实体和 Repository
- DTO、参数校验、统一异常响应
- 订单创建、订单查询、按客户查询、订单状态更新
- JUnit 5 + Mockito 单元测试
- MockMvc API 集成测试

### 本地启动

启动 PostgreSQL：

```bash
docker compose up -d postgres
```

启动服务：

```bash
mvn -pl order-service spring-boot:run
```

运行测试：

```bash
mvn test
```

### API 示例

创建订单：

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"customer-001","totalAmount":99.90}'
```

查询订单：

```bash
curl http://localhost:8080/api/orders/{orderId}
```

按客户查询订单列表：

```bash
curl "http://localhost:8080/api/orders?customerId=customer-001"
```

更新订单状态：

```bash
curl -X PATCH http://localhost:8080/api/orders/{orderId}/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"PAID"}'
```

### 本周学习重点

读代码时重点理解：

- `controller` 负责 HTTP 请求和响应
- `service` 负责业务逻辑和事务边界
- `repository` 负责数据库访问
- `domain` 负责核心业务对象和状态规则
- `dto` 负责 API 入参和出参
- `exception` 负责统一错误响应

本周先把 `POST /api/orders`、`GET /api/orders/{id}`、`PATCH /api/orders/{id}/status` 这三条链路讲清楚。

### 阿里云服务器部署

当前项目可以部署到 2 核 2GiB 云服务器。建议这台机器第 1 个月只跑：

- `order-service`
- PostgreSQL

暂时不要同时跑 Kafka、Redis、Prometheus、Grafana 等组件。

服务器需要安装：

- Git
- Docker
- Docker Compose 插件

在服务器上拉取代码后，进入项目目录：

```bash
cp .env.server.example .env.server
```

编辑 `.env.server`，把 `POSTGRES_PASSWORD` 改成强密码。

启动：

```bash
docker compose --env-file .env.server -f compose.server.yml up -d --build
```

如果不想在服务器上编译 Maven 项目，可以在本机先打 jar，再用轻量 Compose 部署。

本机打包：

```bash
mvn clean package
mkdir -p deploy
cp order-service/target/order-service-0.0.1-SNAPSHOT.jar deploy/order-service.jar
```

把这些文件传到服务器：

```bash
tar --exclude='.git' --exclude='.m2' --exclude='target' --exclude='*/target' \
  -czf /tmp/order-management-system-deploy.tar.gz \
  compose.jar.yml .env.server.example deploy order-service/Dockerfile.jar
```

服务器解压后启动：

```bash
cp .env.server.example .env.server
docker compose --env-file .env.server -f compose.jar.yml up -d --build
```

查看状态：

```bash
docker compose --env-file .env.server -f compose.server.yml ps
```

查看日志：

```bash
docker compose --env-file .env.server -f compose.server.yml logs -f order-service
```

测试健康检查：

```bash
curl http://服务器公网IP:8080/actuator/health
```

创建订单：

```bash
curl -X POST http://服务器公网IP:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"customer-001","totalAmount":99.90}'
```

安全提醒：

- 阿里云安全组只开放 `8080` 给你自己的 IP。
- 不要开放 PostgreSQL 的 `5432` 到公网。
- 当前项目还没加登录和 JWT，不建议直接暴露给所有公网 IP。

1. 最终目标

3 到 4 个月后，你应该能做到：

- 用英文或中文清楚讲解一个完整 Java 后端项目。
- 熟悉外企 Java 常见技术栈：Java 21、Spring Boot、PostgreSQL、JPA/Hibernate、Kafka、Redis、Docker、Kubernetes 基础、JUnit、Mockito、Testcontainers。
- 能回答 Java、Spring、数据库、Kafka、Redis、分布式系统、测试、部署、可观测性相关高频问题。
- 能完成中级 Java 后端岗位常见系统设计题。
- 有一份可以写进简历和 GitHub 的项目。

2. 推荐项目主线

项目名称：Order Management System

项目定位：事件驱动订单系统。

推荐架构：

Client / API Tester
        |
Spring Boot REST API
        |
order-service
  - order creation
  - order query
  - validation
  - transaction
  - outbox event
        |
        |---- PostgreSQL
        |---- Redis
        |---- Kafka
        |
payment-service
  - consume order.created
  - payment processing simulation
  - idempotency
  - publish payment.completed
        |
notification-consumer
  - consume payment.completed
  - retry
  - dead letter topic


最终技术栈：

Language: Java 21
Framework: Spring Boot
API: REST, OpenAPI optional
Security: Spring Security, JWT
Database: PostgreSQL
ORM: Spring Data JPA / Hibernate
Migration: Flyway
Cache: Redis
Messaging: Kafka
Testing: JUnit 5, Mockito, Testcontainers, WireMock optional
Build: Maven or Gradle
Infra: Docker, Docker Compose, Kubernetes basics
Observability: Spring Boot Actuator, logs, metrics, trace id, OpenTelemetry basics
CI/CD: GitHub Actions optional


3. 每天 3 小时学习节奏

推荐每天固定节奏：

第 1 小时：学习核心概念
第 2 小时：写项目代码
第 3 小时：总结、刷面试题、英文表达


每天都要产出一点东西：

- 一段可运行代码
- 一页学习笔记
- 3 到 5 个面试问答
- 一段项目讲解话术

如果当天很累，最低要求：

30 分钟看文档或课程
60 分钟写代码
30 分钟复盘


不要连续多天只看课不写代码。

4. 第 1 个月：Java、Spring Boot、PostgreSQL、JPA

本月目标：做出一个质量不错的单体后端服务。

学习重点：

- Java 17/21 核心语法和新特性
- JVM 基础：内存区域、GC、类加载、线程池、锁
- Spring IoC、AOP、Bean 生命周期
- Spring Boot 自动配置
- Spring MVC 请求处理流程
- 参数校验和全局异常处理
- Spring 事务和事务传播
- Spring Data JPA / Hibernate
- PostgreSQL 基础、索引、事务隔离级别、锁
- Flyway 数据库版本管理
- JUnit 5、Mockito 基础

项目任务：

- 创建 order-service
- 实现订单创建、订单查询、订单状态更新
- 使用 PostgreSQL 存储订单
- 使用 Flyway 管理表结构
- 使用 JPA 建模实体和 Repository
- 加入 DTO、参数校验、统一异常响应
- 编写核心 Service 单元测试
- 编写 Repository 或 API 集成测试

本月必须能回答：

- Spring Boot 自动配置是怎么工作的？
- Bean 的生命周期是什么？
- @Transactional 什么情况下会失效？
- Spring 事务传播机制有哪些？
- JPA 的懒加载是什么？
- N+1 查询问题怎么解决？
- 数据库索引为什么能加速查询？
- 事务隔离级别分别解决什么问题？
- 乐观锁和悲观锁有什么区别？

本月交付物：

- 一个可运行的 order-service
- PostgreSQL + Flyway
- 基础测试
- 一份项目 README 定版
- 30 个 Java/Spring/DB 面试问答

5. 第 2 个月：Kafka、Redis、分布式可靠性

本月目标：把单体项目升级为事件驱动系统。

学习重点：

- Kafka topic、partition、consumer group
- Kafka offset、rebalance、ack
- 消息重复、消息丢失、消息乱序
- 重试机制和死信队列
- 幂等消费
- Outbox Pattern
- Redis 缓存、过期时间、缓存穿透、缓存击穿、缓存雪崩
- Redis 分布式锁的使用边界
- 超时、重试、熔断、限流
- 分布式系统中的最终一致性

项目任务：

- 增加 Kafka
- 下单后发布 order.created 事件
- 创建 payment-service
- payment-service 消费 order.created
- 模拟支付成功或失败
- 支付完成后发布 payment.completed
- 创建 notification-consumer
- 消费支付完成事件
- 实现幂等消费
- 实现失败重试和死信 topic
- 使用 Redis 做幂等 key 或订单查询缓存

本月必须能回答：

- Kafka partition 的作用是什么？
- Consumer group 是怎么工作的？
- 为什么 Kafka 消费者需要处理重复消息？
- 如何设计幂等消费？
- 什么是 Outbox Pattern？
- 本地事务提交成功但消息发送失败怎么办？
- Redis 缓存和数据库如何保持一致？
- Redis 分布式锁有什么问题？
- 重试为什么必须配合幂等？

本月交付物：

- order-service
- payment-service
- notification-consumer
- Kafka 事件链路
- Redis 缓存或幂等实现
- 重试和死信队列
- 30 个 Kafka/Redis/分布式面试问答

6. 第 3 个月：Docker、测试、安全、可观测性、Kubernetes 基础

本月目标：让项目像一个真实工程，而不是只在本地 IDE 能跑。

学习重点：

- Dockerfile
- Docker Compose
- 多服务本地启动
- Kubernetes Deployment、Service、Ingress
- ConfigMap、Secret
- readinessProbe 和 livenessProbe
- Spring Security
- JWT 认证
- OAuth2/OIDC 基础概念
- JUnit 5、Mockito 进阶
- Testcontainers
- WireMock optional
- Spring Boot Actuator
- 日志 trace id
- Prometheus metrics
- OpenTelemetry 基础

项目任务：

- 为每个服务写 Dockerfile
- 使用 Docker Compose 启动 PostgreSQL、Redis、Kafka 和服务
- 给服务加 health check
- 加入 Spring Security + JWT
- 保护核心 API
- 使用 Testcontainers 编写 PostgreSQL/Kafka 集成测试
- 添加 Actuator
- 添加结构化日志和 trace id
- 编写 Kubernetes yaml
- 至少能在 kind 或 minikube 中部署核心服务

本月必须能回答：

- Docker 解决什么问题？
- Docker Compose 和 Kubernetes 的区别是什么？
- Kubernetes Deployment 和 Service 分别是什么？
- readinessProbe 和 livenessProbe 有什么区别？
- JWT 的结构是什么？
- 后端如何验证 JWT？
- 单元测试和集成测试有什么区别？
- Testcontainers 解决什么问题？
- 线上接口变慢怎么排查？
- metrics、logs、traces 分别解决什么问题？

本月交付物：

- Docker Compose 一键启动环境
- Kubernetes 基础部署文件
- JWT 鉴权
- Actuator health 和 metrics
- Testcontainers 集成测试
- 项目 README 完整版
- 30 个测试/安全/部署/可观测性面试问答

7. 第 4 个月：面试冲刺和系统设计

如果只有 3 个月时间，可以把这一部分压缩进第 3 个月后半段。

本月目标：把技术能力转化为面试表现。

学习重点：

- 英文简历
- LinkedIn profile
- GitHub README
- 项目讲解
- 行为面试 STAR 法则
- Java 高频题
- Spring 高频题
- 数据库高频题
- Kafka 高频题
- 系统设计
- 英文技术表达

系统设计必练题：

- Design Order System
- Design Payment System
- Design Notification System
- Design Rate Limiter
- Design URL Shortener
- Design Audit Log System
- Design Inventory System
- Design Audit Log / Activity Feed

每道系统设计题都按这个结构准备：

Requirements
API design
Data model
High-level architecture
Core workflow
Consistency strategy
Failure handling
Scalability
Observability
Security
Trade-offs


项目讲解英文模板：

I built an event-driven order management system using Java 21, Spring Boot,
PostgreSQL, Kafka, Redis, Docker, and Kubernetes.

The system supports order creation, asynchronous payment processing,
idempotent event consumption, retry and dead-letter handling, JWT-based
authentication, and basic observability with metrics and tracing.

The main technical challenge was ensuring consistency between database
transactions and Kafka events. I used the Outbox Pattern to persist domain
events in the same transaction as the order, and then published those events
asynchronously.


本月必须能回答：

- 请介绍你最近做的项目。
- 这个项目里最难的问题是什么？
- 你如何保证消息消费幂等？
- 你如何处理数据库事务和 Kafka 消息的一致性？
- 如果订单量增长 10 倍，你会怎么扩展？
- 如果支付服务挂了，系统会发生什么？
- 如果 Kafka 消费积压，你会怎么排查？
- 如果接口 P99 延迟升高，你会怎么排查？
- 你为什么选择 JPA，而不是 MyBatis？
- 你如何写测试保证核心流程可靠？

本月交付物：

- 英文简历
- GitHub 项目 README
- 项目架构图
- 项目英文讲解稿
- 100 个高频面试问答
- 8 个系统设计题笔记
- 至少 6 次模拟面试

8. 每周固定复盘模板

每周结束时写一次：

本周完成：
1.
2.
3.

本周没完成：
1.
2.

卡住的问题：
1.
2.

下周目标：
1.
2.
3.

需要 Codex 帮我细化的地方：
1.
2.


9. 学习优先级

最高优先级：

Java
Spring Boot
Spring Transaction
JPA/Hibernate
PostgreSQL
Kafka
Redis
Testing
System Design


中等优先级：

Docker
Kubernetes basics
Spring Security
JWT
Actuator
OpenTelemetry basics
Prometheus basics
GitHub Actions


低优先级，暂时不要深挖：

Dubbo
Nacos
Sentinel
RocketMQ
Seata
XXL-JOB
Spring Cloud Alibaba
Service Mesh
Terraform advanced
复杂云平台架构


10. 以后贴给 Codex 的提示词

你以后可以把这份文档中的某个月或某一节贴给 Codex，然后加上下面的提示词。

让 Codex 拆每周计划

我正在按这份《外企 Java 3 到 4 个月学习准备总纲》学习。
我每天有 3 小时。
请你基于第 X 个月的目标，帮我拆成 4 周计划。
要求：
1. 每周列出学习主题、项目任务、面试题、交付物。
2. 每天给出 3 小时安排。
3. 标出哪些必须完成，哪些可以选做。
4. 最后给我一份周末复盘清单。


让 Codex 拆每天任务

我正在准备外企 Java 面试，每天有 3 小时。
当前阶段是第 X 个月第 X 周。
本周目标是：
粘贴本周目标

请你帮我制定今天的学习任务。
要求：
1. 分成 3 个小时安排。
2. 给出具体学习内容。
3. 给出项目代码任务。
4. 给出 5 个面试问题。
5. 给出今天结束前必须产出的东西。


让 Codex 帮你做项目

我正在做外企 Java 面试项目 Order Management System。
当前技术栈是 Java 21、Spring Boot、PostgreSQL、JPA、Kafka、Redis、Docker。
我现在要实现：
描述你要做的功能

请你帮我：
1. 设计代码结构。
2. 给出实现步骤。
3. 解释关键技术点。
4. 补充必要测试。
5. 给出面试时可以怎么讲。


让 Codex 模拟面试

请你作为外企 Java 面试官，基于我的项目和当前学习阶段进行模拟面试。
要求：
1. 一次只问一个问题。
2. 我回答后你再追问。
3. 你要指出我的回答哪里好、哪里不够。
4. 最后帮我整理标准答案。
5. 面试语言可以用英文，但解释可以用中文。


让 Codex 帮你复盘

这是我本周的学习复盘：
粘贴复盘内容

请你帮我：
1. 判断我的进度是否正常。
2. 找出最应该补的短板。
3. 调整下周计划。
4. 给我一份更具体的每日任务表。


11. 判断自己是否准备好了

你可以用下面的标准判断是否可以开始投递：

- 能在 5 分钟内讲清楚项目架构。
- 能在 10 分钟内讲清楚订单创建到支付完成的完整链路。
- 能讲清楚 Kafka 重复消费、失败重试、死信队列。
- 能讲清楚事务和消息一致性的处理方式。
- 能讲清楚 JPA 的常见性能问题。
- 能讲清楚一个接口慢了怎么排查。
- 能写出基本的 JUnit 和 Testcontainers 测试。
- 能解释 Docker Compose 和 Kubernetes 的区别。
- 能用英文介绍项目背景、技术栈、难点和结果。
- 能完成至少 5 道系统设计题的完整讲解。

12. 总结路线

最短可行路线：

第 1 个月：Java + Spring Boot + PostgreSQL + JPA
第 2 个月：Kafka + Redis + 分布式可靠性
第 3 个月：Docker + Testing + Security + Observability + K8s basics
第 4 个月：系统设计 + 简历 + 模拟面试


如果压缩到 3 个月：

第 1 个月：Java + Spring Boot + PostgreSQL + JPA
第 2 个月：Kafka + Redis + 分布式可靠性
第 3 个月：Docker + Testing + Security + Observability + 系统设计冲刺


核心原则：每学一个技术点，都要放进项目里，并且整理成面试时能讲出来的故事。

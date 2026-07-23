# order-service

订单管理系统的第 1 个月定版服务，目标是把 Java、Spring Boot、PostgreSQL、JPA、Flyway、测试和部署流程串成一个可以讲清楚的最小闭环。

## 当前范围

- 创建订单
- 按 `orderId` 查询订单
- 按 `customerId` 查询订单列表
- 更新订单状态
- PostgreSQL 持久化
- Flyway 管理表结构
- 参数校验和统一异常响应
- JUnit 5 + Mockito 单元测试
- MockMvc 集成测试
- Docker 容器部署

## 技术栈

- Java 17
- Spring Boot 3.3.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Flyway
- PostgreSQL
- H2（测试）

## 目录结构

- `controller`：HTTP 接口
- `service`：业务逻辑和事务边界
- `repository`：数据库访问
- `domain`：订单实体和状态规则
- `dto`：请求和响应对象
- `exception`：统一错误响应

## 数据模型

`orders`

- `id`
- `customer_id`
- `total_amount`
- `status`
- `version`
- `created_at`
- `updated_at`

`status` 当前支持：

- `CREATED`
- `PAID`
- `CANCELLED`

当前状态规则：

- 新订单默认是 `CREATED`
- `CREATED` 可以变为 `PAID`
- `CREATED` 可以变为 `CANCELLED`
- `PAID` 不能再变为 `CANCELLED`
- `CANCELLED` 不能再变为 `PAID`

## API

### 创建订单

`POST /api/orders`

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"customer-001","totalAmount":99.90}'
```

### 查询订单

`GET /api/orders/{orderId}`

```bash
curl http://localhost:8080/api/orders/{orderId}
```

### 按客户查询订单列表

`GET /api/orders?customerId=customer-001`

```bash
curl "http://localhost:8080/api/orders?customerId=customer-001"
```

### 更新订单状态

`PATCH /api/orders/{orderId}/status`

```bash
curl -X PATCH http://localhost:8080/api/orders/{orderId}/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"PAID"}'
```

## 本地运行

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

健康检查：

```bash
curl http://localhost:8080/actuator/health
```

## Docker 部署

本地 Compose：

```bash
docker compose up -d --build
```

服务器 Compose：

```bash
cp .env.server.example .env.server
docker compose --env-file .env.server -f compose.server.yml up -d --build
```

打包 jar 后部署：

```bash
mvn clean package
mkdir -p deploy
cp order-service/target/order-service-0.0.1-SNAPSHOT.jar deploy/order-service.jar
docker compose --env-file .env.server -f compose.jar.yml up -d --build
```

## 设计要点

- `@Transactional` 放在 service 层
- JPA 实体负责最基础的状态约束
- Flyway 管理建表和索引
- 全局异常返回统一 JSON
- `@Version` 预留乐观锁能力
- 查询接口按 `created_at desc` 排序

## 已完成

- `POST /api/orders`
- `GET /api/orders/{orderId}`
- `GET /api/orders?customerId=...`
- `PATCH /api/orders/{orderId}/status`
- 单元测试
- 集成测试
- 状态流转冲突测试

## 下一步

- Kafka 事件发布
- Outbox Pattern
- Redis 幂等或缓存
- 拆分 payment-service

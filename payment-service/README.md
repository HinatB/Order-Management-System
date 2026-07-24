# payment-service

支付服务当前目标是消费 `order.created` 事件，并为订单创建一条 `PENDING` 支付记录。

## 当前范围

- 独立 Maven 模块
- Spring Boot Web 服务
- Actuator 健康检查
- Kafka consumer
- PostgreSQL 持久化
- Flyway 管理表结构
- Spring Data JPA 实体和 Repository
- 消费 `order.created` 后保存 `PENDING` payment 记录
- 默认端口 `8082`

## 数据模型

`payments`

- `id`
- `order_id`
- `customer_id`
- `amount`
- `status`
- `created_at`
- `updated_at`

`status` 当前支持：

- `PENDING`

## 本地运行

启动服务：

```bash
mvn -pl payment-service spring-boot:run
```

健康检查：

```bash
curl http://localhost:8082/actuator/health
```

验证 Kafka 消费：

1. 启动本地基础设施和 `order-service`
2. 启动 `payment-service`
3. 调用 `POST /api/orders` 创建订单
4. 查看 `payment-service` 日志中是否出现 `Created pending payment`
5. 查询 PostgreSQL 中的 `payments` 表

```bash
docker exec oms-postgres psql -U oms -d order_management \
  -c "select order_id, customer_id, amount, status from payments order by created_at desc limit 5;"
```

## 设计要点

- `payment-service` 不直接依赖 `order-service` 的 Java class
- 服务之间通过 Kafka event schema 通信
- 本地开发复用同一个 PostgreSQL 实例，但 `payment-service` 使用独立的 Flyway history table：`payment_flyway_schema_history`
- 当前还没有做幂等消费，重复的 `order.created` 可能创建重复 payment

## 下一步

- 实现基于 `orderId` 的幂等消费
- 模拟支付成功或失败

# payment-service

支付服务当前目标是作为独立 Spring Boot 服务启动，并消费 `order.created` 事件。

## 当前范围

- 独立 Maven 模块
- Spring Boot Web 服务
- Actuator 健康检查
- Kafka consumer
- 消费 `order.created` 后打印日志
- 默认端口 `8082`

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
4. 查看 `payment-service` 日志中是否出现 `Received order.created event`

## 下一步

- 增加支付数据模型和数据库表
- 收到 `order.created` 后保存 payment 记录

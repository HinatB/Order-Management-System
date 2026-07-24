# payment-service

支付服务的第 1 步骨架，当前目标是作为独立 Spring Boot 服务启动，并为后续消费 `order.created` 事件做准备。

## 当前范围

- 独立 Maven 模块
- Spring Boot Web 服务
- Actuator 健康检查
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

## 下一步

- 引入 Kafka consumer
- 消费 `order.created`
- 先打印订单创建事件日志

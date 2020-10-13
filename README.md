# GrowingIO Java SDK

GrowingIO提供在Server端部署的SDK，从而可以方便的进行事件上报等操作 <https://docs.growingio.com/op/developer-manual/sdkintegrated/cdp/java-sdk>

## Support Java Version

```java
java 6, 7, 8
```

## 依赖

我们推荐使用 Maven 管理Java 项目，请在 pom.xml 文件中，添加一下依赖信息，Maven将自动获取 Java SDK 并更新项目配置

pom.xml

```maven
<dependencies>
    <dependency>
        <groupId>io.growing.sdk.java</groupId>
        <artifactId>growingio-java-sdk</artifactId>
        <version>1.0.8-cdp</version>
    </dependency>
</dependencies>
```

若出现依赖冲突的问题（例如运行时找不到类），可以选择使用 standalone     

```maven
<dependency>
    <groupId>io.growing.sdk.java</groupId>
    <artifactId>growingio-java-sdk</artifactId>
    <version>1.0.2</version>
    <classifier>standalone</classifier>
    <exclusions>
        <exclusion>
            <groupId>org.xerial.snappy</groupId>
            <artifactId>snappy-java</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
        </exclusion>
    </exclusions>
</dependency>    
```

## 示例程序

```java
//事件行为消息体
GIOEventMessage eventMessage = new GIOEventMessage.Builder()
    .eventTime(System.currentTimeMillis())            // 默认为系统当前时间,选填
    .eventKey("3")                                    // 事件标识 (必填)
    .eventNumValue(1.0)                               // 打点事件数值 (选填)
    .loginUserId("417abcabcabcbac")                   // 登陆用户ID (选填)
    .addEventVariable("product_name", "苹果")          // 事件级变量 (选填)
    .addEventVariable("product_classify", "水果")      // 事件级变量 (选填)
    .addEventVariable("product_price", 14)            // 事件级变量 (选填)
    .addItem("item_id", "item_key")                   // 物品模型id, key (选填)
    .build();

//上传事件行为消息到服务器
GrowingAPI.send(eventMessage);
```

## 配置文件信息

gio.properties

```properties
#项目采集端地址
api.host=https://api.growingio.com
#消息发送间隔时间,单位ms（默认 100）
send.msg.interval=100
#消息发送线程数量 （默认 3）
send.msg.thread=3
#消息队列大小 （默认 500）
msg.store.queue.size=500
#数据压缩 false:不压缩,true:压缩
#不压缩可节省cpu，压缩可省带宽
compress=true
#日志级别输出 (debug | error)
logger.level=debug
#自定义日志输出实现类
logger.implementation=io.growing.sdk.java.logger.GioLoggerImpl
#运行模式，test:仅输出消息体，不发送消息，production: 发送消息
run.mode=test
# 设置代理, 如果不设置，默认为不使用代理
proxy.host=127.0.0.1
proxy.port=3128
# 设置代理认证用户密码, 如果不设置，默认为不使用用户验证 [认证加密方式为 Basic base64]
proxy.user=demo
proxy.password=demo
#http 连接超时时间,默认2秒
#connection.timeout=2000
#http 连接读取时间,默认2秒
#read.timeout=2000
```

### 事件消息

* 默认采用阻塞队列，队列大小为500.
* 如果队列满了，新的消息会被丢弃（可通过 `msg.store.queue.size` 和 `send.msg.interval` 调节队列大小和消息发送间隔时间，避免丢消息）

### sdk log 输出级别
通过以下配置可以控制 sdk 的日志输出级别
```text
# debug: 输出 debug 信息，建议连调阶段开启，可输出消息的发送报文
# error: 仅输出 错误日志，不会输出 debug 级别的信息
logger.level=debug
```

### 自定义 sdk log 输出
通过以下配置，可自定义日志输出实现类, **默认为 `io.growing.sdk.java.logger.GioLoggerImpl` 会将日志输出到 控制台**

```text
logger.implementation=io.growing.sdk.java.demo.DemoLogger
```
自定义日志输出实现类示例，DemoLogger 类需要客户自己实现，客户可根据自己的系统内部的日志工具将 sdk 的日志输出，并制定适合自己业务的日志保存策略

```java
public class DemoLogger implements GioLoggerInterface {
	private final Logger logger = LoggerFactory.getLogger(DemoLogger.class);

	public void debug(String msg) {
		logger.debug(msg);
	}

	public void error(String msg) {
		logger.error(msg);
	}
}
```
比如以上 demo 中，采用的就是 SLF4J 和 Log4j2 的组合, 客户可通过自己的日志工具定制 日志保留时间，及日志存储大小。

程序运行时可以通过 GrowingAPI.initConfig 指定配置文件 (如果不需要指定配置文件路径，则默认加载 gio.properties)

### 自定义配置文件路径
* 需要在 GrowingAPI 初始化之前调用 initConfig(String configFilePath), 进行配置初始化

#### 自定义配置

* 如果需要自定义 Properties 进行配置初始化，则需要在 GrowingAPI 初始化之前调用 initConfig(Properties properties), 进行配置初始化.
* 自定义 properties key 参考 gio_default.properties 文件
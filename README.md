# GrowingIO Java SDK

GrowingIO提供在Server端部署的SDK，从而可以方便的进行事件上报等操作

<https://docs.growingio.com/docs/>


## Support Java Version

    java 6, 7, 8

## 依赖
mvn

    <dependency>
        <groupId>io.growing.sdk.java</groupId>
        <artifactId>gio-java-sdk</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>


## 示例程序
	//事件行为消息体
    GIOEventMessage eventMessage = new GIOEventMessage.Builder()
            .eventTime(System.currentTimeMillis())            // 默认为系统当前时间,选填
            .eventKey("3")                                    // 事件标识 (必填)
            .eventNumValue(1.0)                               // 打点事件数值 (选填)
            .loginUserId("417abcabcabcbac")                   // 带用登陆用户ID的 (选填)
            .addEventVariable("product_name", "苹果")          // 事件级变量 (选填)
            .addEventVariable("product_classify", "水果")      // 事件级变量 (选填)
            .addEventVariable("product_price", 14)            // 事件级变量 (选填)
            .build();

    //上传事件行为消息到服务器
    GrowingAPI.send(eventMessage);

## 配置文件信息

gio.properties

	#项目采集端地址
	api.host=https://api.growingio.com
	#项目的AccountID
	project.id=填写您项目的AccountID
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
	logger.implemention=io.growing.sdk.java.logger.GioLoggerImpl
	#运行模式，test:仅输出消息体，不发送消息，production: 发送消息
	run.mode=test

## 事件消息

	默认采用阻塞队列，队列大小为500.
	如果队列满了，新的消息会被丢弃（可通过 [msg.store.queue.size] 和 [send.msg.interval] 调节队列大小和消息发送间隔时间，避免丢消息）




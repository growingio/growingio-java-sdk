package utils;

import com.google.gson.JsonParser;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.growing.collector.tunnel.protocol.EventV3Dto;
import io.growing.sdk.java.thread.GioThreadNamedFactory;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.net.ProxySelector;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static constant.Constants.*;

public class HttpUtils {

    private static CloseableHttpClient sHttpClient = null;
    private static BlockingQueue<InnerMessage> sQueue = null;

    static {
        // 本地抓包
//        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
//        try {
//            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
//            sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
//        } catch (Exception ignored) {
//        }

        // connectTimeout、responseTimeout 等参数 可以选择读取 gio.properties
        sHttpClient = HttpClients.custom()
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(3, TimeValue.ofMilliseconds(1000)))
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(3)
                        .setMaxConnPerRoute(3)
                        // 本地抓包
//                        .setSSLSocketFactory(sslSocketFactory)
                        .build())
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.ofMilliseconds(10000))
                        .setConnectTimeout(Timeout.ofMilliseconds(10000))
                        .setResponseTimeout(Timeout.ofMilliseconds(10000))
                        .setConnectionKeepAlive(TimeValue.ofSeconds(300))
                        .setCookieSpec(StandardCookieSpec.IGNORE)
                        .build())
                // 本地抓包
//                .setRoutePlanner(new SystemDefaultRoutePlanner(
//                        ProxySelector.getDefault()))
                .build();

        sQueue = new LinkedBlockingDeque<>(500);

        // 事件 单条发送 不适合使用ScheduledThreadPool，转为使用 FixedThreadPool
        int numConsumers = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(numConsumers, new GioThreadNamedFactory("sender"));
        for (int i = 0; i < numConsumers; ++i) {
            executorService.execute(new Consumer());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                executorService.shutdownNow();

                try {
                    // 保证consumer中 因中断未发送的事件能够被正常发送
                    executorService.awaitTermination(3000, TimeUnit.MILLISECONDS);
                    InnerMessage message = sQueue.poll();
                    while(message != null) {
                        sendMessage(message);
                        message = sQueue.poll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public static void request(Message message, Map<String, String> headers) {
        try {
            // 考虑是否使用offer 队列满时丢弃相关数据，避免阻塞
            sQueue.put(InnerMessage.Builder.newBuilder()
                    .setMessage(message)
                    .setHeaders(headers)
                    .build());
        } catch (Exception e) {
        }
    }

    private static void sendMessage(InnerMessage message) {
        try {
            URI uri = URI.create(URL + "?stm=" + System.currentTimeMillis());
            HttpPost httpPost = new HttpPost(uri);
            message.getHeaders().entrySet().stream().forEach(entry -> httpPost.addHeader(entry.getKey(), entry.getValue()));
            // PB 直接 发送
            httpPost.setEntity(new ByteArrayEntity(message.getMessage().toByteArray(), ContentType.create(HEADER_CONTENT_TYPE_PROTOBUF, StandardCharsets.UTF_8), StandardCharsets.UTF_8.toString()));

            // JSON 发送
//            Set<Descriptors.FieldDescriptor> fieldsToAlwaysOutput = new HashSet<>();
//            fieldsToAlwaysOutput.add(EventV3Dto.getDescriptor().findFieldByName("event_type"));
//            httpPost.setEntity(new ByteArrayEntity(JsonParser.parseString(JsonFormat.printer().includingDefaultValueFields(fieldsToAlwaysOutput).print(message.getMessage()))
//                    .getAsJsonObject()
//                    .get("values")
//                    .toString()
//                    .getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_JSON, StandardCharsets.UTF_8.toString()));
            try (CloseableHttpResponse response = sHttpClient.execute(httpPost)) {
                // 除 重试策略 指定条件外 是否直接丢弃
            }
        } catch (CancellationException e) {
            try {
                // 从连接池中获取连接时 当前线程中断，会导致该事件丢失
                sQueue.put(message);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        } catch (Throwable e) {
            // 其他异常，是否直接丢弃事件
        }
    }

    private static class Consumer implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    sendMessage(sQueue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static class InnerMessage {
        private Message mMessage;
        private Map<String, String> mHeaders = new HashMap<>();

        private InnerMessage() {
        }

        public Message getMessage() {
            return mMessage;
        }

        public void setMessage(Message message) {
            this.mMessage = message;
        }

        public Map<String, String> getHeaders() {
            return mHeaders;
        }

        public void setHeaders(Map<String, String> headers) {
            if (headers != null) {
                this.mHeaders.putAll(headers);
            }
        }

        public static final class Builder {
            private InnerMessage mInnerMessage;

            private Builder() {
                mInnerMessage = new InnerMessage();
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            public Builder setMessage(Message message) {
                mInnerMessage.setMessage(message);
                return this;
            }

            public Builder setHeaders(Map<String, String> headers) {
                mInnerMessage.setHeaders(headers);
                return this;
            }

            public InnerMessage build() {
                return mInnerMessage;
            }
        }
    }
}

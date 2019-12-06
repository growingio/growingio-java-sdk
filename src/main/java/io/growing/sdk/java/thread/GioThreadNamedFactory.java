package io.growing.sdk.java.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 2018-11-24 17:29
 */
public class GioThreadNamedFactory implements ThreadFactory {
    private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    private final String mPrefix;

    private final boolean mDaemon;

    private final ThreadGroup mGroup;

    public GioThreadNamedFactory() {
        this("pool-" + POOL_SEQ.getAndIncrement(), false);
    }

    public GioThreadNamedFactory(String prefix) {
        this(prefix, false);
    }

    public GioThreadNamedFactory(String prefix, boolean daemon) {
        mPrefix = prefix + "-thread-";
        mDaemon = daemon;
        SecurityManager s = System.getSecurityManager();
        mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup, runnable, name, 0);
        ret.setDaemon(mDaemon);
        return ret;
    }

    public ThreadGroup getThreadGroup() {
        return mGroup;
    }
}
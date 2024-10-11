package io.growing.sdk.java.sender.retry;

public interface Predicate<T>{

    boolean apply(T input);
}

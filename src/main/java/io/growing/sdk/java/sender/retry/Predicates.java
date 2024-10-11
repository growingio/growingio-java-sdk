package io.growing.sdk.java.sender.retry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Predicates {

    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        return new OrPredicate<T>(defensiveCopy(components));
    }

    private static <T> List<T> defensiveCopy(T... array) {
        return defensiveCopy(Arrays.asList(array));
    }

    static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<T>();
        for (T element : iterable) {
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }

    private static class OrPredicate<T> implements Predicate<T> {
        private final List<? extends Predicate<? super T>> components;

        private OrPredicate(List<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override
        public boolean apply(T t) {
            // Avoid using the Iterator to avoid generating garbage (issue 820).
            for (int i = 0; i < components.size(); i++) {
                if (components.get(i).apply(t)) {
                    return true;
                }
            }
            return false;
        }
    }
}

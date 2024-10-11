## 重试策略代码来源致谢

相关代码主要来源如下
https://github.com/google/guava
https://github.com/rholder/guava-retrying
https://github.com/google/guava/issues/490

考虑到本SDK需要支持到Java 6，尽量减少三方依赖，不使用guava中的Predicate，Predicates以及Java8相关特性
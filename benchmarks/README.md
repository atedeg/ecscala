# Benchmarks
Benchmarks are implemented using [JMH](https://openjdk.java.net/projects/code-tools/jmh/) and run with [sbt-jmh](https://github.com/sbt/sbt-jmh) plugin.  

To execute set of benchmarks from one class type following in `sbt` terminal:
```console
benchamrks / Jmh / run .*ClassName
```

Please consult [JMH](https://openjdk.java.net/projects/code-tools/jmh/) and [sbt-jmh](https://github.com/sbt/sbt-jmh) websites for more details about creating and running benchmarks.
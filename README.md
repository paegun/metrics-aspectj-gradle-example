# metrics-aspectj-gradle-example
## Purpose
This is to serve as a minimal-ish example of using metrics-aspectj, an aspect-
oriented programming (AOP) adapter for Dropwizard Metrics.

The gradle-aspectj plugin and its related example were instrumental in adapting
the maven-based instructions for metrics-aspectj.

## Quickstart

```
git clone https://github.com/paegun/metrics-aspectj-gradle-example
cd metrics-aspectj-gradle-example
gradle run
```

The build should do the following:
1. Compile the java source using the default build task w/i gradle.
2. "Aspect Weave" the aspect byte code from the jars in the aspectpath into the
    byte code of the compiled source.

NOTE: the aspect-containing jar contains not only aspects but also code that
is called from the weaved-in bits. So the jar must also be included w/i the
run-time dependencies.

## References
metrics-aspectj https://github.com/astefanutti/metrics-aspectj
Dropwizard Metrics https://github.com/dropwizard/metrics
gradle-aspectj https://github.com/eveoh/gradle-aspectj

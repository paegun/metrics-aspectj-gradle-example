// aspects to demonstrate that aspect weaving is functioning for the explicit
// case to prove out that the gradle-aspectj plugin is functioning correctly.
final aspect MetricStrategyFactoryAspect {
    pointcut callMetricRegistry() : target(com.codahale.metrics.MetricRegistry);
    before() : callMetricRegistry() {
        System.out.println("calling MetricRegistry");
    }

    pointcut callMeter() : target(com.codahale.metrics.Meter);
    before() : callMeter() {
        System.out.println("calling Meter");
    }

    pointcut callTimer() : target(com.codahale.metrics.Timer);
    before() : callTimer() {
        System.out.println("calling Timer");
    }
}

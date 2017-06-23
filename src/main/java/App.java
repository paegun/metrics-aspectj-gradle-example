import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.SharedMetricRegistries;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;

import io.astefanutti.metrics.aspectj.Metrics;

// ~minimal App using metrics-aspectj
public class App {
    public static void main(String[] args) throws InterruptedException {
        ITimedMethod timedMethod = new TimedMethod();

        for (int i = 0; i < 10; ++i) {
            timedMethod.randomTimeTask();
        }

        // await console reporter
        Thread.sleep(1010);
    }
}

// this interface is not strictly necessary, but was added to make the toggling
// of explicit profiling easier.
//
// the class TimedMethodImplicit matches the ease of implementation for metrics
// that is documented and made possible by metrics-aspectj.
interface ITimedMethod {
    public void randomTimeTask() throws InterruptedException;
}

class TimedMethod implements ITimedMethod {
    // to use the same metric registry when explicitly (read: cross-cuttingly)
    // profiling the App as well as when implicitly (read: aspect-oriented-ly)
    // profiling the App, here for the explicit case, get or create
    // (read: ~singleton) the metric registry using the same name as is used for
    // the implicit case. FYI the default name for the implicit case is
    // "metrics-registry".
    private static final MetricRegistry metrics =
        SharedMetricRegistries.getOrCreate("someMetrics");

    private ITimedMethod tmi;
    private ITimedMethod tme;

    public TimedMethod() {
        metricConsoleReporterStart();
        this.tmi = new TimedMethodImplicit();
        if (!AppConfig.isExplicitlyMeasuring) {
            this.tme = new TimedMethodNop();
        } else {
            this.tme = new TimedMethodExplicit(this.metrics);
        }
    }

    public void randomTimeTask() throws InterruptedException {
        tmi.randomTimeTask();
        tme.randomTimeTask();
    }

    private void metricConsoleReporterStart() {
        ConsoleReporter.forRegistry(this.metrics)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
            .start(1, TimeUnit.SECONDS);
    }
}

@Metrics(registry = "someMetrics")
class TimedMethodImplicit implements ITimedMethod {
    private final Random rand = new Random();

    @Timed(name = "randomTimeTask")
    public void randomTimeTask() throws InterruptedException {
        Thread.sleep(rand.nextInt(100));
    }
}

class TimedMethodNop implements ITimedMethod {
    public void randomTimeTask() throws InterruptedException { }
}

class TimedMethodExplicit implements ITimedMethod {
    private final MetricRegistry metrics;
    private final Timer timer;

    private final Random rand = new Random();

    public TimedMethodExplicit(MetricRegistry metrics) {
        this.metrics = metrics;
        this.timer = metrics.timer("request_times");
    }

    @Timed(name = "randomTimeTask")
    public void randomTimeTask() throws InterruptedException {
        Timer.Context context = null;
        try {
            context = this.timer.time();
            Thread.sleep(rand.nextInt(100));
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }
}

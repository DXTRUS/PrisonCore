package us.dxtrus.prisoncore.jobs;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import us.dxtrus.prisoncore.PrisonCore;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represents a job that gets run on a thread.
 */
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class Job {
    private static final ScheduledExecutorService scheduler =
            new ScheduledThreadPoolExecutor(3);

    private final String name;
    private final Duration interval;
    private boolean silent = false;

    public final void start() {
        scheduler.scheduleAtFixedRate(this::run,
                interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
        PrisonCore.getInstance().getLogger().info("[JOBS] Job '%s' scheduled at an interval of %s seconds"
                .formatted(this.name, interval.get(ChronoUnit.SECONDS)));
    }

    void run() {
        int errors = 0;
        log("[JOBS] Running job '%s'".formatted(name));
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();
            errors++;
        }
        log("[JOBS] Job '%s' completed (%s errors)".formatted(this.name, errors));
    }

    public void shutdown() {
        scheduler.shutdownNow();
        log("[JOBS] Job '%s' shutdown!".formatted(this.name));
    }

    protected abstract void execute();

    private void log(String message) {
        if (!silent) {
            PrisonCore.getInstance().getLogger().info(message);
        }
    }
}

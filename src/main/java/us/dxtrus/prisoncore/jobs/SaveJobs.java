package us.dxtrus.prisoncore.jobs;

import us.dxtrus.prisoncore.mine.MineManager;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.storage.StorageManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SaveJobs {
    private static final List<Job> runningJobs = new ArrayList<>();

    public static void startAll() {
        Stream.of(
                new MineSaveJob()
        ).forEach(job -> {
            job.start();
            runningJobs.add(job);
        });
    }

    public static void forceRunAll() {
        runningJobs.forEach(Job::run);
    }

    public static void shutdownAll() {
        runningJobs.forEach(Job::shutdown);
    }

    public static class MineSaveJob extends Job {
        public MineSaveJob() {
            super("Mine Save", Duration.ofMinutes(30));
        }

        @Override
        protected void execute() {
            MineManager.getInstance().getAllMines().forEach(mine -> {
                StorageManager.getInstance().save(PrivateMine.class, mine);
            });
        }
    }
}
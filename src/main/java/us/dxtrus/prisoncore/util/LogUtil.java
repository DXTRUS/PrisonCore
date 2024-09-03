package us.dxtrus.prisoncore.util;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    private static final Logger logger = Logger.getLogger("PrisonCore");

    private LogUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void info(@NotNull String message) {
        logger.log(Level.INFO, message);
    }

    public static void warn(@NotNull String message) {
        logger.log(Level.WARNING, message);
    }

    public static void warn(@NotNull String message, @NotNull Throwable e) {
        logger.log(Level.WARNING, message, e);
    }

    public static void severe(@NotNull String message) {
        logger.severe(message);
    }

    public static void severe(@NotNull String message, @NotNull Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }

    public static void debug(@NotNull String message) {
        logger.log(Level.FINE, message);
    }
}

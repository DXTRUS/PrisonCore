package us.dxtrus.prisoncore.mines.loader;

import com.infernalsuite.aswm.api.loaders.SlimeLoader;
import lombok.Getter;

import java.io.IOException;

public abstract class UpdatableLoader implements SlimeLoader {
    public abstract void update() throws NewerDatabaseException, IOException;

    @Getter
    public static class NewerDatabaseException extends Exception {

        private final int currentVersion;
        private final int databaseVersion;


        public NewerDatabaseException(int currentVersion, int databaseVersion) {
            this.currentVersion = currentVersion;
            this.databaseVersion = databaseVersion;
        }
    }
}
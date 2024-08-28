package us.dxtrus.prisoncore.storage.doas;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import us.dxtrus.commons.database.dao.Dao;
import us.dxtrus.commons.shaded.hikari.HikariDataSource;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.stats.Statistics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class DaoStatictics implements Dao<Statistics> {
    private final HikariDataSource dataSource;

    @Override
    public Optional<Statistics> get(UUID uuid) {
        String sql = "SELECT * FROM statistics WHERE uuid = ? LIMIT 1;";
        Statistics statistics = null;

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();

            if (!results.next()) {
                statistics = new Statistics(uuid);
                this.save(statistics);

                return Optional.of(statistics);
            }

            statistics = new Statistics(uuid,
                    new BigDecimal(results.getString("tokens")),
                    new BigDecimal(results.getString("gems")),
                    new BigDecimal(results.getString("blocks_broken"))
            );

            return Optional.of(statistics);
        } catch (SQLException ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to query statistics for " + uuid, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Statistics> getAll() {
        throw new NotImplementedException();
    }

    @Override
    public void save(Statistics statistics) {
        String sql = "INSERT INTO statistics (uuid, tokens, gems, blocks_broken) VALUES (?, ?, ?, ?);";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statistics.getUuid().toString());
            statement.setString(2, statistics.getTokens().toString());
            statement.setString(3, statistics.getGems().toString());
            statement.setString(4, statistics.getBlocksBroken().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to create statistics for " + statistics.getUuid(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Statistics statistics, String[] strings) {
        String sql = "UPDATE statistics SET tokens = ?, gems = ?, blocks_broken = ? WHERE uuid = " + statistics.getUuid().toString() + ";";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, statistics.getTokens().toString());
            statement.setString(2, statistics.getGems().toString());
            statement.setString(3, statistics.getBlocksBroken().toString());

            statement.executeUpdate();
        } catch (SQLException ex) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to update statistics for " + statistics.getUuid(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Statistics statistics) {
        throw new NotImplementedException();
    }
}

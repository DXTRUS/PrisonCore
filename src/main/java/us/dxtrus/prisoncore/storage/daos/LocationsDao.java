package us.dxtrus.prisoncore.storage.daos;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import us.dxtrus.commons.database.dao.Dao;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.locations.Loc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class LocationsDao implements Dao<Loc> {
    private final HikariDataSource dataSource;
    private final Gson gson = new Gson();

    @Override
    public Optional<Loc> get(UUID uuid) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<Loc> get(String enumName) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT `loc`
                    FROM `locations`
                    WHERE `name`=?;""")) {
                statement.setString(1, enumName);
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final Loc loc = gson.fromJson(resultSet.getString("loc"), Loc.class);
                    return Optional.of(loc);
                }
            }
        } catch (SQLException e) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to get location!", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Loc> getAll() {
        throw new NotImplementedException();
    }

    @Override
    public void save(Loc loc) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `locations` 
                    (`name`, `loc`)
                    VALUES (?, ?)
                    ON DUPLICATE KEY UPDATE `loc` = VALUES(`loc`);""")) {
                statement.setString(1, loc.getLocation().name());
                statement.setString(2, gson.toJson(loc));
                statement.execute();
            }
        } catch (SQLException e) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to save location!", e);
        }
    }

    @Override
    public void update(Loc loc, String[] strings) {

    }

    @Override
    public void delete(Loc loc) {

    }
}

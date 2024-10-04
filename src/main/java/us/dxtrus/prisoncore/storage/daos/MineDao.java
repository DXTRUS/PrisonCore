package us.dxtrus.prisoncore.storage.daos;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import us.dxtrus.commons.database.dao.Dao;
import us.dxtrus.commons.shaded.hikari.HikariDataSource;
import us.dxtrus.prisoncore.PrisonCore;
import us.dxtrus.prisoncore.mine.models.LocRef;
import us.dxtrus.prisoncore.mine.models.PrivateMine;
import us.dxtrus.prisoncore.mine.models.Server;
import us.dxtrus.prisoncore.mine.network.ServerManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

@RequiredArgsConstructor
public class MineDao implements Dao<PrivateMine> {
    private final Gson gson = new Gson();
    private final HikariDataSource dataSource;

    @Override
    public Optional<PrivateMine> get(UUID uuid) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT `owner`, `spawnLoc`, `center`, `loaded`, `server`, `npcLoc`, `level`
                    FROM `mines`
                    WHERE `owner`=?;""")) {
                statement.setString(1, uuid.toString());
                final ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    final UUID ownerUUID = uuid;
                    final LocRef spawnLoc = gson.fromJson(resultSet.getString("spawnLoc"), LocRef.class);
                    final LocRef center = gson.fromJson(resultSet.getString("center"), LocRef.class);
                    final LocRef npcLoc = gson.fromJson(resultSet.getString("npcLoc"), LocRef.class);
                    final boolean loaded = resultSet.getBoolean("loaded");
                    final int level = resultSet.getInt("level");
                    String serverName = resultSet.getString("server");
                    final Server server = serverName.equals("None") ? null : ServerManager.getInstance().getServer(serverName);
                    return Optional.of(new PrivateMine(ownerUUID, spawnLoc, center, npcLoc, server, level, loaded));
                }
            }
        } catch (SQLException e) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to get mine!", e);
        }
        return Optional.empty();
    }

    @Override
    public List<PrivateMine> getAll() {
        return List.of();
    }

    @Override
    public void save(PrivateMine privateMine) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `mines` 
                    (`owner`, `spawnLoc`, `center`, `loaded`, `server`, `npcLoc`, `level`)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        `spawnLoc` = VALUES(`spawnLoc`),
                        `loaded` = VALUES(`loaded`),
                        `server` = VALUES(`server`),
                        `npcLoc` = VALUES(`npcLoc`),
                        `level` = VALUES(`level`),
                        `center` = VALUES(`center`);""")) {
                statement.setString(1, privateMine.getOwner().toString());
                statement.setString(2, gson.toJson(privateMine.getSpawnLocation()));
                statement.setString(3, gson.toJson(privateMine.getCenter()));
                statement.setBoolean(4, privateMine.isLoaded());
                statement.setString(5, privateMine.getServer() == null ? "None" : privateMine.getServer().getName());
                statement.setString(6, gson.toJson(privateMine.getNpcLocation()));
                statement.setInt(7, privateMine.getLevel());
                statement.execute();
            }
        } catch (SQLException e) {
            PrisonCore.getInstance().getLogger().log(Level.SEVERE, "Failed to save mine!", e);
        }
    }

    @Override
    public void update(PrivateMine privateMine, String[] strings) {

    }

    @Override
    public void delete(PrivateMine privateMine) {

    }
}

package fr.rudy.multiworld;

import fr.rudy.databaseapi.DatabaseAPI;
import fr.rudy.multiworld.commands.SetSpawnCommand;
import fr.rudy.multiworld.commands.SpawnTeleportCommand;
import fr.rudy.multiworld.commands.WorldCommand;
import fr.rudy.multiworld.listener.JoinSpawnListener;
import fr.rudy.multiworld.manager.CoreSpawnManager;
import fr.rudy.multiworld.manager.WorldSpawnManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MultiWorldPlugin extends JavaPlugin {

    private static MultiWorldPlugin instance;
    private CoreSpawnManager coreSpawnManager;
    private WorldSpawnManager worldSpawnManager;
    private Connection database;

    public static MultiWorldPlugin get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("DatabaseAPI");
        if (plugin instanceof DatabaseAPI dbAPI && plugin.isEnabled()) {
            database = dbAPI.getDatabaseManager().getConnection();

            // Créer les tables si elles n'existent pas
            try (Statement stmt = database.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS main_spawn (" +
                        "id INTEGER PRIMARY KEY CHECK (id = 0), " +
                        "world TEXT NOT NULL, x DOUBLE NOT NULL, y DOUBLE NOT NULL, z DOUBLE NOT NULL, " +
                        "yaw FLOAT NOT NULL, pitch FLOAT NOT NULL)");

                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS world_spawns (" +
                        "world_name VARCHAR(64) PRIMARY KEY, " +
                        "x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
            } catch (SQLException e) {
                e.printStackTrace();
                getLogger().severe("❌ Erreur lors de la création des tables MySQL.");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            coreSpawnManager = new CoreSpawnManager(database);
            worldSpawnManager = new WorldSpawnManager(database);
        } else {
            getLogger().severe("❌ DatabaseAPI introuvable ou désactivé !");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Enregistrement des commandes
        getCommand("spawn").setExecutor(new SpawnTeleportCommand());
        getCommand("world").setExecutor(new WorldCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand());

        // Listener de téléportation au spawn
        Bukkit.getPluginManager().registerEvents(new JoinSpawnListener(coreSpawnManager), this);

        getLogger().info("✅ MultiWorld activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("⛔ MultiWorld désactivé.");
        try {
            if (database != null && !database.isClosed()) {
                database.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CoreSpawnManager getCoreSpawnManager() {
        return coreSpawnManager;
    }

    public WorldSpawnManager getWorldSpawnManager() {
        return worldSpawnManager;
    }
}

package fr.rudy.multiworld.commands;

import fr.rudy.multiworld.MultiWorldPlugin;
import fr.rudy.multiworld.manager.WorldSpawnManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldCommand implements CommandExecutor {

    private final String permission = "admin";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage("§cVous n'avez pas la permission !");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUtilisation : /world <create|remove|tp|setspawn> <nom> [joueur]");
            return true;
        }

        String action = args[0].toLowerCase();
        String worldName = args[1];

        switch (action) {
            case "create":
                return createWorld(sender, worldName);
            case "remove":
                return removeWorld(sender, worldName);
            case "tp":
                String targetName = args.length >= 3 ? args[2] : sender.getName();
                return teleportToWorld(sender, worldName, targetName);
            case "setspawn":
                return setWorldSpawn(sender, worldName);
            default:
                sender.sendMessage("§cCommande inconnue. Utilisez : create, remove, tp, setspawn");
                return true;
        }
    }

    private boolean createWorld(CommandSender sender, String worldName) {
        if (Bukkit.getWorld(worldName) != null) {
            sender.sendMessage("§cLe monde §b" + worldName + "§c existe déjà !");
            return true;
        }

        new WorldCreator(worldName).createWorld();
        sender.sendMessage("§aMonde §b" + worldName + "§a créé avec succès !");
        return true;
    }

    private boolean removeWorld(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        if (!worldFolder.exists()) {
            sender.sendMessage("§cLe dossier du monde §b" + worldName + "§c n'existe pas.");
            return true;
        }

        if (deleteFolder(worldFolder)) {
            sender.sendMessage("§aLe monde §b" + worldName + "§a a été supprimé !");
        } else {
            sender.sendMessage("§cErreur lors de la suppression du monde §b" + worldName);
        }
        return true;
    }

    private boolean teleportToWorld(CommandSender sender, String worldName, String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable : §b" + targetName);
            return true;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
            if (!worldFolder.exists() || !worldFolder.isDirectory()) {
                sender.sendMessage("§cLe monde §b" + worldName + "§c n'existe pas dans les fichiers !");
                return true;
            }

            sender.sendMessage("§eChargement du monde §b" + worldName + "§e...");
            world = new WorldCreator(worldName).createWorld();
            if (world == null) {
                sender.sendMessage("§cErreur lors du chargement du monde §b" + worldName + "§c.");
                return true;
            }

            sender.sendMessage("§aMonde §b" + worldName + "§a chargé avec succès !");
        }

        WorldSpawnManager spawnManager = MultiWorldPlugin.get().getWorldSpawnManager();
        Location spawn = spawnManager.getSpawn(worldName);

        if (spawn == null) {
            spawn = world.getSpawnLocation();
            spawn.setYaw(0f);
            spawn.setPitch(0f);
            sender.sendMessage("§eAucun spawn personnalisé trouvé. Téléportation au spawn par défaut.");
        } else {
            sender.sendMessage("§aTéléportation vers le spawn précis de §b" + worldName + "§a.");
        }

        target.teleport(spawn);
        if (!sender.getName().equalsIgnoreCase(target.getName())) {
            target.sendMessage("§aVous avez été téléporté dans §b" + worldName + " §apar un administrateur.");
        }
        return true;
    }

    private boolean setWorldSpawn(CommandSender sender, String worldName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent exécuter cette commande.");
            return true;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("§cLe monde §b" + worldName + "§c n'est pas chargé !");
            return true;
        }

        Player player = (Player) sender;
        if (!player.getWorld().equals(world)) {
            sender.sendMessage("§cVous devez être dans le monde §b" + worldName + "§c pour définir son spawn.");
            return true;
        }

        Location loc = player.getLocation();
        MultiWorldPlugin.get().getWorldSpawnManager().setSpawn(worldName, loc);
        sender.sendMessage("§aLe spawn précis du monde §b" + worldName + "§a a été défini !");
        return true;
    }

    private boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        return folder.delete();
    }
}

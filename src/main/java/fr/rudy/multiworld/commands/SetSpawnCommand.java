package fr.rudy.multiworld.commands;

import fr.rudy.multiworld.MultiWorldPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande !");
            return true;
        }

        MultiWorldPlugin.get().getCoreSpawnManager().setSpawn(player.getLocation());
        player.sendMessage("§aSpawn global défini avec succès !");
        return true;
    }
}

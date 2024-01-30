package org.yuezhikong.plugins.command;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.yuezhikong.plugins.SecuritiesMarket;

public class CommandSm implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Economy economy = SecuritiesMarket.getEconomy();

        if (args.length == 0) { // Ensure that there was specified a player
            sender.sendMessage("You must specify the name of the player whose balance you would like to reset");
            return true;
        }
        String playerName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null) { // Check if player has joined the server
            sender.sendMessage("A player with the name '" + playerName + "' has never joined this server");
            return true;
        }

        // Reset the player's balance to 0
        economy.withdrawPlayer(target, economy.getBalance(target));
        return true;
    }
}

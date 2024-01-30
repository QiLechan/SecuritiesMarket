package org.yuezhikong.plugins.command;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.yuezhikong.plugins.net.Http.getTicker;


public class CommandSm implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("指令格式错误");
            return true;
        }
        switch (args[0]){
            case "inquire":
                if (args.length > 2){
                    sender.sendMessage("指令格式错误");
                }
                else {
                    String Ticker = getTicker(args[1]);
                    sender.sendMessage(Ticker);
                }
        }
        return true;
    }
}

package org.yuezhikong.plugins.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class sm implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            ItemStack diamond = new ItemStack(Material.DIAMOND);
            ItemStack bricks = new ItemStack(Material.BRICK);
            bricks.setAmount(20);
            player.getInventory().addItem(bricks, diamond);
        }
        return true;
    }
}

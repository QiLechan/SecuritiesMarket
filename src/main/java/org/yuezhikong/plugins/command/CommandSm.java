package org.yuezhikong.plugins.command;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.yuezhikong.plugins.SecuritiesMarket;

import java.util.HashMap;

import static org.yuezhikong.plugins.net.Http.getTicker;


public class CommandSm implements CommandExecutor {
    private HashMap<Player, Double> price = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("指令格式错误");
            return true;
        }
        switch (args[0]){
            case "inquire": {
                if (args.length > 2) {
                    sender.sendMessage("指令格式错误");
                } else {
                    String Ticker = getTicker(args[1]);
                    String[] parts = Ticker.split("=")[1].split("~");
                    sender.sendMessage(parts[1] + " 股票代码：" + parts[2] + " 当前价格：" + parts[3] + " 涨跌：" + parts[4] + " 涨跌幅：" + parts[5] + "%");
                }
                break;
            }
            case "buy": {
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length != 3) {
                    sender.sendMessage("指令格式错误");
                } else {
                    String Ticker = getTicker(args[1]);
                    String[] parts = Ticker.split("=")[1].split("~");
                    String amount = args[2];
                    price.put(player, Double.parseDouble(parts[3]) * Double.parseDouble(amount));
                    sender.sendMessage("您将要购买" + parts[1] + "股票，当前市场价格为：" + parts[3] + "，购买数量为：" + amount + "，花费金额为：" + price.get(player) + "元");
                    sender.sendMessage("确认购买输入/sm yes，取消购买输入/sm no");
                }
                break;
                }
            case "yes":{
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                else {
                    Player player = (Player) sender;
                    if (price.get(player) == null){
                        sender.sendMessage("请先输入/sm buy");
                        break;
                    }
                    if (player.hasPermission("sm.buy")){
                        EconomyResponse response = SecuritiesMarket.getEconomy().withdrawPlayer(player, price.get(player));
                        if (response.type != EconomyResponse.ResponseType.SUCCESS) {
                            sender.sendMessage("购买失败");
                            break;
                        } else {sender.sendMessage("购买成功");
                        }
                    } else {
                        sender.sendMessage("您没有权限购买");
                        break;
                    }
                }
                break;
            }
            case "no":{
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                Player player = (Player) sender;
                price.remove(player);
                break;
            }
        }
        return true;
    }
}

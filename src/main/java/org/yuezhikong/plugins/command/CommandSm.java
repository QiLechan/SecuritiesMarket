package org.yuezhikong.plugins.command;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuezhikong.plugins.SecuritiesMarket;

import java.util.HashMap;

import static org.yuezhikong.plugins.net.Request.getTicker;
import static org.yuezhikong.plugins.sqlite.SqliteManager.Buy;
import static org.yuezhikong.plugins.sqlite.SqliteManager.Sell;


public class CommandSm implements CommandExecutor {
    private HashMap<Player, Double> price = new HashMap<>();
    private HashMap<Player, String> ticker = new HashMap<>();
    private HashMap<Player, Integer> amount = new HashMap<>();
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
                }
                else {
                    String Ticker = getTicker(args[1]);
                    if (Ticker.equals("none_match")) {
                        sender.sendMessage("股票代码错误");
                        break;
                    }
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
                    if (Ticker.equals("none_match")) {
                        sender.sendMessage("股票代码错误");
                        break;
                    }
                    String[] parts = Ticker.split("=")[1].split("~");
                    try {
                        if (args[2].contains(".")){
                            sender.sendMessage("购买数量必须是一个整数");
                            break;
                        }
                        int amount = Integer.parseInt(args[2]);
                        if (amount <= 0){
                            sender.sendMessage("购买数量必须大于0");
                            break;
                        }
                    } catch (NumberFormatException e){
                        sender.sendMessage("购买数量必须是一个大于0的整数");
                        break;
                    }
                    amount.put(player, Integer.parseInt(args[2]));
                    price.put(player, Double.parseDouble(parts[3]) * Double.parseDouble(String.valueOf(amount.get(player))));
                    ticker.put(player, parts[2]);
                    sender.sendMessage("您将要购买" + parts[1] + "股票，当前市场价格为：" + parts[3] + "，购买数量为：" + amount.get(player) + "手，花费金额为：" + price.get(player)*100 + "元");
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
                        } else {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Buy(player.getName(), player.getUniqueId().toString(), ticker.get(player), amount.get(player)*100);
                                    price.remove(player);
                                    ticker.remove(player);
                                    amount.remove(player);
                                    sender.sendMessage("购买成功");
                                }
                            }.runTaskAsynchronously(SecuritiesMarket.getPlugin(SecuritiesMarket.class));
                        }
                    } else {
                        sender.sendMessage("您没有权限购买");
                        price.remove(player);
                        ticker.remove(player);
                        amount.remove(player);
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
                amount.remove(player);
                ticker.remove(player);
                break;
            }
            case "sell": {
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length != 3) {
                    sender.sendMessage("指令格式错误");
                } else {
                    String Ticker = getTicker(args[1]);
                    if (Ticker.equals("none_match")) {
                        sender.sendMessage("股票代码错误");
                        break;
                    }
                    String[] parts = Ticker.split("=")[1].split("~");
                    try {
                        if (args[2].contains(".")){
                            sender.sendMessage("卖出数量必须是一个整数");
                            break;
                        }
                        int amount = Integer.parseInt(args[2]);
                        if (amount <= 0){
                            sender.sendMessage("卖出数量必须大于0");
                            break;
                        }
                    } catch (NumberFormatException e){
                        sender.sendMessage("卖出数量必须是一个大于0的整数");
                        break;
                    }
                    amount.put(player, Integer.parseInt(args[2]));
                    price.put(player, Double.parseDouble(parts[3]) * Double.parseDouble(String.valueOf(amount.get(player))));
                    ticker.put(player, parts[2]);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int response = Sell(player.getUniqueId().toString(), ticker.get(player), amount.get(player));
                            if (response == 0) {
                                sender.sendMessage("卖出失败，您没有这支股票");
                            } else if (response == 1){
                                sender.sendMessage("卖出失败，您账户内余额小于您要卖出的数量");
                            }
                            else if (response == 2){
                                EconomyResponse economyResponse = SecuritiesMarket.getEconomy().depositPlayer(player, price.get(player));
                                if (economyResponse.type != EconomyResponse.ResponseType.SUCCESS) {
                                    sender.sendMessage("卖出失败");
                                }
                                else {
                                    sender.sendMessage("您已卖出" +amount.get(player)+"股"+ parts[1] + ",获得" + price.get(player) + "元");
                                }
                            }
                            price.remove(player);
                            ticker.remove(player);
                            amount.remove(player);
                        }
                    }.runTaskAsynchronously(SecuritiesMarket.getPlugin(SecuritiesMarket.class));
                }
            }
            default:{
                sender.sendMessage("指令格式错误");
            }
        }
        return true;
    }
}

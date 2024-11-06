package org.yuezhikong.plugins.command;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuezhikong.plugins.SecuritiesMarket;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.bukkit.Bukkit.getPlayerExact;
import static org.yuezhikong.plugins.net.Request.getTicker;
import static org.yuezhikong.plugins.sqlite.SqliteManager.*;
import static org.yuezhikong.plugins.util.TimeCheck.isWithinWorkingHours;


public class CommandSm implements CommandExecutor {
    private HashMap<Player, Double> price = new HashMap<>();
    private HashMap<Player, String> ticker = new HashMap<>();
    private HashMap<Player, Integer> amount = new HashMap<>();
    private FileConfiguration config = SecuritiesMarket.getServerconfig();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("指令格式错误");
            return false;
        }
        switch (args[0]){
            case "inquire": {
                if (args.length > 2) {
                    sender.sendMessage("指令格式错误");
                    return false;
                }
                String Ticker = getTicker(args[1]);
                if (Ticker.equals("none_match")) {
                    sender.sendMessage("股票代码错误");
                    break;
                }
                String[] parts = Ticker.split("=")[1].split("~");
                sender.sendMessage(parts[1] + " 股票代码：" + parts[2] + " 当前价格：" + parts[3] + " 涨跌：" + parts[4] + " 涨跌幅：" + parts[5] + "%");
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
                    return false;
                }
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
                if (config.getBoolean("ClosedMarkets")) {
                    if (!isWithinWorkingHours()){
                        sender.sendMessage("当前不在交易时间。交易时间为周一至周五的上午9:30至11:30和下午1:00至3:00之间。");
                        return true;
                    }
                }
                amount.put(player, Integer.parseInt(args[2]));
                price.put(player, Double.parseDouble(parts[3]) * Double.parseDouble(String.valueOf(amount.get(player))));
                ticker.put(player, args[1]);
                sender.sendMessage("您将要购买" + parts[1] + "股票，当前市场价格为：" + parts[3] + "，购买数量为：" + amount.get(player) + "手，花费金额为：" + price.get(player)*100 + "元");
                sender.sendMessage("确认购买输入/sm yes，取消购买输入/sm no");
                break;
            }
            case "yes":{
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                if (args.length != 1) {
                    sender.sendMessage("指令格式错误");
                    return false;
                }
                Player player = (Player) sender;
                if (price.get(player) == null){
                    sender.sendMessage("请先输入/sm buy");
                    break;
                }
                if (!player.hasPermission("sm.buy")){
                    sender.sendMessage("您没有权限购买");
                    price.remove(player);
                    ticker.remove(player);
                    amount.remove(player);
                    break;
                }
                if (SecuritiesMarket.getEconomy().getBalance(player) < price.get(player)*100){
                    player.sendMessage("购买失败，余额不足");
                    break;
                }
                EconomyResponse response = SecuritiesMarket.getEconomy().withdrawPlayer(player, price.get(player)*100);
                if (response.type != EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage("购买失败");
                    break;
                }
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
                    return false;
                }
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
                if (config.getBoolean("ClosedMarkets")) {
                    if (!isWithinWorkingHours()){
                        sender.sendMessage("当前不在交易时间。交易时间为周一至周五的上午9:30至11:30和下午1:00至3:00之间。");
                        return true;
                    }
                }
                amount.put(player, Integer.parseInt(args[2]));
                price.put(player, Double.parseDouble(parts[3]) * Double.parseDouble(String.valueOf(amount.get(player))));
                ticker.put(player, args[1]);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int response = Sell(player.getUniqueId().toString(), ticker.get(player), amount.get(player));
                        if (response == 0) {
                            sender.sendMessage("卖出失败，您没有这支股票");
                        }
                        else if (response == 1){
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
                break;
            }
            case "check": {
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length != 1) {
                    sender.sendMessage("指令格式错误");
                    return false;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        String UUID = player.getUniqueId().toString();
                        HashMap<String,Integer> response = Check(UUID);
                        if (response == null){
                            sender.sendMessage("您没有购买任何股票");
                        }
                        else {
                            sender.sendMessage("您已购买以下股票");
                            for (String key : response.keySet()) {
                                String Ticker = getTicker(key);
                                String[] price = Ticker.split("=")[1].split("~");
                                sender.sendMessage("股票代码：" + key + " 数量：" + response.get(key) + "股 当前价格：" + price[3] + "元");
                            }
                        }
                    }
                }.runTaskAsynchronously(SecuritiesMarket.getPlugin(SecuritiesMarket.class));
                break;
            }
            case "transfer":{
                if (!(sender instanceof Player)){
                    sender.sendMessage("请在游戏内输入指令");
                    return true;
                }
                Player player = (Player) sender;
                Player target = getPlayerExact(args[2]);
                if (args.length != 4) {
                    sender.sendMessage("指令格式错误");
                    return false;
                }
                if (args[3].contains(".")){
                    sender.sendMessage("转移数量必须是一个整数");
                    break;
                }
                if (Integer.parseInt(args[3])<=0){
                    sender.sendMessage("转移数量必须大于0");
                    break;
                }
                if (target == null){
                    sender.sendMessage("玩家不在线");
                    return true;
                }
                String Ticker = getTicker(args[1]);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int response = Sell(player.getUniqueId().toString(), args[1], Integer.parseInt(args[3]));
                        if (response == 0) {
                            sender.sendMessage("转移失败，您没有这支股票");
                        }
                        else if (response == 1){
                            sender.sendMessage("转移失败，您账户内余额小于您要卖出的数量");
                        }
                        else if (response == 2){
                            Buy(target.getName(), target.getUniqueId().toString(), args[1], Integer.parseInt(args[3]));
                            sender.sendMessage("转移成功");
                        }
                    }
                }.runTaskAsynchronously(SecuritiesMarket.getPlugin(SecuritiesMarket.class));
            }
            case "help" :{
                sender.sendMessage("/sm inquire [股票代码]：查询股票代码");
                sender.sendMessage("/sm buy [股票代码] [购买数量]：购买股票");
                sender.sendMessage("/sm yes：确认交易");
                sender.sendMessage("/sm no：取消交易");
                sender.sendMessage("/sm sell [持有股票] [售出数量]：出售股票");
                sender.sendMessage("/sm check：查询持有股票");
                sender.sendMessage("/sm transfer [股票代码] [收款人] [数量]：转移股票");
            }
            default:{
                sender.sendMessage("指令格式错误");
            }
        }
        return true;
    }
}

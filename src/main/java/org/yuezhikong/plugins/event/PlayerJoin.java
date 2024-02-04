package org.yuezhikong.plugins.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.yuezhikong.plugins.SecuritiesMarket;

import java.util.HashMap;

import static org.yuezhikong.plugins.net.Request.getTicker;
import static org.yuezhikong.plugins.sqlite.SqliteManager.Check;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            public void run() {
                String UUID = String.valueOf(event.getPlayer().getUniqueId());
                HashMap<String,Integer> response = Check(UUID);
                if (response != null){
                    event.getPlayer().sendMessage("以下是您购买的股票当前市场价格");
                    for (String key : response.keySet()) {
                        String Ticker = getTicker(key);
                        String[] price = Ticker.split("=")[1].split("~");
                        event.getPlayer().sendMessage("股票代码：" + key + " 数量：" + response.get(key) + "股 当前价格：" + price[3] + "元");
                    }
                }
            }
        }.runTaskAsynchronously(SecuritiesMarket.getPlugin(SecuritiesMarket.class));
    }
}

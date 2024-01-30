package org.yuezhikong.SecuritiesMarket;

import org.bukkit.plugin.java.JavaPlugin;

public class SecuritiesMarket extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("onEnable is called!");
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
}
package org.yuezhikong.plugins;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuezhikong.plugins.command.CommandSm;

public class SecuritiesMarket extends JavaPlugin {
    private static Economy econ = null;
    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else {
            getLogger().severe("[证券市场]加载成功");
        }
        this.getCommand("sm").setExecutor(new CommandSm());
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public static Economy getEconomy() {
        return econ;
    }
}
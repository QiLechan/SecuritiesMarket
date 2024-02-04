package org.yuezhikong.plugins;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuezhikong.plugins.command.CommandSm;
import org.yuezhikong.plugins.event.PlayerJoin;

import java.io.File;

import static org.yuezhikong.plugins.sqlite.SqliteManager.connect;
import static org.yuezhikong.plugins.sqlite.SqliteManager.createNewTable;

public class SecuritiesMarket extends JavaPlugin {
    private static Economy econ = null;
    private static File DataFolder = null;
    private static FileConfiguration config = null;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        DataFolder = this.getDataFolder();
        config = getConfig();
        if (config.getInt("Database") == 1){
            String Folder = DataFolder.getPath();
            File db = new File(Folder, "sqlite.db");
            if (!db.exists()) {
                connect();
                createNewTable();
            }
        }
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - 您只安装了Vault，没有安装经济插件。正在卸载本插件……", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else {
            getLogger().info("[证券市场]加载成功");
        }
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        this.getCommand("sm").setExecutor(new CommandSm());
    }
    @Override
    public void onDisable() {
        PlayerJoinEvent.getHandlerList().unregister(this);
        getLogger().info("[证券市场]卸载成功");
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
    public static File getFolder() {
        return DataFolder;
    }
    public static FileConfiguration getServerconfig() {
        return config;
    }
}
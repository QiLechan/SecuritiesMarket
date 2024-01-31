package org.yuezhikong.plugins;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.yuezhikong.plugins.command.CommandSm;

import java.io.File;

import static org.yuezhikong.plugins.sqlite.SqliteManager.connect;
import static org.yuezhikong.plugins.sqlite.SqliteManager.createNewTable;

public class SecuritiesMarket extends JavaPlugin {
    private static Economy econ = null;
    private static File DataFolder = null;
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        DataFolder = this.getDataFolder();
        FileConfiguration config = getConfig();
        if (config.getInt("Database") == 1){
            String Folder = DataFolder.getPath() + "/sqlite.db";
            File db = new File(Folder);
            if (!db.exists()) {
                connect();
                createNewTable();
            }
        }
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
    public static File getFolder() {
        return DataFolder;
    }
}
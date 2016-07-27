package de.vincidev.bungeeban;

import de.vincidev.bungeeban.commands.*;
import de.vincidev.bungeeban.handlers.*;
import de.vincidev.bungeeban.util.ConfigManager;
import de.vincidev.bungeeban.util.SQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeBan extends Plugin {

    public static String PREFIX = "", CONSOLE_PREFIX = "[BungeeBan] ";

    private static BungeeBan instance;
    private static SQL sql;
    private static ConfigManager configManager;
    private MiscHandler joinHandler;

    public static BungeeBan getInstance() {
        return instance;
    }

    public static SQL getSQL() {
        return sql;
    }

    public static void setSQL(SQL sql) {
        BungeeBan.sql = sql;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.init();
        log("Config and files initialized. Now attempting to connect to SQL.");
        sql.openConnection();
        if (sql.isConnected()) {
            log("SQL successfully connected. Now creating tables.");
            sql.createTableIfNotExists("BungeeBan_Bans", "UUID VARCHAR(40), BanEnd BIGINT, BanReason VARCHAR(256), BannedBy VARCHAR(45)");
            register();
            log("Tables successfully created.");
            log("Loading complete!");
        } else {
            log("An internal error occured whilst connecting to SQL.");
        }
        
        joinHandler.setWhitelisted(joinHandler.isWhitelisted());
    }

    @Override
    public void onDisable() {
        sql.closeConnection();
    }

    public void register() {
        PluginManager pm = BungeeCord.getInstance().getPluginManager();
        
        joinHandler = new MiscHandler(this.getConfigManager().getFile().getParentFile());
        pm.registerListener(this, new BanHandler());
        pm.registerListener(this, new UnbanHandler());
        pm.registerListener(this, joinHandler);
        pm.registerCommand(this, new BanCommand("ban"));
        pm.registerCommand(this, new KickCommand("kick"));
        pm.registerCommand(this, new TempbanCommand("tempban"));
        pm.registerCommand(this, new UnbanCommand("unban"));
        pm.registerCommand(this, new CheckCommand("check"));
        pm.registerCommand(this, new WhitelistCommand("whitelist", joinHandler));
    }

    public void log(String str) {
        System.out.println(CONSOLE_PREFIX + str);
    }
}

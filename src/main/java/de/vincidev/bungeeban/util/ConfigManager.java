package de.vincidev.bungeeban.util;

import com.google.common.io.ByteStreams;
import de.vincidev.bungeeban.BungeeBan;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    Configuration configuration = null;

    public void init() {
        saveDefaultConfig();
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile());
            BungeeBan.PREFIX = getString("lang.prefix");
            BungeeBan.CONSOLE_PREFIX = getString("lang.console_prefix");
            if (configuration.getString("api") == null) {
                configuration.set("api", "eu");
                save();
            }
            if (configuration.getString("backend").equalsIgnoreCase("mysql")) {
                BungeeBan.setSQL(new SQL(configuration.getString("mysql.host"),
                        configuration.getInt("mysql.port"),
                        configuration.getString("mysql.username"),
                        configuration.getString("mysql.password"),
                        configuration.getString("mysql.database")));
            } else {
                BungeeBan.setSQL(new SQL(configuration.getString("sqlite.file")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String timeFormat(int days, int hours, int minutes, int seconds) {
        return getString("lang.time_format").replace("{DAYS}", "" + days).replace("{HOURS}", "" + hours)
                .replace("{MINUTES}", "" + minutes).replace("{SECONDS}", "" + seconds);
    }

    public String getString(String key, String... replace) {
        String str = configuration.getString(key);
        str = ChatColor.translateAlternateColorCodes('&', str);
        for (String repl : replace) {
            String[] r = repl.split("~");
            str = str.replace(r[0], r[1]);
        }
        return str;
    }

    public String getString(String key) {
        String str = configuration.getString(key);
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

    public List<String> getStringList(String key, String... replace) {
        List<String> list = getStringList(key);
        List<String> avail = new ArrayList<>();
        for (String str : list) {
            for (String repl : replace) {
                String[] r = repl.split("~");
                str = str.replace(r[0], r[1]);
            }
            avail.add(str);
        }
        return avail;
    }

    public List<String> getStringList(String key) {
        List<String> list = configuration.getStringList(key);
        List<String> avail = new ArrayList<>();
        for (String str : list) {
            avail.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return avail;
    }

    public File getFile() {
        return new File(BungeeBan.getInstance().getDataFolder(), "config.yml");
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!BungeeBan.getInstance().getDataFolder().exists()) {
            BungeeBan.getInstance().getDataFolder().mkdir();
        }
        File file = getFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream is = BungeeBan.getInstance().getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(file)) {
                    ByteStreams.copy(is, os);
                    os.close();
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

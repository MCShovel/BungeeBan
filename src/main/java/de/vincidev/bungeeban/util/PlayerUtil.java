package de.vincidev.bungeeban.util;

import de.vincidev.bungeeban.BungeeBan;
import net.md_5.bungee.BungeeCord;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerUtil {
    private static HashMap<String, UUID> uuidCache = new HashMap();
    private static HashMap<UUID, String> playernameCache = new HashMap();

    public static UUID getUniqueId(String playername) {
        if (uuidCache.containsKey(playername)) {
            return (UUID) uuidCache.get(playername);
        }
        if (BungeeCord.getInstance().getPlayer(playername) != null) {
            UUID uuid = BungeeCord.getInstance().getPlayer(playername).getUniqueId();
            uuidCache.put(playername, uuid);
            return uuid;
        }
        try {
            URLConnection conn = new URL("https://www.minecraftcapes.co.uk/Server-API/getInfo.php?username=" + playername + "&type=uuid").openConnection();
            String response = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (br.ready()) {
                response = response + br.readLine();
            }
            UUID uuid = UUID.fromString(response);
            uuidCache.put(playername, uuid);
            return uuid;
        } catch (Exception localException) {
        }
        return null;
    }

    public static String getUUID(String playername) {
        UUID uuid = getUniqueId(playername);
        if (uuid != null) {
            return uuid.toString();
        }
        return null;
    }

    public static String getPlayerName(UUID uuid) {
        if (playernameCache.containsKey(uuid)) {
            return (String) playernameCache.get(uuid);
        }
        if (BungeeCord.getInstance().getPlayer(uuid) != null) {
            String name = BungeeCord.getInstance().getPlayer(uuid).getName();
            playernameCache.put(uuid, name);
            return name;
        }
        try {
            URLConnection conn = new URL("https://www.minecraftcapes.co.uk/Server-API/getInfo.php?uuid=" + uuid.toString() + "&type=username").openConnection();
            String response = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (br.ready()) {
                response = response + br.readLine();
            }
            playernameCache.put(uuid, response);
            return response;
        } catch (Exception localException) {
        }
        return null;
    }

    public static String getPlayerName(String uuid) {
        return getPlayerName(UUID.fromString(uuid));
    }

    public static HashMap<Long, String> getNameHistory(UUID uuid) {
        try {
            URLConnection conn = new URL("https://" + BungeeBan.getConfigManager().getString("api") + ".mc-api.net/v3/history/" + uuid.toString()).openConnection();
            String response = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (br.ready()) {
                response = response + br.readLine();
            }
            JSONArray arr = new JSONObject(response).getJSONArray("history");
            HashMap<Long, String> history = new HashMap();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject change = arr.getJSONObject(i);
                String name = change.getString("name");
                long changedAt = 0L;
                if (change.has("changedToAt")) {
                    changedAt = change.getLong("changedToAt");
                }
                history.put(Long.valueOf(changedAt), name);
            }
            return history;
        } catch (Exception localException) {
        }
        return null;
    }
}

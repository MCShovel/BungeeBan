package de.vincidev.bungeeban.util;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.events.BungeeMuteEvent;
import de.vincidev.bungeeban.events.BungeeUnmuteEvent;
import net.md_5.bungee.BungeeCord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BungeeMuteManager {

    public static boolean ismuted(UUID uuid) {
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Mutes WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void unmute(UUID uuid, String unmutedBy) {
        BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUnmuteEvent(uuid, unmutedBy));
        BungeeBan.getSQL().update("DELETE FROM BungeeBan_Mutes WHERE UUID='" + uuid.toString() + "'");
    }

    public static void mute(UUID uuid, long seconds, String MuteReason, String MutedBy) {
        if (!ismuted(uuid)) {
            long end = -1L;
            if (seconds > 0) {
                end = System.currentTimeMillis() + (seconds * 1000);
            }
            BungeeCord.getInstance().getPluginManager().callEvent(new BungeeMuteEvent(uuid, end, MuteReason, MutedBy));
            BungeeBan.getSQL().update("INSERT INTO BungeeBan_Mutes(UUID, MuteEnd, MuteReason, MutedBy) " +
                    "VALUES('" + uuid.toString() + "', '" + end + "', '" + MuteReason + "', '" + MutedBy + "')");
        }
    }

    public static long getMuteEnd(UUID uuid) {
        long end = 0;
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Mutes WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                end = rs.getLong("MuteEnd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return end;
    }

    public static String getMuteReason(UUID uuid) {
        String str = "";
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Mutes WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                str = rs.getString("MuteReason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getWhomuted(UUID uuid) {
        String str = "";
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Mutes WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                str = rs.getString("MutedBy");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getRemainingmuteTime(UUID uuid) {
        if (ismuted(uuid)) {
            long end = getMuteEnd(uuid);
            if (end > 0) {
                long millis = end - System.currentTimeMillis();
                int days = 0;
                int hours = 0;
                int minutes = 0;
                int seconds = 0;
                while (millis >= 1000) {
                    seconds++;
                    millis -= 1000;
                }
                while (seconds >= 60) {
                    minutes++;
                    seconds -= 60;
                }
                while (minutes >= 60) {
                    hours++;
                    minutes -= 60;
                }
                while (hours >= 24) {
                    days++;
                    hours -= 24;
                }
                return BungeeBan.getConfigManager().timeFormat(days, hours, minutes, seconds);
            } else {
                return BungeeBan.getConfigManager().getString("lang.time_format_permanent");
            }
        }
        return null;
    }

    public static List<String> lines(UUID uuid) {
        return BungeeBan.getConfigManager().getStringList("lang.mutemessage",
                "{REASON}~" + getMuteReason(uuid),
                "{BY}~" + getWhomuted(uuid),
                "{REMAININGTIME}~" + getRemainingmuteTime(uuid));
    }

}

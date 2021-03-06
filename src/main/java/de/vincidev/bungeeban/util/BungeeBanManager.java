package de.vincidev.bungeeban.util;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.events.BungeeBanEvent;
import de.vincidev.bungeeban.events.BungeeUnbanEvent;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BungeeBanManager {

    public static boolean isBanned(UUID uuid) {
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void unban(UUID uuid, String unbannedBy) {
        BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUnbanEvent(uuid, unbannedBy));
        BungeeBan.getSQL().update("DELETE FROM BungeeBan_Bans WHERE UUID='" + uuid.toString() + "'");
    }

    public static void ban(UUID uuid, long seconds, String banReason, String bannedBy) {
        if (!isBanned(uuid)) {
            long end = -1L;
            if (seconds > 0) {
                end = System.currentTimeMillis() + (seconds * 1000);
            }
            BungeeBan.getSQL().update("INSERT INTO BungeeBan_Bans(UUID, BanEnd, BanReason, BannedBy) " +
                    "VALUES('" + uuid.toString() + "', '" + end + "', '" + banReason.replace("'", "''") + "', '" + bannedBy + "')");
            ProxiedPlayer target = BungeeCord.getInstance().getPlayer(uuid);
            if (target != null) {
                target.disconnect(getBanKickMessage(uuid));
            }
            BungeeCord.getInstance().getPluginManager().callEvent(new BungeeBanEvent(uuid, end, banReason, bannedBy));
        }
    }

    public static long getBanEnd(UUID uuid) {
        long end = 0;
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                end = rs.getLong("BanEnd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return end;
    }

    public static String getBanReason(UUID uuid) {
        String str = "-";
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                str = rs.getString("BanReason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getWhoBanned(UUID uuid) {
        String str = "";
        ResultSet rs = BungeeBan.getSQL().getResult("SELECT * FROM BungeeBan_Bans WHERE UUID='" + uuid.toString() + "'");
        try {
            if (rs.next()) {
                str = rs.getString("BannedBy");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getRemainingBanTime(UUID uuid) {
        if (isBanned(uuid)) {
            long end = getBanEnd(uuid);
            if (end > 0) {
                long remain = (end + 999) - System.currentTimeMillis();
                int days = (int)(remain / (24 * 60 * 60 * 1000));
                remain %= (24 * 60 * 60 * 1000);
                int hours = (int)(remain / (60 * 60 * 1000));
			    remain %= (60 * 60 * 1000);
                int minutes = (int)(remain / (60 * 1000));
				remain %= (60 * 1000);
                int seconds = (int)Math.round(remain / 1000.0);
                return BungeeBan.getConfigManager().timeFormat(days, hours, minutes, seconds);
            } else {
                return BungeeBan.getConfigManager().getString("lang.time_format_permanent");
            }
        }
        return null;
    }

    public static String getBanKickMessage(UUID uuid) {
        List<String> lines = BungeeBan.getConfigManager().getStringList("lang.banmessage",
                "{REASON}~" + getBanReason(uuid),
                "{BY}~" + getWhoBanned(uuid),
                "{REMAININGTIME}~" + getRemainingBanTime(uuid));
        String str = "";
        for (String line : lines) {
            str = str + line + "\n";
        }
        return str;
    }

}

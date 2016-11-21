package de.vincidev.bungeeban.handlers;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.events.BungeeBanEvent;
import de.vincidev.bungeeban.util.BungeeBanManager;
import de.vincidev.bungeeban.util.PlayerUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class BanHandler implements Listener {

    @EventHandler
    public void onBan(BungeeBanEvent e) {
        List<String> messages = BungeeBan.getConfigManager().getStringList("lang.commands.ban.broadcast",
                "{REASON}~" + e.getBanReason(),
                "{BY}~" + e.getBannedBy(),
                "{NAME}~" + PlayerUtil.getPlayerName(e.getBanned()),
                "{REMAININGTIME}~" + BungeeBanManager.getRemainingBanTime(e.getBanned()));
        for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
            if (p.hasPermission("BungeeBan.Broadcast")) {
                for (String msg : messages) {
                    p.sendMessage(BungeeBan.PREFIX + msg);
                }
            }
        }
    }

}

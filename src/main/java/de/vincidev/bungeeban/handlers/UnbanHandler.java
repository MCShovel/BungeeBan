package de.vincidev.bungeeban.handlers;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.events.BungeeUnbanEvent;
import de.vincidev.bungeeban.util.PlayerUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class UnbanHandler implements Listener {

    @EventHandler
    public void onUnban(BungeeUnbanEvent e) {
        List<String> messages = BungeeBan.getConfigManager().getStringList("lang.commands.unban.broadcast",
                "{BY}~" + e.getUnbannedBy(),
                "{NAME}~" + PlayerUtil.getPlayerName(e.getUnbanned()));
        for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
            if (p.hasPermission("BungeeBan.Broadcast")) {
                for (String msg : messages) {
                    p.sendMessage(BungeeBan.PREFIX + msg);
                }
            }
        }
    }

}

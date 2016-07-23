package de.vincidev.bungeeban.handlers;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.events.BungeeUnmuteEvent;
import de.vincidev.bungeeban.util.PlayerUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class UnmuteHandler implements Listener {

    @EventHandler
    public void onUnmute(BungeeUnmuteEvent e) {
        List<String> messages = BungeeBan.getConfigManager().getStringList("lang.commands.inmite.broadcast",
                "{BY}~" + e.getUnmutedBy(),
                "{NAME}~" + PlayerUtil.getPlayerName(e.getUnmuted()));
        for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
            if (p.hasPermission("BungeeBan.Broadcast.Unmute")) {
                for (String msg : messages) {
                    p.sendMessage(BungeeBan.PREFIX + msg);
                }
            }
        }
    }

}

package de.vincidev.bungeeban.handlers;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.util.BungeeBanManager;
import de.vincidev.bungeeban.util.BungeeMuteManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class MiscHandler implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent ev) {
        String partialPlayerName = ev.getCursor().toLowerCase();
        int lastSpaceIndex = partialPlayerName.lastIndexOf(' ');
        if (lastSpaceIndex >= 0) {
            partialPlayerName = partialPlayerName.substring(lastSpaceIndex + 1);
        }
        for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
            if (p.getName().toLowerCase().startsWith(partialPlayerName)) {
                ev.getSuggestions().add(p.getName());
            }
        }
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        if (BungeeBanManager.isBanned(uuid)) {
            long end = BungeeBanManager.getBanEnd(uuid);
            long current = System.currentTimeMillis();
            if (end > 0) {
                if (end < current) {
                    BungeeBanManager.unban(uuid, "automatic");
                } else {
                    e.setCancelled(true);
                    e.setCancelReason(BungeeBanManager.getBanKickMessage(uuid));
                }
            } else {
                e.setCancelled(true);
                e.setCancelReason(BungeeBanManager.getBanKickMessage(uuid));
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = ((ProxiedPlayer) e.getSender());
        String msg = e.getMessage();
        if (BungeeMuteManager.ismuted(p.getUniqueId())) {
            long end = BungeeMuteManager.getMuteEnd(p.getUniqueId());
            long current = System.currentTimeMillis();
            if (end > 0) {
                if (end > current) {
                    if (msg.startsWith("/")) {
                        List<String> blocked = BungeeBan.getConfigManager().getStringList("mute_deny_commands");
                        for (String block : blocked) {
                            if (msg.toLowerCase().startsWith(block.toLowerCase())) {
                                e.setCancelled(true);
                                for (String part : BungeeMuteManager.lines(p.getUniqueId())) {
                                    p.sendMessage(BungeeBan.PREFIX + part);
                                }
                                return;
                            }
                        }
                    } else {
                        for (String part : BungeeMuteManager.lines(p.getUniqueId())) {
                            p.sendMessage(BungeeBan.PREFIX + part);
                        }
                        e.setCancelled(true);
                    }
                } else {
                    BungeeMuteManager.unmute(p.getUniqueId(), "automatic");
                }
            } else {
                if (msg.startsWith("/")) {
                    List<String> blocked = BungeeBan.getConfigManager().getStringList("mute_deny_commands");
                    for (String block : blocked) {
                        if (msg.toLowerCase().startsWith(block.toLowerCase())) {
                            e.setCancelled(true);
                            for (String part : BungeeMuteManager.lines(p.getUniqueId())) {
                                p.sendMessage(BungeeBan.PREFIX + part);
                            }
                            return;
                        }
                    }
                } else {
                    for (String part : BungeeMuteManager.lines(p.getUniqueId())) {
                        p.sendMessage(BungeeBan.PREFIX + part);
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

}

package de.vincidev.bungeeban.commands;

import de.vincidev.bungeeban.BungeeBan;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class KickCommand extends Command {

    public KickCommand(String name) {
        super(name);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeeBan.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("BungeeBan.Kick")) {
                    if (args.length >= 2) {
                        String playername = args[0];
                        String reason = "";
                        for (int i = 1; i <= args.length - 1; i++) {
                            reason = reason + args[i] + " ";
                        }
                        ProxiedPlayer p = BungeeCord.getInstance().getPlayer(playername);
                        if (p != null) {
                            String kickmsg = "";
                            List<String> kickmsgs = BungeeBan.getConfigManager().getStringList("lang.kickedmessage",
                                    "{REASON}~" + reason,
                                    "{BY}~" + sender.getName());
                            for (String part : kickmsgs) {
                                kickmsg = kickmsg + part + "\n";
                            }
                            p.disconnect(kickmsg);
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.kick.kicked", "{NAME}~" + p.getName()));
                            List<String> messages = BungeeBan.getConfigManager().getStringList("lang.commands.kick.broadcast",
                                    "{REASON}~" + reason,
                                    "{BY}~" + sender.getName(),
                                    "{NAME}~" + p.getName());
                            for (ProxiedPlayer o : BungeeCord.getInstance().getPlayers()) {
                                if (o.hasPermission("BungeeBan.Broadcast.Ban")) {
                                    for (String msg : messages) {
                                        o.sendMessage(BungeeBan.PREFIX + msg);
                                    }
                                }
                            }

                        } else {
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_not_online", "{NAME}~" + playername));
                        }
                    } else {
                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.ban.syntax"));
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.no_permissions"));
                }
            }
        });
    }

}

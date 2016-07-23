package de.vincidev.bungeeban.commands;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.util.BungeeMuteManager;
import de.vincidev.bungeeban.util.PlayerUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class MuteCommand extends Command {

    public MuteCommand(String name) {
        super(name);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeeBan.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("bungeeban.Mute")) {
                    if (args.length >= 2) {
                        String playername = args[0];
                        String reason = "";
                        for (int i = 1; i <= args.length - 1; i++) {
                            reason = reason + args[i] + " ";
                        }
                        UUID uuid = PlayerUtil.getUniqueId(playername);
                        if (uuid != null) {
                            if (!BungeeMuteManager.ismuted(uuid)) {
                                sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.mute.muted", "{NAME}~" + playername));
                                BungeeMuteManager.mute(uuid, -1, reason, sender.getName());
                            } else {
                                sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_already_muted", "{NAME}~" + playername));
                            }
                        } else {
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_uuid_not_found"));
                        }
                    } else {
                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.mute.syntax"));
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.no_permissions"));
                }
            }
        });
    }

}

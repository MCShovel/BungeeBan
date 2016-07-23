package de.vincidev.bungeeban.commands;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.util.BungeeMuteManager;
import de.vincidev.bungeeban.util.PlayerUtil;
import de.vincidev.bungeeban.util.TimeUnit;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class TempmuteCommand extends Command {

    public TempmuteCommand(String name) {
        super(name);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeeBan.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("BungeeBan.TempMute")) {
                    if (args.length >= 4) {
                        String playername = args[0];
                        String reason = "";
                        for (int i = 3; i <= args.length - 1; i++) {
                            reason = reason + args[i] + " ";
                        }
                        UUID uuid = PlayerUtil.getUniqueId(playername);
                        if (uuid != null) {
                            if (!BungeeMuteManager.ismuted(uuid)) {
                                try {
                                    long seconds = Integer.parseInt(args[1]);
                                    TimeUnit unit = TimeUnit.getByString(args[2]);
                                    if (unit != null) {
                                        seconds = seconds * unit.getSeconds();
                                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.tempmute.muted", "{NAME}~" + playername));
                                        BungeeMuteManager.mute(uuid, seconds, reason, sender.getName());
                                    } else {
                                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.unknown_timeunit"));
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.internal_error", "{DETAILS}~\"" + args[1] + "\" is not a number."));
                                }
                            } else {
                                sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_already_muted", "{NAME}~" + playername));
                            }
                        } else {
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_uuid_not_found"));
                        }
                    } else {
                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.tempmute.syntax"));
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.no_permissions"));
                }
            }
        });
    }

}

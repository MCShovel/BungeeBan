package de.vincidev.bungeeban.commands;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.util.BungeeBanManager;
import de.vincidev.bungeeban.util.PlayerUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class UnbanCommand extends Command {

    public UnbanCommand(String name) {
        super(name);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeeBan.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("BungeeBan.Unban")) {
                    if (args.length == 1) {
                        String playername = args[0];
                        UUID uuid = PlayerUtil.getUniqueId(playername);
                        if (uuid != null) {
                            if (BungeeBanManager.isBanned(uuid)) {
                                sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.unban.unbanned", "{NAME}~" + playername));
                                BungeeBanManager.unban(uuid, sender.getName());
                            } else {
                                sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_not_banned", "{NAME}~" + playername));
                            }
                        } else {
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_uuid_not_found"));
                        }
                    } else {
                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.unban.syntax"));
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.no_permissions"));
                }
            }
        });
    }

}

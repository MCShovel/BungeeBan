package de.vincidev.bungeeban.commands;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.util.BungeeBanManager;
import de.vincidev.bungeeban.util.BungeeMuteManager;
import de.vincidev.bungeeban.util.PlayerUtil;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.UUID;

public class CheckCommand extends Command {

    public CheckCommand(String name) {
        super(name);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeeBan.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("BungeeBan.Check")) {
                    if (args.length == 1) {
                        UUID uuid = PlayerUtil.getUniqueId(args[0]);
                        if (uuid != null) {
                            String playername = PlayerUtil.getPlayerName(uuid);
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.check.uuid", "{UUID}~" + uuid.toString()));
                            if (BungeeBanManager.isBanned(uuid)) {
                                List<String> msgs = BungeeBan.getConfigManager().getStringList("lang.commands.check.banned.positive",
                                        "{NAME}~" + playername,
                                        "{REASON}~" + BungeeBanManager.getBanReason(uuid),
                                        "{BY}~" + BungeeBanManager.getWhoBanned(uuid),
                                        "{REMAININGTIME}~" + BungeeBanManager.getRemainingBanTime(uuid));
                                for (String msg : msgs) {
                                    sender.sendMessage(BungeeBan.PREFIX + msg);
                                }
                            } else {
                                List<String> msgs = BungeeBan.getConfigManager().getStringList("lang.commands.check.banned.negative",
                                        "{NAME}~" + playername);
                                for (String msg : msgs) {
                                    sender.sendMessage(BungeeBan.PREFIX + msg);
                                }
                            }
                            if (BungeeMuteManager.ismuted(uuid)) {
                                List<String> msgs = BungeeBan.getConfigManager().getStringList("lang.commands.check.muted.positive",
                                        "{NAME}~" + playername,
                                        "{REASON}~" + BungeeMuteManager.getMuteReason(uuid),
                                        "{BY}~" + BungeeMuteManager.getWhomuted(uuid),
                                        "{REMAININGTIME}~" + BungeeMuteManager.getRemainingmuteTime(uuid));
                                for (String msg : msgs) {
                                    sender.sendMessage(BungeeBan.PREFIX + msg);
                                }
                            } else {
                                List<String> msgs = BungeeBan.getConfigManager().getStringList("lang.commands.check.muted.negative",
                                        "{NAME}~" + playername);
                                for (String msg : msgs) {
                                    sender.sendMessage(BungeeBan.PREFIX + msg);
                                }
                            }
                        } else {
                            sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.player_uuid_not_found"));
                        }
                    } else {
                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.check.syntax"));
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.no_permissions"));
                }
            }
        });
    }

}

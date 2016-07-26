package de.vincidev.bungeeban.commands;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.handlers.MiscHandler;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class WhitelistCommand extends Command {

	private final MiscHandler _joinHandler;
	
    public WhitelistCommand(String name, MiscHandler joinHandler) {
        super(name);
    	_joinHandler = joinHandler;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeCord.getInstance().getScheduler().runAsync(BungeeBan.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("BungeeBan.Whitelist.Admin")) {
                    if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
                        boolean enable = args[0].equalsIgnoreCase("on");

                        if (enable != _joinHandler.isWhitelisted()) {
                        	_joinHandler.setWhitelisted(enable);
                        	if (enable) {
	                            for (ProxiedPlayer p : BungeeCord.getInstance().getPlayers()) {
	                        		if (!_joinHandler.isWhitelistPlayer(p.getName(), p.getUniqueId())) {
	                        			p.disconnect(BungeeBan.getConfigManager().getString("lang.errors.whitelisted"));
	                        		}	                            	
	                            }
                        	}
                        }
                    } else {
                        sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.commands.whitelist.syntax"));
                    }
                } else {
                    sender.sendMessage(BungeeBan.PREFIX + BungeeBan.getConfigManager().getString("lang.errors.no_permissions"));
                }
            }
        });
    }

}

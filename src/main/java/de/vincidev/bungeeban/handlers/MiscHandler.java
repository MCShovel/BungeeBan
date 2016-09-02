package de.vincidev.bungeeban.handlers;

import de.vincidev.bungeeban.BungeeBan;
import de.vincidev.bungeeban.util.BungeeBanManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MiscHandler implements Listener {

	private final File _whitelisted;
	private ArrayList<String> savedMotd;
	
    public MiscHandler(File parentFile) {
    	_whitelisted = new File(parentFile, "whitelisted");
	}

    public boolean isWhitelisted() {
    	return _whitelisted.exists();
    }

	public boolean isWhitelistPlayer(String name, UUID uniqueId) {
		// TODO Auto-generated method stub
		String val = 
				String.valueOf(BungeeBan.getConfigManager().getString("whitelist." + uniqueId.toString())) +
				String.valueOf(BungeeBan.getConfigManager().getString("whitelist." + String.valueOf(name)));
		if ("true".equalsIgnoreCase(val)) {
			return true;
		}

		BungeeBan.getInstance().getLogger().info("Failed whitelist check (" + val + ") for " + "whitelist." + uniqueId.toString() + " / whitelist." + String.valueOf(name));
		return false;
	}
    
    public void setWhitelisted(boolean on) {
		try {
	    	if (on) {
	    		if (!_whitelisted.exists())
	    			_whitelisted.createNewFile();

	    		Collection<ListenerInfo> listeners = ProxyServer.getInstance().getConfig().getListeners();
	    		savedMotd = new ArrayList<String>();
	    		
	            for ( ListenerInfo info : listeners )
	            {
	            	try {
	            		savedMotd.add(info.getMotd());
		            	Field f = info.getClass().getDeclaredField("motd");
		            	f.setAccessible(true); // which enables accessibility
	            		f.set(info, BungeeBan.getConfigManager().getString("lang.errors.whitelisted"));
	            	}
	            	catch(Exception e) {
	            	}
	            }
	    	} else {
	    		if (_whitelisted.exists())
	    			_whitelisted.delete();

	    		int ix = 0;
	    		Collection<ListenerInfo> listeners = ProxyServer.getInstance().getConfig().getListeners();
	            for ( ListenerInfo info : listeners )
	            {
	            	if (savedMotd == null || ix >= savedMotd.size()) {
	            		break;
	            	}
	            	
	            	try {
	            		Field f = info.getClass().getDeclaredField("motd");
		            	f.setAccessible(true); // which enables accessibility
	            		f.set(info, savedMotd.get(ix));
	            	}
	            	catch(Exception e) {
	            	}
	            }
	    		
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
    	PendingConnection con = e.getConnection();
        UUID uuid = con.getUniqueId();
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
        
        if (isWhitelisted() && !isWhitelistPlayer(con.getName(), con.getUniqueId())) {
            e.setCancelled(true);
            e.setCancelReason(BungeeBan.getConfigManager().getString("lang.errors.whitelisted"));
        }
    }
}

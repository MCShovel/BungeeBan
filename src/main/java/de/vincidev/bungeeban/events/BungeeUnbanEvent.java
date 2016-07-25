package de.vincidev.bungeeban.events;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class BungeeUnbanEvent extends Event {

    private UUID unbanned;
    private String unbannedBy;

    public BungeeUnbanEvent(UUID unbanned, String unbannedBy) {
        this.unbanned = unbanned;
        this.unbannedBy = unbannedBy;
    }

    public String getUnbannedBy() {
        return unbannedBy;
    }

    public UUID getUnbanned() {
        return unbanned;
    }
}

package de.vincidev.bungeeban.events;

import net.md_5.bungee.api.plugin.Event;

import java.util.UUID;

public class BungeeBanEvent extends Event {

    private UUID banned;
    private long banEnd;
    private String banReason;
    private String bannedBy;

    public BungeeBanEvent(UUID banned, long banEnd, String banReason, String bannedBy) {
        this.banned = banned;
        this.banEnd = banEnd;
        this.banReason = banReason;
        this.bannedBy = bannedBy;
    }

    public long getBanEnd() {
        return banEnd;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public String getBanReason() {
        return banReason;
    }

    public UUID getBanned() {
        return banned;
    }
}

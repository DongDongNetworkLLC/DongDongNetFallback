package com.dongdongnetwork.punishment.bungee.event;
import com.dongdongnetwork.punishment.utils.Punishment;
import net.md_5.bungee.api.plugin.Event;
public class PunishmentEvent extends Event {
    private final Punishment punishment;
    public PunishmentEvent(Punishment punishment) {
        this.punishment = punishment;
    }
    public Punishment getPunishment() {
        return punishment;
    }
}
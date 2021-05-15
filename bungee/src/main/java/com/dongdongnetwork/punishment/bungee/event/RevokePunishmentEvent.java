package com.dongdongnetwork.punishment.bungee.event;
import com.dongdongnetwork.punishment.utils.Punishment;
import net.md_5.bungee.api.plugin.Event;
public class RevokePunishmentEvent extends Event {
    private final Punishment punishment;
    private final boolean massClear;
    public RevokePunishmentEvent(Punishment punishment, boolean massClear) {
        this.punishment = punishment;
        this.massClear = massClear;
    }
    public Punishment getPunishment() {
        return punishment;
    }
    public boolean isMassClear() {
        return massClear;
    }
}
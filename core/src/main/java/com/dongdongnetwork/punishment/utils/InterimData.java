package com.dongdongnetwork.punishment.utils;
import com.dongdongnetwork.punishment.manager.PunishmentManager;
import java.util.Set;

public class InterimData {

    private final String uuid, name, ip;
    private final Set<Punishment> punishments, history;

    public InterimData(String uuid, String name, String ip, Set<Punishment> punishments, Set<Punishment> history) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.punishments = punishments;
        this.history = history;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Set<Punishment> getPunishments() {
        return punishments;
    }

    public Set<Punishment> getHistory() {
        return history;
    }

    public Punishment getBan() {
        for (Punishment pt : punishments) {
            if (pt.getType().getBasic() == PunishmentType.BAN && !pt.isExpired()) {
                return pt;
            }
        }
        return null;
    }

    public void accept() {
        PunishmentManager.get().getLoadedPunishments(false).addAll(punishments);
        PunishmentManager.get().getLoadedHistory().addAll(history);
        PunishmentManager.get().setCached(this);
    }
}
package com.dongdongnetwork.punishment.utils;
public enum PunishmentType {
    BAN("Ban", null, false, "dongdong.punish.ban.perma"),
    TEMP_BAN("Tempban", BAN, true, "dongdong.punish.ban.temp"),
    IP_BAN("Ipban", BAN, false, "dongdong.punish.ipban.perma"),
    TEMP_IP_BAN("Tempipban", BAN, true, "dongdong.punish.ipban.temp"),
    MUTE("Mute", null, false, "dongdong.punish.mute.perma"),
    TEMP_MUTE("Tempmute", MUTE, true, "dongdong.punish.mute.temp"),
    WARNING("Warn", null, false, "dongdong.punish.warn.perma"),
    TEMP_WARNING("Tempwarn", WARNING, true, "dongdong.punish.warn.temp"),
    KICK("Kick", null, false, "dongdong.punish.kick.use"),
    NOTE("Note", null, false, "dongdong.punish.note.use");

    private final String name;
    private final String perms;
    private final PunishmentType basic;
    private final boolean temp;

    PunishmentType(String name, PunishmentType basic, boolean temp, String perms) {
        this.name = name;
        this.basic = basic;
        this.temp = temp;
        this.perms = perms;
    }

    public static PunishmentType fromCommandName(String cmd) {
        switch (cmd) {
            case "ban":
                return BAN;
            case "tempban":
                return TEMP_BAN;
            case "mute":
                return MUTE;
            case "tempmute":
                return TEMP_MUTE;
            default:
                return null;
        }
    }

    public String getName() {
        return name;
    }

    public String getPerms() {
        return perms;
    }

    public boolean isTemp() {
        return temp;
    }

    public String getConfSection(String path) {
        return name+"."+path;
    }

    public PunishmentType getBasic() {
        return basic == null ? this : basic;
    }

    public PunishmentType getPermanent() {
        if(this == IP_BAN || this == TEMP_IP_BAN)
            return IP_BAN;

        return getBasic();
    }

    public boolean isIpOrientated() {
        return this == IP_BAN || this == TEMP_IP_BAN;
    }
}
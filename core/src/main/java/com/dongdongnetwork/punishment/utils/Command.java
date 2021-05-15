package com.dongdongnetwork.punishment.utils;
import com.dongdongnetwork.punishment.MethodInterface;
import com.dongdongnetwork.punishment.Universal;
import com.dongdongnetwork.punishment.manager.DatabaseManager;
import com.dongdongnetwork.punishment.manager.MessageManager;
import com.dongdongnetwork.punishment.manager.PunishmentManager;
import com.dongdongnetwork.punishment.manager.UUIDManager;
import com.dongdongnetwork.punishment.utils.commands.ListProcessor;
import com.dongdongnetwork.punishment.utils.commands.PunishmentProcessor;
import com.dongdongnetwork.punishment.utils.commands.RevokeByIdProcessor;
import com.dongdongnetwork.punishment.utils.commands.RevokeProcessor;
import com.dongdongnetwork.punishment.utils.tabcompletion.BasicTabCompleter;
import com.dongdongnetwork.punishment.utils.tabcompletion.CleanTabCompleter;
import com.dongdongnetwork.punishment.utils.tabcompletion.PunishmentTabCompleter;
import com.dongdongnetwork.punishment.utils.tabcompletion.TabCompleter;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import static com.dongdongnetwork.punishment.utils.CommandUtils.*;
import static com.dongdongnetwork.punishment.utils.tabcompletion.MutableTabCompleter.list;
public enum Command {
    BAN(
            PunishmentType.BAN.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.BAN),
            PunishmentType.BAN.getConfSection("Usage"),
            "ban"),
    TEMP_BAN(
            PunishmentType.TEMP_BAN.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_BAN),
            PunishmentType.TEMP_BAN.getConfSection("Usage"),
            "tempban"),

    MUTE(
            PunishmentType.MUTE.getPerms(),
            ".+",
            new PunishmentTabCompleter(false),
            new PunishmentProcessor(PunishmentType.MUTE),
            PunishmentType.MUTE.getConfSection("Usage"),
            "mute"),

    TEMP_MUTE(
            PunishmentType.TEMP_MUTE.getPerms(),
            "(-s )?\\S+ ?([1-9][0-9]*([wdhms]|mo)|#.+)( .*)?",
            new PunishmentTabCompleter(true),
            new PunishmentProcessor(PunishmentType.TEMP_MUTE),
            PunishmentType.TEMP_MUTE.getConfSection("Usage"),
            "tempmute"),

    UN_BAN("dongdong.punish." + PunishmentType.BAN.getName() + ".undo",
            "\\S+",
            new BasicTabCompleter("[Name/IP]"),
            new RevokeProcessor(PunishmentType.BAN),
            "Un" + PunishmentType.BAN.getConfSection("Usage"),
            "unban"),

    UN_MUTE("dongdong.punish." + PunishmentType.MUTE.getName() + ".undo",
            "\\S+",
            new BasicTabCompleter(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]"),
            new RevokeProcessor(PunishmentType.MUTE),
            "Un" + PunishmentType.MUTE.getConfSection("Usage"),
            "unmute"),

    UN_PUNISH("dongdong.punish.all.undo",
            "[0-9]+",
            new BasicTabCompleter("<ID>"),
            new RevokeByIdProcessor("UnPunish", PunishmentManager.get()::getPunishment),
            "UnPunish.Usage",
            "unpunish"),
    CHANGE_REASON("dongdong.punish.changeReason",
            "([0-9]+|(?i)(ban|mute) \\S+) .+",
            new CleanTabCompleter((user, args) -> {
                if(args.length <= 1) {
                    return list("<ID>", "ban", "mute");
                }else {
                    boolean playerTarget = args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("mute");
                    if(args.length == 2 && playerTarget){
                        return list(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]");
                    } else if((playerTarget && args.length == 3) || args.length == 2){
                        return list("new reason...");
                    } else {
                        return list();
                    }
                }
            }),
            input -> {
                Punishment punishment;

                if (input.getPrimaryData().matches("[0-9]*")) {
                    int id = Integer.parseInt(input.getPrimaryData());
                    input.next();

                    punishment = PunishmentManager.get().getPunishment(id);
                } else {
                    PunishmentType type = PunishmentType.valueOf(input.getPrimary().toUpperCase());
                    input.next();

                    String target = input.getPrimary();
                    if (!target.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                        target = processName(input);
                        if (target == null)
                            return;
                    } else {
                        input.next();
                    }

                    punishment = getPunishment(target, type);
                }

                String reason = processReason(input);
                if (reason == null)
                    return;

                if (punishment != null) {
                    punishment.updateReason(reason);
                    MessageManager.sendMessage(input.getSender(), "ChangeReason.Done",
                            true, "ID", String.valueOf(punishment.getId()));
                } else {
                    MessageManager.sendMessage(input.getSender(), "ChangeReason.NotFound", true);
                }
            },
            "ChangeReason.Usage",
            "change-reason"),

    BAN_LIST("dongdong.punish.bans",
            "([1-9][0-9]*)?",
            new BasicTabCompleter("<Page>"),
            new ListProcessor(
                    target -> PunishmentManager.get().getPunishments(SQLQuery.SELECT_ALL_PUNISHMENTS_LIMIT, 150),
                    "Bans", false, false),
            "Bans.Usage",
            "bans"),

    HISTORY("dongdong.punish.history",
            "\\S+( [1-9][0-9]*)?",
            new CleanTabCompleter((user, args) -> {
                if(args.length == 1)
                    return list(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]");
                else if(args.length == 2)
                    return list("<Page>");
                else
                    return list();
            }),
            new ListProcessor(
                    target -> PunishmentManager.get().getPunishments(target, null, false),
                    "History", true, true),
            "History.Usage",
            "history"),

    CHECK("dongdong.punish.check",
            "\\S+",
            new BasicTabCompleter(CleanTabCompleter.PLAYER_PLACEHOLDER, "[Name]"),
            input -> {
                String name = input.getPrimary();

                String uuid = processName(input);
                if (uuid == null)
                    return;

                String ip = Universal.get().getIps().getOrDefault(name.toLowerCase(), "none cashed");
                String loc = Universal.get().getMethods().getFromUrlJson("http://ip-api.com/json/" + ip, "country");
                Punishment mute = PunishmentManager.get().getMute(uuid);
                Punishment ban = PunishmentManager.get().getBan(uuid);

                String cached = MessageManager.getMessage("Check.Cached", false);
                String notCached = MessageManager.getMessage("Check.NotCached", false);

                boolean nameCached = PunishmentManager.get().isCached(name.toLowerCase());
                boolean ipCached = PunishmentManager.get().isCached(ip);
                boolean uuidCached = PunishmentManager.get().isCached(uuid);

                Object sender = input.getSender();
                MessageManager.sendMessage(sender, "Check.Header", true, "NAME", name, "CACHED", nameCached ? cached : notCached);
                MessageManager.sendMessage(sender, "Check.UUID", false, "UUID", uuid, "CACHED", uuidCached ? cached : notCached);
                if (Universal.get().hasPerms(sender, "dongdong.punish.check.ip")) {
                    MessageManager.sendMessage(sender, "Check.IP", false, "IP", ip, "CACHED", ipCached ? cached : notCached);
                }
                MessageManager.sendMessage(sender, "Check.Geo", false, "LOCATION", loc == null ? "failed!" : loc);
                MessageManager.sendMessage(sender, "Check.Mute", false, "DURATION", mute == null ? "§anone" : mute.getType().isTemp() ? "§e" + mute.getDuration(false) : "§cperma");
                if (mute != null) {
                    MessageManager.sendMessage(sender, "Check.MuteReason", false, "REASON", mute.getReason());
                }
                MessageManager.sendMessage(sender, "Check.Ban", false, "DURATION", ban == null ? "§anone" : ban.getType().isTemp() ? "§e" + ban.getDuration(false) : "§cperma");
                if (ban != null) {
                    MessageManager.sendMessage(sender, "Check.BanReason", false, "REASON", ban.getReason());
                }
                MessageManager.sendMessage(sender, "Check.Warn", false, "COUNT", PunishmentManager.get().getCurrentWarns(uuid) + "");

                MessageManager.sendMessage(sender, "Check.Note", false, "COUNT", PunishmentManager.get().getCurrentNotes(uuid) + "");
            },
            "Check.Usage",
            "check");
    private final String permission;
    private final Predicate<String[]> syntaxValidator;
    private final TabCompleter tabCompleter;
    private final Consumer<CommandInput> commandHandler;
    private final String usagePath;
    private final String[] names;
    Command(String permission, Predicate<String[]> syntaxValidator,
            TabCompleter tabCompleter, Consumer<CommandInput> commandHandler, String usagePath, String... names) {
        this.permission = permission;
        this.syntaxValidator = syntaxValidator;
        this.tabCompleter = tabCompleter;
        this.commandHandler = commandHandler;
        this.usagePath = usagePath;
        this.names = names;
    }
    Command(String permission, String regex, TabCompleter tabCompleter, Consumer<CommandInput> commandHandler,
            String usagePath, String... names) {
        this(permission, (args) -> String.join(" ", args).matches(regex), tabCompleter, commandHandler, usagePath, names);
    }
    public boolean validateArguments(String[] args) {
        return syntaxValidator.test(args);
    }
    public void execute(Object player, String[] args) {
        commandHandler.accept(new CommandInput(player, args));
    }
    public TabCompleter getTabCompleter() {
        return tabCompleter;
    }
    public static Command getByName(String name) {
        String lowerCase = name.toLowerCase();
        for (Command command : values()) {
            for (String s : command.names) {
                if (s.equals(lowerCase))
                    return command;
            }
        }
        return null;
    }
    public String getPermission() {
        return this.permission;
    }
    public Predicate<String[]> getSyntaxValidator() {
        return this.syntaxValidator;
    }

    public Consumer<CommandInput> getCommandHandler() {
        return this.commandHandler;
    }

    public String getUsagePath() {
        return this.usagePath;
    }

    public String[] getNames() {
        return this.names;
    }

    public static class CommandInput {
        private Object sender;
        private String[] args;

        CommandInput(Object sender, String[] args) {
            this.sender = sender;
            this.args = args;
        }

        public String getPrimary() {
            return args.length == 0 ? null : args[0];
        }

        String getPrimaryData() {
            return getPrimary().toLowerCase();
        }

        public void removeArgument(int index) {
            args = ArrayUtils.remove(args, index);
        }

        public void next() {
            args = ArrayUtils.remove(args, 0);
        }

        public boolean hasNext() {
            return args.length > 0;
        }

        public Object getSender() {
            return this.sender;
        }

        public String[] getArgs() {
            return this.args;
        }
    }
}

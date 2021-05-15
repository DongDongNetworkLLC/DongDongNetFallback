package com.dongdongnetwork.punishment;
import com.google.gson.Gson;
import com.dongdongnetwork.punishment.manager.*;
import com.dongdongnetwork.punishment.utils.Command;
import com.dongdongnetwork.punishment.utils.InterimData;
import com.dongdongnetwork.punishment.utils.Punishment;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class Universal {
    private static Universal instance = null;
    public static void setRedis(boolean redis) {
        Universal.redis = redis;
    }
    private final Map<String, String> ips = new HashMap<>();
    private MethodInterface mi;
    private LogManager logManager;
    private static boolean redis = false;
    private final Gson gson = new Gson();
    public static Universal get() {
        return instance == null ? instance = new Universal() : instance;
    }
    public void setup(MethodInterface mi) {
        this.mi = mi;
        mi.loadFiles();
        logManager = new LogManager();
        UpdateManager.get().setup();
        UUIDManager.get().setup();
        try {
            DatabaseManager.get().setup(mi.getBoolean(mi.getConfig(), "UseMySQL", false));
        } catch (Exception ex) {
            log("Failed enabling database-manager...");
            debugException(ex);
        }
        mi.setupMetrics();
        PunishmentManager.get().setup();
        for (Command command : Command.values()) {
            for (String commandName : command.getNames()) {
                mi.setCommandExecutor(commandName, command.getTabCompleter());
            }
        }
        if (mi.getBoolean(mi.getConfig(), "DetailedEnableMessage", true)) {

        } else {
			
        }
    }
    public void shutdown() {
        DatabaseManager.get().shutdown();
    }
    public MethodInterface getMethods() {
        return mi;
    }
    public boolean isBungee() {
        return mi.isBungee();
    }
    public Map<String, String> getIps() {
        return ips;
    }
    public static boolean isRedis() {
        return redis;
    }
    public Gson getGson() {
        return gson;
    }
    public String getFromURL(String surl) {
        String response = null;
        try {
            URL url = new URL(surl);
            Scanner s = new Scanner(url.openStream());
            if (s.hasNext()) {
                response = s.next();
                s.close();
            }
        } catch (IOException exc) {
            debug("!! Failed to connect to URL: " + surl);
        }
        return response;
    }
    public boolean isMuteCommand(String cmd) {
        return isMuteCommand(cmd, getMethods().getStringList(getMethods().getConfig(), "MuteCommands"));
    }
    boolean isMuteCommand(String cmd, List<String> muteCommands) {
        String[] words = cmd.split(" ");
        if (words[0].indexOf(':') != -1) {
            words[0] = words[0].split(":", 2)[1];
        }
        for (String muteCommand : muteCommands) {
            if (muteCommandMatches(words, muteCommand)) {
                return true;
            }
        }
        return false;
    }
    boolean muteCommandMatches(String[] commandWords, String muteCommand) {
        if (commandWords[0].equalsIgnoreCase(muteCommand)) {
            return true;
        }
        if (muteCommand.indexOf(' ') != -1) {
            String[] muteCommandWords = muteCommand.split(" ");
            if (muteCommandWords.length > commandWords.length) {
                return false;
            }
            for (int n = 0; n < muteCommandWords.length; n++) {
                if (!muteCommandWords[n].equalsIgnoreCase(commandWords[n])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public boolean isExemptPlayer(String name) {
        List<String> exempt = getMethods().getStringList(getMethods().getConfig(), "ExemptPlayers");
        if (exempt != null) {
            for (String str : exempt) {
                if (name.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }
        return false;
    }
    public String callConnection(String name, String ip) {
        name = name.toLowerCase();
        String uuid = UUIDManager.get().getUUID(name);
        if (uuid == null) return "Failed to fetch your UUID";

        if (ip != null) {
            getIps().remove(name);
            getIps().put(name, ip);
        }
        InterimData interimData = PunishmentManager.get().load(name, uuid, ip);
        if (interimData == null) {
            if (getMethods().getBoolean(mi.getConfig(), "LockdownOnError", true)) {
                return "Failed to load player data!";
            } else {
                return null;
            }
        }
        Punishment pt = interimData.getBan();
        if (pt == null) {
            interimData.accept();
            return null;
        }
        return pt.getLayoutBSN();
    }
    public boolean hasPerms(Object player, String perms) {
        if (mi.hasPerms(player, perms)) {
            return true;
        }
        if (mi.getBoolean(mi.getConfig(), "EnableAllPermissionNodes", false)) {
            while (perms.contains(".")) {
                perms = perms.substring(0, perms.lastIndexOf('.'));
                if (mi.hasPerms(player, perms + ".all")) {
                    return true;
                }
            }
        }
        return false;
    }
    public void log(String msg) {
        mi.log(msg);
        debugToFile(msg);
    }
    public void debug(Object msg) {
        if (mi.getBoolean(mi.getConfig(), "Debug", false)) {
            mi.log(msg.toString());
        }
        debugToFile(msg);
    }
    public void debugException(Exception exc) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        debug(sw.toString());
    }
    public void debugSqlException(SQLException ex) {
        if (mi.getBoolean(mi.getConfig(), "Debug", false)) {
            debug("§7An error has occurred with the database, the error code is: '" + ex.getErrorCode() + "'");
            debug("§7The state of the sql is: " + ex.getSQLState());
            debug("§7Error message: " + ex.getMessage());
        }
        debugException(ex);
    }
    private void debugToFile(Object msg) {
        File debugFile = new File(mi.getDataFolder(), "logs/latest.log");
        if (!debugFile.exists()) {
            try {
                debugFile.createNewFile();
            } catch (IOException ex) {
                System.out.print("An error has occurred creating the 'latest.log' file again, check your server.");
                System.out.print("Error message" + ex.getMessage());
            }
        } else {
            logManager.checkLastLog(false);
        }
        try {
            FileUtils.writeStringToFile(debugFile, "[" + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + "] " + mi.clearFormatting(msg.toString()) + "\n", "UTF8", true);
        } catch (IOException ex) {
            System.out.print("An error has occurred writing to 'latest.log' file.");
            System.out.print(ex.getMessage());
        }
    }
}

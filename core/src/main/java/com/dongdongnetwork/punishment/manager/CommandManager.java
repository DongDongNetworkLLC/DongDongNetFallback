package com.dongdongnetwork.punishment.manager;
import com.dongdongnetwork.punishment.Universal;
import com.dongdongnetwork.punishment.utils.Command;
public class CommandManager {
    private static CommandManager instance = null;
    public static synchronized CommandManager get() {
        return instance == null ? instance = new CommandManager() : instance;
    }
    public void onCommand(final Object sender, final String cmd, final String[] args) {
        Universal.get().getMethods().runAsync(() -> {
            Command command = Command.getByName(cmd);
            if (command == null)
                return;
            String permission = command.getPermission();
            if (permission != null && !Universal.get().hasPerms(sender, permission)) {
                MessageManager.sendMessage(sender, "General.NoPerms", true);
                return;
            }
            if (!command.validateArguments(args)) {
                MessageManager.sendMessage(sender, command.getUsagePath(), true);
                return;
            }
            command.execute(sender, args);
        });
    }
}
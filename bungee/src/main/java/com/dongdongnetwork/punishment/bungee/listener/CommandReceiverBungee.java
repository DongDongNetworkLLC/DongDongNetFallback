package com.dongdongnetwork.punishment.bungee.listener;
import com.dongdongnetwork.punishment.bungee.BungeeMain;
import com.dongdongnetwork.punishment.manager.CommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
public class CommandReceiverBungee extends Command {
    public CommandReceiverBungee(String name) {
        super(name);
    }
    @Override
	public void execute(final CommandSender sender, final String[] args) {
    	if (args.length > 0) {
    		args[0] = (BungeeMain.get().getProxy().getPlayer(args[0]) != null ? BungeeMain.get().getProxy().getPlayer(args[0]).getName() : args[0]);
    	}
        CommandManager.get().onCommand(sender, this.getName(), args);
    }
}
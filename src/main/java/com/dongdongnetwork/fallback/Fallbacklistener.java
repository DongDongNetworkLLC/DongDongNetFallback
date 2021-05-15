package com.dongdongnetwork.fallback;
import java.util.Iterator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.event.EventHandler;
public class Fallbacklistener implements Listener {
    Fallbackmain plugin;
    public Fallbacklistener(Fallbackmain plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onServerKickEvent(ServerKickEvent ev) {
        ServerInfo kickedFrom = null;
        if (ev.getPlayer().getServer() != null) {
            kickedFrom = ev.getPlayer().getServer().getInfo();
        } else if (this.plugin.getProxy().getReconnectHandler() != null) {
            kickedFrom = this.plugin.getProxy().getReconnectHandler().getServer(ev.getPlayer());
        } else {
            kickedFrom = AbstractReconnectHandler.getForcedHost(ev.getPlayer().getPendingConnection());
            if (kickedFrom == null)
            {
                kickedFrom = ProxyServer.getInstance().getServerInfo(ev.getPlayer().getPendingConnection().getListener().getDefaultServer());
            }
        }
        ServerInfo kickTo = this.plugin.getProxy().getServerInfo(plugin.getConfig().getString("server"));
        if (kickedFrom != null && kickedFrom.equals(kickTo)) {
            return;
        }
        String reason = BaseComponent.toLegacyText(ev.getKickReasonComponent());
        String[] moveMsg = plugin.getConfig().getString("msg").replace("%msg%", reason).split("\n");
        Iterator<String> it = this.plugin.getConfig().getStringList("list").iterator();
        if (this.plugin.getConfig().getString("mode").equals("whitelist")) {
            while (it.hasNext()) {
                String next = it.next();
                if (reason.contains(next)) {
                    ev.setCancelled(true);
                    ev.setCancelServer(kickTo);
                    if (!(moveMsg.length == 1 && moveMsg[0].equals(""))) {
                        for (String line : moveMsg) {
                            ev.getPlayer().sendMessage(TextComponent.fromLegacyText(
                                    ChatColor.translateAlternateColorCodes('&', line)));
                        }
                    }
                    break;
                }
            }
        } else {
            while (it.hasNext()) {
                String next = it.next();
                if (reason.contains(next)) {
                    return;
                }
            }
            ev.setCancelled(true);
            ev.setCancelServer(kickTo);
            if (!(moveMsg.length == 1 && moveMsg[0].equals(""))) {
                for (String line : moveMsg) {
                    ev.getPlayer().sendMessage(TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&', line)));
                }
            }
        }
    }
}
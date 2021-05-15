package com.dongdongnetwork.punishment.bungee.listener;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.dongdongnetwork.punishment.Universal;
import com.dongdongnetwork.punishment.bungee.BungeeMain;
import com.dongdongnetwork.punishment.manager.PunishmentManager;
import com.dongdongnetwork.punishment.manager.UUIDManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
public class ConnectionListenerBungee implements Listener {
    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
    public void onConnection(LoginEvent event) {
        if(event.isCancelled())
            return;
        UUIDManager.get().supplyInternUUID(event.getConnection().getName(), event.getConnection().getUniqueId());
        event.registerIntent((BungeeMain)Universal.get().getMethods().getPlugin());
        Universal.get().getMethods().runAsync(() -> {
            String result = Universal.get().callConnection(event.getConnection().getName(), event.getConnection().getAddress().getAddress().getHostAddress());
            if (result != null) {
                if(BungeeMain.getCloudSupport() != null){
                    BungeeMain.getCloudSupport().kick(event.getConnection().getUniqueId(), result);
                }else {
                    event.setCancelled(true);
                    event.setCancelReason(result);
                }
            }
            if (Universal.isRedis()) {
                RedisBungee.getApi().sendChannelMessage("dongdongpunishment:connection", event.getConnection().getName() + "," + event.getConnection().getAddress().getAddress().getHostAddress());
            }
            event.completeIntent((BungeeMain) Universal.get().getMethods().getPlugin());
        });
    }
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        Universal.get().getMethods().runAsync(() -> {
            if (event.getPlayer() != null) {
                PunishmentManager.get().discard(event.getPlayer().getName());
            }
        });
    }
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onLogin(final PostLoginEvent event) {
        Universal.get().getMethods().scheduleAsync(() -> {
        }, 20);
    }
}
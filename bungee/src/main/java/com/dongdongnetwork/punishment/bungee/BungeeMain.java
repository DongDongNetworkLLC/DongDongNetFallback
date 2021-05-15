package com.dongdongnetwork.punishment.bungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.dongdongnetwork.punishment.Universal;
import com.dongdongnetwork.punishment.bungee.cloud.CloudSupport;
import com.dongdongnetwork.punishment.bungee.cloud.CloudSupportHandler;
import com.dongdongnetwork.punishment.bungee.listener.ChatListenerBungee;
import com.dongdongnetwork.punishment.bungee.listener.ConnectionListenerBungee;
import com.dongdongnetwork.punishment.bungee.listener.InternalListener;
import com.dongdongnetwork.punishment.bungee.listener.PubSubMessageListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
public class BungeeMain extends Plugin {
    private static BungeeMain instance;
    private static CloudSupport cloudSupport;
    public static BungeeMain get() {
        return instance;
    }
    public static CloudSupport getCloudSupport() {
        return cloudSupport;
    }
    @Override
    public void onEnable() {
        instance = this;
        Universal.get().setup(new BungeeMethods());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ConnectionListenerBungee());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListenerBungee());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new InternalListener());
        ProxyServer.getInstance().registerChannel("dongdongpunishment:main");
        cloudSupport = CloudSupportHandler.getCloudSystem();
        if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
            Universal.setRedis(true);
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PubSubMessageListener());
            RedisBungee.getApi().registerPubSubChannels("dongdongpunishment:main", "dongdongpunishment:connection");
        }
    }
    @Override
    public void onDisable() {
        Universal.get().shutdown();
    }
}
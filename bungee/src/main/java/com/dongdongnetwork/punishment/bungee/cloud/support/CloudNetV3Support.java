package com.dongdongnetwork.punishment.bungee.cloud.support;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import com.dongdongnetwork.punishment.bungee.cloud.CloudSupport;
import java.util.UUID;
public class CloudNetV3Support implements CloudSupport {
    @Override
    public void kick(UUID uniqueID, String reason) {
        CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getPlayerExecutor(uniqueID).kick(reason);
    }
}
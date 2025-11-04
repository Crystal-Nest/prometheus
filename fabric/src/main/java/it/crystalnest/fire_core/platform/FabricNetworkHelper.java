package it.crystalnest.fire_core.platform;

import it.crystalnest.fire_core.api.Fire;
import it.crystalnest.fire_core.network.packet.RegisterFirePacket;
import it.crystalnest.fire_core.network.packet.UnregisterFirePacket;
import it.crystalnest.fire_core.platform.services.NetworkHelper;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric networking helper.
 */
public final class FabricNetworkHelper implements NetworkHelper {
  @Override
  public void register() {
    PayloadTypeRegistry.playS2C().register(RegisterFirePacket.TYPE, RegisterFirePacket.CODEC);
    PayloadTypeRegistry.playS2C().register(UnregisterFirePacket.TYPE, UnregisterFirePacket.CODEC);
  }

  @Override
  public void sendToClient(ServerPlayer player, Fire fire) {
    ServerPlayNetworking.send(player, new RegisterFirePacket(fire));
  }

  @Override
  public void sendToClient(ServerPlayer player, ResourceLocation fireType) {
    ServerPlayNetworking.send(player, new UnregisterFirePacket(fireType));
  }
}

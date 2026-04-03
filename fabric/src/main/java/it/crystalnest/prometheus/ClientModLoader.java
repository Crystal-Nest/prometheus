package it.crystalnest.prometheus;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.network.handler.FirePacketHandler;
import it.crystalnest.prometheus.network.packet.RegisterFirePacket;
import it.crystalnest.prometheus.network.packet.UnregisterFirePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Soul fire'd mod client loader.
 */
@ApiStatus.Internal
public final class ClientModLoader implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    FireClientManager.registerFires(FireManager.getFires());
    BlockEntityRenderers.register(FireManager.getCustomCampfireEntityType().get(), CampfireRenderer::new);
    FireManager.getComponentListList(Fire.Component.FLAME_PARTICLE).forEach(flames -> flames.forEach(flame -> ParticleProviderRegistry.getInstance().register(flame, FlameParticle.Provider::new)));
    ClientPlayNetworking.registerGlobalReceiver(RegisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
    ClientPlayNetworking.registerGlobalReceiver(UnregisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
  }
}

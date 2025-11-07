package it.crystalnest.prometheus;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.network.handler.FirePacketHandler;
import it.crystalnest.prometheus.network.packet.RegisterFirePacket;
import it.crystalnest.prometheus.network.packet.UnregisterFirePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Soul fire'd mod client loader.
 */
@ApiStatus.Internal
public final class ClientModLoader implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    FireClientManager.registerFires(FireManager.getFires());
    BlockEntityRenderers.register(FireManager.CUSTOM_CAMPFIRE_ENTITY_TYPE.get(), CampfireRenderer::new);
    FireManager.getComponentList(Fire.Component.CAMPFIRE_BLOCK).stream().filter(CustomCampfireBlock.class::isInstance).forEach(campfire -> BlockRenderLayerMap.putBlock(campfire, ChunkSectionLayer.CUTOUT));
    FireManager.getComponentList(Fire.Component.SOURCE_BLOCK).stream().filter(CustomFireBlock.class::isInstance).forEach(source -> BlockRenderLayerMap.putBlock(source, ChunkSectionLayer.CUTOUT));
    FireManager.getComponentList(Fire.Component.TORCH_BLOCK).stream().filter(CustomTorchBlock.class::isInstance).forEach(torch -> BlockRenderLayerMap.putBlock(torch, ChunkSectionLayer.CUTOUT));
    FireManager.getComponentList(Fire.Component.WALL_TORCH_BLOCK).stream().filter(CustomWallTorchBlock.class::isInstance).forEach(torch -> BlockRenderLayerMap.putBlock(torch, ChunkSectionLayer.CUTOUT));
    FireManager.getComponentList(Fire.Component.FLAME_PARTICLE).forEach(flame -> ParticleFactoryRegistry.getInstance().register(flame, FlameParticle.Provider::new));
    ClientPlayNetworking.registerGlobalReceiver(RegisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
    ClientPlayNetworking.registerGlobalReceiver(UnregisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
  }
}

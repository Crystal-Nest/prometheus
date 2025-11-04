package it.crystalnest.fire_core;

import it.crystalnest.fire_core.api.Fire;
import it.crystalnest.fire_core.api.FireManager;
import it.crystalnest.fire_core.api.block.CustomCampfireBlock;
import it.crystalnest.fire_core.api.block.CustomFireBlock;
import it.crystalnest.fire_core.api.block.CustomTorchBlock;
import it.crystalnest.fire_core.api.block.CustomWallTorchBlock;
import it.crystalnest.fire_core.api.client.FireClientManager;
import it.crystalnest.fire_core.network.handler.FirePacketHandler;
import it.crystalnest.fire_core.network.packet.RegisterFirePacket;
import it.crystalnest.fire_core.network.packet.UnregisterFirePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.RenderType;
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
    BlockEntityRenderers.register(FireManager.CUSTOM_CAMPFIRE_ENTITY_TYPE.get(), CampfireRenderer::new);
    FireManager.getComponentList(Fire.Component.CAMPFIRE_BLOCK).stream().filter(CustomCampfireBlock.class::isInstance).forEach(campfire -> BlockRenderLayerMap.INSTANCE.putBlock(campfire, RenderType.cutout()));
    FireManager.getComponentList(Fire.Component.SOURCE_BLOCK).stream().filter(CustomFireBlock.class::isInstance).forEach(source -> BlockRenderLayerMap.INSTANCE.putBlock(source, RenderType.cutout()));
    FireManager.getComponentList(Fire.Component.TORCH_BLOCK).stream().filter(CustomTorchBlock.class::isInstance).forEach(torch -> BlockRenderLayerMap.INSTANCE.putBlock(torch, RenderType.cutout()));
    FireManager.getComponentList(Fire.Component.WALL_TORCH_BLOCK).stream().filter(CustomWallTorchBlock.class::isInstance).forEach(torch -> BlockRenderLayerMap.INSTANCE.putBlock(torch, RenderType.cutout()));
    FireManager.getComponentList(Fire.Component.FLAME_PARTICLE).forEach(flame -> ParticleFactoryRegistry.getInstance().register(flame, FlameParticle.Provider::new));
    ClientPlayNetworking.registerGlobalReceiver(RegisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
    ClientPlayNetworking.registerGlobalReceiver(UnregisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
  }
}

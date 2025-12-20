package it.crystalnest.prometheus;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomLanternBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.network.handler.FirePacketHandler;
import it.crystalnest.prometheus.network.packet.RegisterFirePacket;
import it.crystalnest.prometheus.network.packet.UnregisterFirePacket;
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
    BlockEntityRenderers.register(FireManager.getCustomCampfireEntityType().get(), CampfireRenderer::new);
    FireManager.getComponentListList(Fire.Component.CAMPFIRE_BLOCK).stream().filter(CustomCampfireBlock.class::isInstance).forEach(campfires -> campfires.forEach(campfire -> BlockRenderLayerMap.INSTANCE.putBlock(campfire, RenderType.cutout())));
    FireManager.getComponentListList(Fire.Component.SOURCE_BLOCK).stream().filter(CustomFireBlock.class::isInstance).forEach(sources -> sources.forEach(source -> BlockRenderLayerMap.INSTANCE.putBlock(source, RenderType.cutout())));
    FireManager.getComponentListList(Fire.Component.LANTERN_BLOCK).stream().filter(CustomLanternBlock.class::isInstance).forEach(lanterns -> lanterns.forEach(lantern -> BlockRenderLayerMap.INSTANCE.putBlock(lantern, RenderType.cutout())));
    FireManager.getComponentListList(Fire.Component.TORCH_BLOCK).stream().filter(CustomTorchBlock.class::isInstance).forEach(torches -> torches.forEach(torch -> BlockRenderLayerMap.INSTANCE.putBlock(torch, RenderType.cutout())));
    FireManager.getComponentListList(Fire.Component.WALL_TORCH_BLOCK).stream().filter(CustomWallTorchBlock.class::isInstance).forEach(torches -> torches.forEach(torch -> BlockRenderLayerMap.INSTANCE.putBlock(torch, RenderType.cutout())));
    FireManager.getComponentListList(Fire.Component.FLAME_PARTICLE).forEach(flames -> flames.forEach(flame -> ParticleFactoryRegistry.getInstance().register(flame, FlameParticle.Provider::new)));
    ClientPlayNetworking.registerGlobalReceiver(RegisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
    ClientPlayNetworking.registerGlobalReceiver(UnregisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
  }
}

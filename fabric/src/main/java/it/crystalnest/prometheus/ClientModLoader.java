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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;
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
    registerCutout(Fire.Component.CAMPFIRE_BLOCK, CustomCampfireBlock.class);
    registerCutout(Fire.Component.SOURCE_BLOCK, CustomFireBlock.class);
    registerCutout(Fire.Component.LANTERN_BLOCK, CustomLanternBlock.class);
    registerCutout(Fire.Component.TORCH_BLOCK, CustomTorchBlock.class);
    registerCutout(Fire.Component.WALL_TORCH_BLOCK, CustomWallTorchBlock.class);
    FireManager.getComponentListList(Fire.Component.FLAME_PARTICLE).forEach(flames -> flames.forEach(flame -> ParticleFactoryRegistry.getInstance().register(flame, FlameParticle.Provider::new)));
    ClientPlayNetworking.registerGlobalReceiver(RegisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
    ClientPlayNetworking.registerGlobalReceiver(UnregisterFirePacket.TYPE, (packet, context) -> FirePacketHandler.handle(packet));
  }

  /**
   * Registers the specified component to the {@link ChunkSectionLayer#CUTOUT} render layer.
   *
   * @param component fire component.
   * @param clazz custom class for instanceof test.
   */
  private static void registerCutout(Fire.Component<Block, Block> component, Class<?> clazz) {
    FireManager.getComponentListList(component).forEach(values -> values.stream().filter(clazz::isInstance).forEach(value -> BlockRenderLayerMap.putBlock(value, ChunkSectionLayer.CUTOUT)));
  }
}

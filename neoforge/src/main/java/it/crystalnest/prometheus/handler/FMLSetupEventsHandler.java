package it.crystalnest.prometheus.handler;

import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomLanternBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.api.client.FireClientManager;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Objects;

/**
 * Handles the registry events.
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public final class FMLSetupEventsHandler {
  private FMLSetupEventsHandler() {}

  /**
   * Handles the {@link FMLClientSetupEvent} event.
   *
   * @param event {@link FMLClientSetupEvent}.
   */
  @SubscribeEvent
  public static void handle(FMLClientSetupEvent event) {
    FireClientManager.registerFires(FireManager.getFires());
    registerCutout(Fire.Component.CAMPFIRE_BLOCK, CustomCampfireBlock.class);
    registerCutout(Fire.Component.SOURCE_BLOCK, CustomFireBlock.class);
    registerCutout(Fire.Component.LANTERN_BLOCK, CustomLanternBlock.class);
    registerCutout(Fire.Component.TORCH_BLOCK, CustomTorchBlock.class);
    registerCutout(Fire.Component.WALL_TORCH_BLOCK, CustomWallTorchBlock.class);
  }

  /**
   * Handles the {@link FMLCommonSetupEvent} event.
   *
   * @param event {@link FMLCommonSetupEvent}.
   */
  @SubscribeEvent
  public static void handle(FMLCommonSetupEvent event) {
    FireManager.getComponentListList(Fire.Component.FIRE_CHARGE_ITEM).forEach(charges -> charges.stream().filter(Objects::nonNull).forEach(DispenserBlock::registerProjectileBehavior));
  }

  /**
   * Handles the {@link RegisterParticleProvidersEvent} event.
   *
   * @param event {@link RegisterParticleProvidersEvent}.
   */
  @SubscribeEvent
  public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
    FireManager.getComponentListList(Fire.Component.FLAME_PARTICLE).forEach(flames -> flames.forEach(flame -> event.registerSpriteSet(flame, FlameParticle.Provider::new)));
  }

  /**
   * Handles the {@link EntityRenderersEvent.RegisterRenderers} event.
   *
   * @param event {@link EntityRenderersEvent.RegisterRenderers}.
   */
  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(FireManager.getCustomCampfireEntityType().get(), CampfireRenderer::new);
  }

  /**
   * Registers the specified component to the {@link ChunkSectionLayer#CUTOUT} render layer.
   *
   * @param component fire component.
   * @param clazz custom class for instanceof test.
   */
  @SuppressWarnings("deprecation")
  private static void registerCutout(Fire.Component<Block, Block> component, Class<?> clazz) {
    FireManager.getComponentListList(component).forEach(values -> values.stream().filter(clazz::isInstance).forEach(value -> ItemBlockRenderTypes.setRenderLayer(value, ChunkSectionLayer.CUTOUT)));
  }
}

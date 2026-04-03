package it.crystalnest.prometheus;

import it.crystalnest.prometheus.attachment.AttachmentRegistry;
import it.crystalnest.prometheus.handler.FireResourceReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod loader.
 */
@ApiStatus.Internal
public final class ModLoader implements ModInitializer {
  @Override
  public void onInitialize() {
    CommonModLoader.init();
    AttachmentRegistry.register();
    registerResourceLoader();
  }

  /**
   * Registers the resource loader for DDFs.
   */
  private static void registerResourceLoader() {
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> FireResourceReloadListener.handle(player));
    ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, Constants.DDFIRES), new FireResourceReloadListener());
  }
}

package it.crystalnest.prometheus;

import it.crystalnest.prometheus.handler.FabricFireResourceReloadListener;
import it.crystalnest.prometheus.handler.FireResourceReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
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
    registerResourceLoader();
  }

  /**
   * Registers the resource loader for DDFs.
   */
  private void registerResourceLoader() {
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> FireResourceReloadListener.handle(player));
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricFireResourceReloadListener());
  }
}

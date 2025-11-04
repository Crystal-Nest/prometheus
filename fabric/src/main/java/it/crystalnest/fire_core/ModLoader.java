package it.crystalnest.fire_core;

import it.crystalnest.fire_core.handler.FabricFireResourceReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
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
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(FabricFireResourceReloadListener::handle);
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricFireResourceReloadListener());
  }
}

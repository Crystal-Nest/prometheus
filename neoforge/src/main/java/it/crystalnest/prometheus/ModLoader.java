package it.crystalnest.prometheus;

import it.crystalnest.prometheus.attachment.AttachmentRegistry;
import it.crystalnest.prometheus.handler.FireResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod loader.
 */
@ApiStatus.Internal
@Mod(Constants.MOD_ID)
public final class ModLoader {
  /**
   * NeoForge mod event bus.
   */
  private static IEventBus bus;

  /**
   * Mod initialization.
   *
   * @param bus Event bus.
   */
  public ModLoader(IEventBus bus) {
    ModLoader.bus = bus;
    CommonModLoader.init();
    AttachmentRegistry.register(bus);
    registerResourceLoader();
  }

  /**
   * Returns the event {@link #bus}.
   *
   * @return the event {@link #bus}.
   */
  public static IEventBus getBus() {
    return bus;
  }

  /**
   * Registers the resource loader for DDFs.
   */
  private static void registerResourceLoader() {
    NeoForge.EVENT_BUS.addListener((AddServerReloadListenersEvent event) -> event.addListener(Identifier.fromNamespaceAndPath(Constants.MOD_ID, Constants.DDFIRES), new FireResourceReloadListener()));
    NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> FireResourceReloadListener.handle(event.getPlayer()));
  }
}

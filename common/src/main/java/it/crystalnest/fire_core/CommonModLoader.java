package it.crystalnest.fire_core;
import it.crystalnest.fire_core.api.FireManager;
import it.crystalnest.fire_core.api.enchantment.EnchantmentRegistry;
import it.crystalnest.fire_core.platform.Services;
import org.jetbrains.annotations.ApiStatus;

/**
 * Common mod loader.
 */
@ApiStatus.Internal
public final class CommonModLoader {
  private CommonModLoader() {}

  /**
   * Initialize common operations across loaders.
   */
  public static void init() {
    FireManager.load();
    Services.NETWORK.register();
    EnchantmentRegistry.register();
  }
}

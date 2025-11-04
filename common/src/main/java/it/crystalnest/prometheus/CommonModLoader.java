package it.crystalnest.prometheus;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.enchantment.EnchantmentRegistry;
import it.crystalnest.prometheus.platform.Services;
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

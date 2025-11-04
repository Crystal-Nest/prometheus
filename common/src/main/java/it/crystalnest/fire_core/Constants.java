package it.crystalnest.fire_core;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common shared constants across all loaders.
 */
@ApiStatus.Internal
public final class Constants {
  /**
   * Mod ID.
   */
  public static final String MOD_ID = "fire_core";

  /**
   * Data Driven Fires id.
   */
  public static final String DDFIRES = "ddfires";

  /**
   * Mod logger.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private Constants() {}
}

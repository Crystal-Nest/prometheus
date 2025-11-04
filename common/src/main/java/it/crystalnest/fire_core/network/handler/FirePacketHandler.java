package it.crystalnest.fire_core.network.handler;

import it.crystalnest.fire_core.api.FireManager;
import it.crystalnest.fire_core.api.client.FireClientManager;
import it.crystalnest.fire_core.network.packet.RegisterFirePacket;
import it.crystalnest.fire_core.network.packet.UnregisterFirePacket;

/**
 * Handler for fire packets.
 */
public final class FirePacketHandler {
  private FirePacketHandler() {}

  /**
   * Handles a {@link RegisterFirePacket}.
   *
   * @param packet {@link RegisterFirePacket}.
   */
  public static void handle(RegisterFirePacket packet) {
    // Fire will already be registered with FireManager if in single-player.
    if (!FireManager.isRegisteredType(packet.fire().getFireType())) {
      FireManager.registerFire(packet.fire());
    }
    FireClientManager.registerFire(packet.fire());
  }

  /**
   * Handles an {@link UnregisterFirePacket}.
   *
   * @param packet {@link UnregisterFirePacket}.
   */
  public static void handle(UnregisterFirePacket packet) {
    FireManager.unregisterFire(packet.fireType());
    FireClientManager.unregisterFire(packet.fireType());
  }
}

package it.crystalnest.prometheus.network.handler;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.network.packet.RegisterFirePacket;
import it.crystalnest.prometheus.network.packet.UnregisterFirePacket;

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

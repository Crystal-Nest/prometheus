package it.crystalnest.prometheus.network.packet;

import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.Fire;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Networking packet to unregister the specified {@link Fire}.
 *
 * @param fireType fire reference.
 */
public record UnregisterFirePacket(Identifier fireType) implements CustomPacketPayload {
  /**
   * Packet type.
   */
  public static final Type<@NotNull UnregisterFirePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "unregister_fire"));

  public static final StreamCodec<FriendlyByteBuf, UnregisterFirePacket> CODEC = StreamCodec.composite(
    Identifier.STREAM_CODEC,
    UnregisterFirePacket::fireType,
    UnregisterFirePacket::new
  );

  @NotNull
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}

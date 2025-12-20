package it.crystalnest.prometheus.attachment;

import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.FireManager;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

/**
 * Attachment type registry.
 */
@ApiStatus.Internal
public final class AttachmentRegistry {
  /**
   * Fire type entity data attachment type.
   */
  @SuppressWarnings("UnstableApiUsage")
  public static final AttachmentType<Identifier> FIRE_TYPE = net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.create(
    Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fire_type"),
    builder -> builder
      .initializer(() -> FireManager.DEFAULT_FIRE_TYPE)
      .persistent(Identifier.CODEC)
      .syncWith(Identifier.STREAM_CODEC, AttachmentSyncPredicate.all())
  );

  private AttachmentRegistry() {}

  /**
   * Called outside to load the class and register.
   */
  public static void register() {}
}

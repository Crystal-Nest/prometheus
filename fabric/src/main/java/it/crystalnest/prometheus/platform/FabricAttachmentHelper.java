package it.crystalnest.prometheus.platform;

import it.crystalnest.prometheus.attachment.AttachmentRegistry;
import it.crystalnest.prometheus.platform.services.AttachmentHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Fabric entity data attachments helper.
 */
@SuppressWarnings("UnstableApiUsage")
public final class FabricAttachmentHelper implements AttachmentHelper {
  @Override
  public ResourceLocation getFireType(Entity entity) {
    return entity.getAttachedOrCreate(AttachmentRegistry.FIRE_TYPE);
  }

  @Override
  public void setFireType(Entity entity, ResourceLocation fireType) {
    entity.setAttached(AttachmentRegistry.FIRE_TYPE, fireType);
  }
}

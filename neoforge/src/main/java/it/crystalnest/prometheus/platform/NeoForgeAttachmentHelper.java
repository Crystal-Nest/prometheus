package it.crystalnest.prometheus.platform;

import it.crystalnest.prometheus.attachment.AttachmentRegistry;
import it.crystalnest.prometheus.platform.services.AttachmentHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * NeoForge entity data attachments helper.
 */
public final class NeoForgeAttachmentHelper implements AttachmentHelper {
  @Override
  public ResourceLocation getFireType(Entity entity) {
    return entity.getData(AttachmentRegistry.FIRE_TYPE);
  }

  @Override
  public void setFireType(Entity entity, ResourceLocation fireType) {
    entity.setData(AttachmentRegistry.FIRE_TYPE, fireType);
  }
}

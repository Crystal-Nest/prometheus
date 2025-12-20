package it.crystalnest.prometheus.platform;

import it.crystalnest.prometheus.attachment.AttachmentRegistry;
import it.crystalnest.prometheus.platform.services.AttachmentHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

/**
 * NeoForge entity data attachments helper.
 */
public final class NeoForgeAttachmentHelper implements AttachmentHelper {
  @Override
  public Identifier getFireType(Entity entity) {
    return entity.getData(AttachmentRegistry.FIRE_TYPE);
  }

  @Override
  public void setFireType(Entity entity, Identifier fireType) {
    entity.setData(AttachmentRegistry.FIRE_TYPE, fireType);
  }
}

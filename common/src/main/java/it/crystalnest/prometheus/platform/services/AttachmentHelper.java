package it.crystalnest.prometheus.platform.services;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Entity data attachments helper.
 */
public interface AttachmentHelper {
  /**
   * Returns the fire type attached to the given entity.
   *
   * @param entity entity.
   * @return entity's fire type.
   */
  ResourceLocation getFireType(Entity entity);

  /**
   * Sets the fire type attached to the given entity.
   *
   * @param entity entity.
   * @param fireType fire type.
   */
  void setFireType(Entity entity, ResourceLocation fireType);
}

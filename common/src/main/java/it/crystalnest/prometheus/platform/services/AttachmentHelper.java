package it.crystalnest.prometheus.platform.services;

import net.minecraft.resources.Identifier;
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
  Identifier getFireType(Entity entity);

  /**
   * Sets the fire type attached to the given entity.
   *
   * @param entity entity.
   * @param fireType fire type.
   */
  void setFireType(Entity entity, Identifier fireType);
}

package it.crystalnest.prometheus.mixin.client;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Injects into {@link EntityRenderState} to alter Fire behavior for consistency.
 */
@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements FireTypeChanger {
  /**
   * Fire type.
   */
  @Unique
  private Identifier fireType;

  @Override
  public Identifier getFireType() {
    return fireType;
  }

  @Override
  public void setFireType(Identifier fireType) {
    this.fireType = FireManager.ensure(fireType);
  }
}

package it.crystalnest.prometheus.mixin;

import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.LanternBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Injects into {@link LanternBlock} to alter Fire behavior for consistency.
 */
@Mixin(LanternBlock.class)
public abstract class LanternBlockMixin implements FireTypeChanger {
  /**
   * Fire Type.
   */
  @Unique
  private Identifier fireType;

  @Override
  public Identifier getFireType() {
    return fireType;
  }

  @Override
  public void setFireType(Identifier fireType) {
    this.fireType = fireType;
  }
}

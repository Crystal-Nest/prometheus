package it.crystalnest.prometheus.mixin;

import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.TorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Injects into {@link TorchBlock} to alter Fire behavior for consistency.
 */
@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin implements FireTypeChanger {
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

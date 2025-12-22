package it.crystalnest.prometheus.mixin;

import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.resources.ResourceLocation;
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
  private ResourceLocation fireType;

  @Override
  public ResourceLocation getFireType() {
    return fireType;
  }

  @Override
  public void setFireType(ResourceLocation fireType) {
    this.fireType = fireType;
  }
}

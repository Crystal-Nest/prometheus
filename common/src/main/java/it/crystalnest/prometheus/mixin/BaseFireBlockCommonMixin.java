package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

/**
 * Injects into {@link BaseFireBlock} to alter Fire behavior for consistency.
 */
@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockCommonMixin implements FireTypeChanger {
  /**
   * Fire Type.
   */
  @Unique
  private Identifier fireType;

  /**
   * Modifies the return value of {@link BaseFireBlock#getState(BlockGetter, BlockPos)}.<br>
   * Returns the most appropriate fire {@link BlockState}.
   *
   * @param original original block state.
   * @param level level.
   * @param pos position.
   */
  @ModifyReturnValue(method = "getState", at = @At(value = "RETURN"))
  private static BlockState modifyGetState(BlockState original, BlockGetter level, BlockPos pos) {
    return FireManager.getComponentListList(Fire.Component.SOURCE_BLOCK).stream().flatMap(Collection::stream).filter(source -> canSurvive(source, level.getBlockState(pos.below()))).findFirst().map(Block::defaultBlockState).orElse(original);
  }

  /**
   * Wraps the call to {@link Entity#hurt(DamageSource, float)} inside the method {@link BaseFireBlock#entityInside(BlockState, Level, BlockPos, Entity, InsideBlockEffectApplier, boolean)}.<br>
   * Hurts the entity with the correct fire damage and {@link DamageSource}.
   *
   * @param instance {@link Entity} invoking (owning) the redirected method.
   * @param source original {@link DamageSource} (normal fire).
   * @param damage original damage (normal fire).
   */
  @WrapOperation(method = "lambda$entityInside$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
  private void wrapHurt(Entity instance, DamageSource source, float damage, Operation<Void> original) {
    FireManager.affect(instance, getFireType(), Fire::getInFire, (TriFunction<Entity, DamageSource, Float, Void>) original::call);
  }

  /**
   * Checks whether the given {@link Block} can burn on the given base.
   *
   * @param source fire source block.
   * @param base block base.
   * @return whether the source can burn on the base.
   */
  @Unique
  private static boolean canSurvive(Block source, BlockState base) {
    return source instanceof CustomFireBlock customFireBlock && customFireBlock.canSurvive(base);
  }

  @Override
  public Identifier getFireType() {
    return fireType;
  }

  @Override
  public void setFireType(Identifier fireType) {
    this.fireType = fireType;
  }
}

package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.QuadriFunction;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import it.crystalnest.prometheus.api.type.FireTyped;
import it.crystalnest.prometheus.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Injects into {@link Entity} to alter Fire behavior for consistency.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements FireTypeChanger {
  /**
   * Shadowed {@link Entity#level}.
   */
  @Shadow
  private Level level;

  /**
   * Shadowed {@link Entity#getRemainingFireTicks()}.
   *
   * @return the remaining ticks the entity is set to burn for.
   */
  @Shadow
  public abstract int getRemainingFireTicks();

  /**
   * Shadowed {@link Entity#fireImmune()}.
   *
   * @return whether this entity is immune to fire damage.
   */
  @Shadow
  public abstract boolean fireImmune();

  @Override
  public ResourceLocation getFireType() {
    return Services.ATTACHMENT.getFireType((Entity) (Object) this);
  }

  @Override
  public void setFireType(ResourceLocation fireType) {
    if (!this.fireImmune()) {
      Services.ATTACHMENT.setFireType((Entity) (Object) this, fireType);
    }
  }

  /**
   * Wraps the call to {@link Entity#hurtServer(ServerLevel, DamageSource, float)} inside the method {@link Entity#baseTick()}.<br>
   * Hurts the entity with the correct fire damage and {@link DamageSource}.
   *
   * @param instance owner of the wrapped method.
   * @param damageSource original {@link DamageSource} (normal fire).
   * @param damage original damage (normal fire).
   * @param original original {@link Operation} being wrapped.
   * @return the result of calling the wrapped method.
   */
  @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
  private boolean wrapHurtServer(Entity instance, ServerLevel level, DamageSource damageSource, float damage, Operation<Boolean> original) {
    return FireManager.affect(instance, ((FireTyped) instance).getFireType(), Fire::getOnFire, (QuadriFunction<Entity, ServerLevel, DamageSource, Float, Boolean>) original::call);
  }

  /**
   * Wraps the call to {@link Entity#igniteForSeconds(float)} inside the method {@link Entity#lavaIgnite()}.<br>
   * Sets the base Fire Type.
   *
   * @param instance owner of the wrapped method.
   * @param seconds seconds to set the entity on fire for.
   * @param original original {@link Operation} being wrapped.
   */
  @WrapOperation(method = "lavaIgnite", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
  private void wrapIgniteForSeconds(Entity instance, float seconds, Operation<Void> original) {
    FireManager.setOnFire(instance, seconds, FireManager.DEFAULT_FIRE_TYPE, original::call);
  }

  /**
   * Injects at the start of the method {@link Entity#setRemainingFireTicks(int)}.<br>
   * Resets the Fire Type when this entity stops burning or catches fire from a new fire source.
   *
   * @param ticks ticks this entity should burn for.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "setRemainingFireTicks", at = @At(value = "HEAD"))
  private void onSetRemainingFireTicks(int ticks, CallbackInfo ci) {
    if (!level.isClientSide() && ticks >= getRemainingFireTicks()) {
      setFireType(FireManager.DEFAULT_FIRE_TYPE);
    }
  }

  /**
   * Wraps the call to {@link Entity#setRemainingFireTicks(int)} inside the method {@link Entity#applyEffectsFromBlocks(List)}.<br>
   * If the entity is a {@link AbstractHurtingProjectile}, makes sure the fire type persists.
   *
   * @param instance owner of the wrapped method.
   * @param remainingFireTicks fire duration in ticks.
   * @param original original {@link Operation} being wrapped.
   */
  @WrapOperation(method = "applyEffectsFromBlocks(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemainingFireTicks(I)V"))
  private void wrapSetRemainingFireTicks(Entity instance, int remainingFireTicks, Operation<Void> original) {
    if (instance instanceof AbstractHurtingProjectile) {
      FireManager.setOnFire(instance, remainingFireTicks, ((FireTyped) instance).getFireType(), (entity, duration) -> original.call(entity, Math.round(duration)));
    }
  }
}

package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link SmallFireball} to alter Fire behavior for consistency.
 */
@Mixin(SmallFireball.class)
public abstract class SmallFireballMixin implements FireTyped {
  /**
   * Wraps the call to {@link Entity#igniteForSeconds(float)} inside the method {@link SmallFireball#onHitEntity(EntityHitResult)}.<br>
   * Sets the correct Fire Type to the {@link Entity} being set on fire.
   *
   * @param instance owner of the wrapped method.
   * @param seconds seconds to set the entity on fire for.
   * @param original original {@link Operation} being wrapped.
   */
  @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
  private void wrapIgniteForSeconds(Entity instance, float seconds, Operation<Void> original) {
    FireManager.setOnFire(instance, seconds, getFireType(), original::call);
  }
}

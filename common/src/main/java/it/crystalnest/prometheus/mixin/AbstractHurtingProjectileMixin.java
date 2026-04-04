package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link AbstractHurtingProjectile} to alter Fire behavior for consistency.
 */
@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileMixin implements FireTyped {
  /**
   * Wraps the call to {@link Entity#igniteForSeconds(float)} inside the method {@link AbstractHurtingProjectile#tick()}.<br>
   * Makes sure the fire type persists.
   *
   * @param instance owner of the wrapped method.
   * @param numberOfSeconds seconds to set the entity on fire for.
   * @param original original {@link Operation} being wrapped.
   */
  @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/AbstractHurtingProjectile;igniteForSeconds(F)V"))
  private void wrapIgniteForSeconds(AbstractHurtingProjectile instance, float numberOfSeconds, Operation<Void> original) {
    FireManager.setOnFire(instance, numberOfSeconds, getFireType(), original::call);
  }
}

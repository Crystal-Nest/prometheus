package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link Zombie} to alter Fire behavior for consistency.
 */
@Mixin(Zombie.class)
public abstract class ZombieMixin implements FireTyped {
  /**
   * Wraps the call to {@link Entity#igniteForSeconds(float)} inside the method {@link Zombie#doHurtTarget(ServerLevel, Entity)}.<br>
   * Sets the correct Fire Type to the {@link Entity} being set on fire.
   *
   * @param instance owner of the redirected method.
   * @param numberOfSeconds amount of seconds the entity should be set on fire for.
   * @param original original {@link Operation} being wrapped.
   */
  @WrapOperation(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
  private void wrapIgniteForSeconds(Entity instance, float numberOfSeconds, Operation<Void> original) {
    FireManager.setOnFire(instance, numberOfSeconds, getFireType(), original::call);
  }
}

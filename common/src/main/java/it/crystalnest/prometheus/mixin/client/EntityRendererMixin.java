package it.crystalnest.prometheus.mixin.client;

import it.crystalnest.prometheus.api.type.FireTypeChanger;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link EntityRenderer} to alter Fire behavior for consistency.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
  /**
   * Injects after the call to {@link Entity#displayFireAnimation()} in the method {@link EntityRenderer#extractRenderState(Entity, EntityRenderState, float)}.<br>
   * Saves the entity's fire type into the render state.
   *
   * @param entity entity to render.
   * @param state {@link EntityRenderState}.
   * @param partialTicks partial ticks.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;displayFireAnimation()Z", shift = At.Shift.AFTER))
  private void onExtractRenderState(Entity entity, EntityRenderState state, float partialTicks, CallbackInfo ci) {
    ((FireTypeChanger) state).setFireType(((FireTyped) entity).getFireType());
  }
}

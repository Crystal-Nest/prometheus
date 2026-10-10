package it.crystalnest.prometheus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link FlameFeatureRenderer} to alter Fire behavior for consistency.
 */
@Mixin(FlameFeatureRenderer.class)
public abstract class FlameFeatureRendererMixin {
  /**
   * Assigns each entity's fire sprites before its flame geometry is prepared.
   *
   * @param instance renderer owning the wrapped method.
   * @param submit entity flame submission.
   * @param buffer vertex buffer.
   * @param sprite0 original first fire sprite.
   * @param sprite1 original second fire sprite.
   * @param original the {@link Operation} for the modified method.
   */
  @WrapOperation(method = "buildGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer;prepare(Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer$Submit;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
  private void wrapPrepare(FlameFeatureRenderer instance, FlameFeatureRenderer.Submit submit, VertexConsumer buffer, TextureAtlasSprite sprite0, TextureAtlasSprite sprite1, Operation<Void> original) {
    Identifier fireType = ((FireTyped) submit.entityRenderState()).getFireType();
    if (FireManager.isRegisteredType(fireType)) {
      sprite0 = FireClientManager.getSprite0(fireType);
      sprite1 = FireClientManager.getSprite1(fireType);
    }
    original.call(instance, submit, buffer, sprite0, sprite1);
  }
}

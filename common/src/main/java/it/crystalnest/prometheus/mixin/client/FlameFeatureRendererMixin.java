package it.crystalnest.prometheus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Injects into {@link FlameFeatureRenderer} to alter Fire behavior for consistency.
 */
@Mixin(FlameFeatureRenderer.class)
public abstract class FlameFeatureRendererMixin {
  /**
   * Wraps the first call to {@link AtlasManager#get(Material)} in the method {@link FlameFeatureRenderer#renderFlame(PoseStack.Pose, MultiBufferSource, EntityRenderState, Quaternionf, AtlasManager)}.<br>
   * Assigns the correct sprite (0) for the fire type the entity is burning from.
   *
   * @param instance {@link AtlasManager} instance owning the wrapped method.
   * @param material original material.
   * @param original the {@link Operation} for the modified method.
   * @param pose pose.
   * @param bufferSource buffer source.
   * @param renderState {@link EntityRenderState} of the entity that's burning.
   * @param rotation rotation matrix.
   * @param atlasManager {@link AtlasManager}.
   * @return {@link TextureAtlasSprite} to assign.
   */
  @WrapOperation(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/AtlasManager;get(Lnet/minecraft/client/resources/model/Material;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", ordinal = 0))
  private TextureAtlasSprite wrapSprite0(AtlasManager instance, Material material, Operation<TextureAtlasSprite> original, PoseStack.Pose pose, MultiBufferSource bufferSource, EntityRenderState renderState, Quaternionf rotation, AtlasManager atlasManager) {
    Identifier fireType = ((FireTyped) renderState).getFireType();
    if (FireManager.isRegisteredType(fireType)) {
      return FireClientManager.getSprite0(fireType);
    }
    return original.call(instance, material);
  }

  /**
   * Wraps the second call to {@link AtlasManager#get(Material)} in the method {@link FlameFeatureRenderer#renderFlame(PoseStack.Pose, MultiBufferSource, EntityRenderState, Quaternionf, AtlasManager)}.<br>
   * Assigns the correct sprite (1) for the fire type the entity is burning from.
   *
   * @param instance {@link AtlasManager} instance owning the wrapped method.
   * @param material original material.
   * @param original the {@link Operation} for the modified method.
   * @param pose pose.
   * @param bufferSource buffer source.
   * @param renderState {@link EntityRenderState} of the entity that's burning.
   * @param rotation rotation matrix.
   * @param atlasManager {@link AtlasManager}.
   * @return {@link TextureAtlasSprite} to assign.
   */
  @WrapOperation(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/AtlasManager;get(Lnet/minecraft/client/resources/model/Material;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;", ordinal = 1))
  private TextureAtlasSprite wrapSprite1(AtlasManager instance, Material material, Operation<TextureAtlasSprite> original, PoseStack.Pose pose, MultiBufferSource bufferSource, EntityRenderState renderState, Quaternionf rotation, AtlasManager atlasManager) {
    Identifier fireType = ((FireTyped) renderState).getFireType();
    if (FireManager.isRegisteredType(fireType)) {
      return FireClientManager.getSprite1(fireType);
    }
    return original.call(instance, material);
  }
}

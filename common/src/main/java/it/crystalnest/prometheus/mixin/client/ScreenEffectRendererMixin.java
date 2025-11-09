package it.crystalnest.prometheus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

/**
 * Injects into {@link ScreenEffectRenderer} to alter Fire behavior for consistency.
 */
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
  /**
   * Wraps the call to {@link MaterialSet#get(Material)} in the method {@link ScreenEffectRenderer#renderFire(PoseStack, MultiBufferSource, TextureAtlasSprite)}.<br>
   * Assigns the correct sprite for the Fire Type the player is burning from.
   *
   * @param instance {@link MaterialSet} instance owning the wrapped method.
   * @param material original material.
   * @param original the {@link Operation} for the wrapped method.
   * @return {@link TextureAtlasSprite} to assign.
   */
  @WrapOperation(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/MaterialSet;get(Lnet/minecraft/client/resources/model/Material;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
  private TextureAtlasSprite wrapSprite(MaterialSet instance, Material material, Operation<TextureAtlasSprite> original) {
    ResourceLocation fireType = ((FireTyped) Objects.requireNonNull(Minecraft.getInstance().player)).getFireType();
    if (FireManager.isRegisteredType(fireType)) {
      return FireClientManager.getSprite1(fireType);
    }
    return original.call(instance, material);
  }
}

package it.crystalnest.prometheus.api.block;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Custom wall torch block.
 */
public class CustomWallTorchBlock extends WallTorchBlock implements FireTyped {
  /**
   * Fire type.
   */
  private final ResourceLocation fireType;

  /**
   * Particle type.
   */
  private final Supplier<SimpleParticleType> type;

  /**
   * @param fireType fire type.
   * @param type particle type.
   * @param properties block properties.
   */
  public CustomWallTorchBlock(ResourceLocation fireType, Supplier<SimpleParticleType> type, Properties properties) {
    this(fireType, type, true, properties);
  }

  /**
   * @param fireType fire type.
   * @param type particle type.
   * @param addDefaultProperties whether to add default block properties.
   * @param properties block properties.
   */
  public CustomWallTorchBlock(ResourceLocation fireType, Supplier<SimpleParticleType> type, boolean addDefaultProperties, Properties properties) {
    // noinspection DataFlowIssue
    super(
      null,
      (addDefaultProperties ? addDefaultProperties(properties) : properties)
        .lightLevel(FireManager.lightLevel(fireType))
        .overrideLootTable(getTorchBlock(fireType).getLootTable())
        .overrideDescription(getTorchBlock(fireType).getDescriptionId())
    );
    this.fireType = fireType;
    this.type = type;
  }

  /**
   * @param fireType fire type.
   * @param type particle type.
   * @param addDefaultProperties whether to add default block properties.
   * @param properties block properties.
   */
  public CustomWallTorchBlock(ResourceLocation fireType, Supplier<SimpleParticleType> type, String torchId, boolean addDefaultProperties, Properties properties) {
    // noinspection DataFlowIssue
    super(
      null,
      (addDefaultProperties ? addDefaultProperties(properties) : properties)
        .lightLevel(FireManager.lightLevel(fireType))
        .overrideLootTable(getTorchBlock(fireType, torchId).getLootTable())
        .overrideDescription(getTorchBlock(fireType, torchId).getDescriptionId())
    );
    this.fireType = fireType;
    this.type = type;
  }

  /**
   * Adds the default properties.
   *
   * @param properties initial properties.
   * @return combination of initial and default properties.
   */
  private static Properties addDefaultProperties(Properties properties) {
    return properties.noCollision().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY);
  }

  /**
   * Returns the required {@link Fire.Component#TORCH_BLOCK}.<br>
   * Use {@link #getTorchBlock(ResourceLocation, String)} to select a specific torch out of the (possibly) many ones associated with this fire.
   *
   * @param fireType fire type.
   * @return related {@link Fire.Component#TORCH_BLOCK}.
   */
  public static Block getTorchBlock(ResourceLocation fireType) {
    return FireManager.getRequiredComponent(fireType, Fire.Component.TORCH_BLOCK);
  }

  /**
   * Returns the required {@link Fire.Component#TORCH_BLOCK}.
   *
   * @param fireType fire type.
   * @param torchId block ID of the corresponding torch block.
   * @return related {@link Fire.Component#TORCH_BLOCK}.
   */
  public static Block getTorchBlock(ResourceLocation fireType, String torchId) {
    return FireManager.getRequiredComponent(fireType, Fire.Component.TORCH_BLOCK, torchId);
  }

  @Override
  public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
    flameParticle = type.get();
    super.animateTick(state, level, pos, random);
  }

  @Override
  public ResourceLocation getFireType() {
    return fireType;
  }
}

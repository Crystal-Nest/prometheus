package it.crystalnest.prometheus.api.block;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Custom torch block.
 */
public class CustomTorchBlock extends TorchBlock implements FireTyped {
  /**
   * Fire type.
   */
  private final Identifier fireType;

  /**
   * Particle type.
   */
  private final Supplier<SimpleParticleType> type;

  /**
   * @param fireType fire type.
   * @param type particle type.
   * @param properties block properties.
   */
  public CustomTorchBlock(Identifier fireType, Supplier<SimpleParticleType> type, Properties properties) {
    this(fireType, type, true, properties);
  }

  /**
   * @param fireType fire type.
   * @param type particle type.
   * @param addDefaultProperties whether to add default block properties.
   * @param properties block properties.
   */
  public CustomTorchBlock(Identifier fireType, Supplier<SimpleParticleType> type, boolean addDefaultProperties, Properties properties) {
    // noinspection DataFlowIssue
    super(null, (addDefaultProperties ? addDefaultProperties(properties) : properties).lightLevel(FireManager.lightLevel(fireType)));
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

  @Override
  public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
    flameParticle = type.get();
    super.animateTick(state, level, pos, random);
  }

  @Override
  public Identifier getFireType() {
    return fireType;
  }
}

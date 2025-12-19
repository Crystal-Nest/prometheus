package it.crystalnest.prometheus.api.block.entity;

import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Custom campfire block entity that allows both automatic use of {@link CampfireBlockEntity} for custom registered campfires and an easier way to create other, more specific, custom campfire block entities.
 */
public class CustomCampfireBlockEntity extends CampfireBlockEntity {
  /**
   * @param pos campfire position.
   * @param state campfire block state.
   */
  public CustomCampfireBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @NotNull
  @Override
  public BlockEntityType<?> getType() {
    return FireManager.getCustomCampfireEntityType().get();
  }
}

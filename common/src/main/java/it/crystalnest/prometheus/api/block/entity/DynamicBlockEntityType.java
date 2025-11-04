package it.crystalnest.prometheus.api.block.entity;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

/**
 * Block entity type that allows a dynamic list of valid blocks.
 *
 * @param <T> block entity.
 */
public class DynamicBlockEntityType<T extends CustomCampfireBlockEntity> extends BlockEntityType<T> {
  /**
   * @param supplier {@link BlockEntitySupplier} for the custom campfire block entity.
   */
  public DynamicBlockEntityType(BlockEntitySupplier<? extends T> supplier) {
    //noinspection DataFlowIssue
    super(supplier, Set.of(), null);
  }

  @Override
  public boolean isValid(BlockState state) {
    return FireManager.getComponentList(Fire.Component.CAMPFIRE_BLOCK).stream().filter(CustomCampfireBlock.class::isInstance).toList().contains(state.getBlock());
  }
}

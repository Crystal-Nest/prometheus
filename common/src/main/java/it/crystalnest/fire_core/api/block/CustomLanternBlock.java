package it.crystalnest.fire_core.api.block;

import it.crystalnest.fire_core.api.Fire;
import it.crystalnest.fire_core.api.FireManager;
import it.crystalnest.fire_core.api.type.FireTyped;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Custom lantern block.
 */
public class CustomLanternBlock extends LanternBlock implements FireTyped {
  /**
   * Fire type.
   */
  private final ResourceLocation fireType;

  /**
   * @param fireType fire type.
   */
  public CustomLanternBlock(ResourceLocation fireType) {
    this(fireType, Properties.of().mapColor(MapColor.METAL).forceSolidOn().requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).noOcclusion().pushReaction(PushReaction.DESTROY));
  }

  /**
   * @param fireType fire type.
   * @param properties block properties.
   */
  public CustomLanternBlock(ResourceLocation fireType, Properties properties) {
    super(properties.lightLevel(state -> FireManager.getProperty(fireType, Fire::getLight)));
    this.fireType = fireType;
  }

  @Override
  public ResourceLocation getFireType() {
    return fireType;
  }
}

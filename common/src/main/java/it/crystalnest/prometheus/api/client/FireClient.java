package it.crystalnest.prometheus.api.client;

import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

/**
 * Fire, client side only.
 */
public final class FireClient {
  /**
   * {@link Identifier} to uniquely identify this Fire.
   */
  private final Identifier fireType;

  /**
   * Fire {@link SpriteId} for the sprite 0.<br>
   * Used only in rendering the Fire of an entity.
   */
  private final SpriteId spriteId0;

  /**
   * Fire {@link SpriteId} for the sprite 1.<br>
   * Used both for rendering the Fire of an entity and the player overlay.
   */
  private final SpriteId spriteId1;

  /**
   * @param fireType {@link #fireType}.
   */
  @SuppressWarnings("deprecation")
  FireClient(Identifier fireType) {
    this.fireType = fireType;
    String modId = fireType.getNamespace();
    String fireId = fireType.getPath();
    String joiner = FireManager.DEFAULT_FIRE_TYPE.equals(fireType) ? "" : "_";
    this.spriteId0 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(modId, "block/" + fireId + joiner + "fire_0"));
    this.spriteId1 = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(modId, "block/" + fireId + joiner + "fire_1"));
  }

  /**
   * Returns this {@link #fireType}.
   *
   * @return this {@link #fireType}.
   */
  public Identifier getFireType() {
    return fireType;
  }

  /**
   * Returns this {@link #spriteId0}.
   *
   * @return this {@link #spriteId0}.
   */
  public SpriteId getSpriteId0() {
    return spriteId0;
  }

  /**
   * Returns this {@link #spriteId1}.
   *
   * @return this {@link #spriteId1}.
   */
  public SpriteId getSpriteId1() {
    return spriteId1;
  }

  @Override
  public String toString() {
    return "FireClient [fireType=" + fireType + ", material0=" + spriteId0 + ", material1=" + spriteId1 + "]";
  }
}

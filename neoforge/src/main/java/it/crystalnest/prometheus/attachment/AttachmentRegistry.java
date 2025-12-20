package it.crystalnest.prometheus.attachment;

import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.FireManager;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Attachment type registry.
 */
@ApiStatus.Internal
public final class AttachmentRegistry {
  /**
   * Attachment type register.
   */
  private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Constants.MOD_ID);

  /**
   * Fire type entity data attachment type.
   */
  public static final Supplier<AttachmentType<Identifier>> FIRE_TYPE = ATTACHMENT_TYPES.register(
    "fire_type",
    () -> AttachmentType
      .builder(() -> FireManager.DEFAULT_FIRE_TYPE)
      .serialize(Identifier.CODEC.fieldOf("fire_type"))
      .sync(Identifier.STREAM_CODEC)
      .build()
  );

  private AttachmentRegistry() {}

  /**
   * Called outside to load the class and register.
   *
   * @param bus event bus.
   */
  public static void register(IEventBus bus) {
    ATTACHMENT_TYPES.register(bus);
  }
}

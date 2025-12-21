package it.crystalnest.prometheus.api;

import it.crystalnest.cobweb.api.pack.dynamic.DynamicDataPack;
import it.crystalnest.cobweb.api.pack.dynamic.DynamicTagBuilder;
import it.crystalnest.cobweb.api.registry.CobwebEntry;
import it.crystalnest.cobweb.api.registry.CobwebRegister;
import it.crystalnest.cobweb.api.registry.CobwebRegistry;
import it.crystalnest.cobweb.platform.model.Platform;
import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.QuadriFunction;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomLanternBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.platform.Services;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility to register game-objects related to Fire Components.
 */
public final class FireRegistrar {
  /**
   * Dynamic data pack to automatically add {@link BlockTags#FIRE} to fire source blocks.
   */
  private static final DynamicDataPack FIRE_SOURCE_TAGS = DynamicDataPack.named(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "fire_source_tags"));

  /**
   * Dynamic data pack to automatically add {@link BlockTags#CAMPFIRES} to campfire blocks.
   */
  private static final DynamicDataPack CAMPFIRE_TAGS = DynamicDataPack.named(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "campfire_tags"));

  /**
   * Dynamic data pack to automatically add {@link ItemTags#CREEPER_IGNITERS} to fire charge items.
   */
  private static final DynamicDataPack FIRE_CHARGE_TAGS = DynamicDataPack.named(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "fire_charge_tags"));

  /**
   * Whether this class has already been loaded.
   */
  private static boolean LOADED = false;

  /**
   * Loads this class.<br>
   * <strong>Internal usage, do not call elsewhere!</strong>
   *
   * @throws IllegalStateException if called more than once.
   */
  @ApiStatus.Internal
  static synchronized void load() {
    if (LOADED) {
      throw new IllegalStateException("FireManager was already loaded");
    }
    LOADED = true;
    FIRE_SOURCE_TAGS.register();
    CAMPFIRE_TAGS.register();
    FIRE_CHARGE_TAGS.register();
  }

  private FireRegistrar() {}

  /**
   * Registers the default values for every specified fire component.<p>
   * <b>Note</b>:
   * <ul>
   *   <li>Order is important: first particle, then blocks, than items.</li>
   *   <li>Do not specify the same component more than once.</li>
   *   <li>Specify only one of {@link Fire.Component#TORCH_BLOCK} or {@link Fire.Component#WALL_TORCH_BLOCK}: they share the same default registration method.</li>
   * </ul>
   *
   * @param fireType fire type.
   * @param components ordered list of components.
   */
  public static void registerDefaultFireComponents(ResourceLocation fireType, Fire.Component<?, ?>... components) {
    for (Fire.Component<?, ?> component : components) {
      component.register(fireType);
    }
  }

  /**
   * Registers the source block for the specified fire.<p>
   *
   * Use the {@link #registerFireSource(ResourceLocation, String, MapColor, BiFunction)} overload if you need to register more than one fire source block for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the source block.
   */
  public static CobwebEntry<CustomFireBlock> registerFireSource(ResourceLocation fireType) {
    return registerFireSource(fireType, MapColor.FIRE, CustomFireBlock::new);
  }

  /**
   * Registers the source block for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerFireSource(ResourceLocation, String, MapColor, BiFunction)} overload if you need to register more than one fire source block for your fire.
   *
   * @param fireType fire type.
   * @param color light color.
   * @param constructor {@link CustomFireBlock} constructor.
   * @param <T> source block type.
   * @return {@link CobwebEntry} for the source block.
   */
  public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(ResourceLocation fireType, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, BlockBehaviour.Properties, T> constructor) {
    return registerFireSource(fireType, TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(fireMod(fireType), fireType.getPath() + "_fire_base_blocks")), color, constructor);
  }

  /**
   * Registers the source block for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerFireSource(ResourceLocation, String, TagKey, MapColor, TriFunction)} overload if you need to register more than one fire source block for your fire.
   *
   * @param fireType fire type.
   * @param base {@link CustomFireBlock#base}.
   * @param color light color.
   * @param constructor {@link CustomFireBlock} constructor.
   * @param <T> source block type.
   * @return {@link CobwebEntry} for the source block.
   */
  public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(ResourceLocation fireType, TagKey<Block> base, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, BlockBehaviour.Properties, T> constructor) {
    return registerFireSource(fireType, color, (type, properties) -> constructor.apply(type, base, properties));
  }

  /**
   * Registers the source block for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerFireSource(ResourceLocation, String, MapColor, BiFunction)} overload if you need to register more than one fire source block for your fire.
   *
   * @param fireType fire type.
   * @param color light color.
   * @param constructor {@link CustomFireBlock} constructor.
   * @param <T> source block type.
   * @return {@link CobwebEntry} for the source block.
   */
  public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(ResourceLocation fireType, MapColor color, BiFunction<ResourceLocation, BlockBehaviour.Properties, T> constructor) {
    return registerFireSource(fireType, FireManager.getComponentPath(fireType, Fire.Component.SOURCE_BLOCK), color, constructor);
  }

  /**
   * Registers the source block for the specified fire from the given constructor.<p>
   *
   * Use one of the other {@code registerFireSource} overloads if you intend to register just one fire source block for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code blockId} parameter of this overload to specify the ID of the fire source.
   *
   * @param fireType fire type.
   * @param blockId block ID.
   * @param base {@link CustomFireBlock#base}.
   * @param color light color.
   * @param constructor {@link CustomFireBlock} constructor.
   * @param <T> source block type.
   * @return {@link CobwebEntry} for the source block.
   */
  public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(ResourceLocation fireType, String blockId, TagKey<Block> base, MapColor color, TriFunction<ResourceLocation, TagKey<Block>, BlockBehaviour.Properties, T> constructor) {
    return registerFireSource(fireType, blockId, color, (type, properties) -> constructor.apply(type, base, properties));
  }

  /**
   * Registers the source block for the specified fire from the given constructor.<p>
   *
   * Use one of the other {@code registerFireSource} overloads if you intend to register just one fire source block for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code blockId} parameter of this overload to specify the ID of the fire source.
   *
   * @param fireType fire type.
   * @param blockId block ID.
   * @param color light color.
   * @param constructor {@link CustomFireBlock} constructor.
   * @param <T> source block type.
   * @return {@link CobwebEntry} for the source block.
   */
  public static <T extends CustomFireBlock> CobwebEntry<T> registerFireSource(ResourceLocation fireType, String blockId, MapColor color, BiFunction<ResourceLocation, BlockBehaviour.Properties, T> constructor) {
    CobwebEntry<T> source = CobwebRegistry.ofBlocks(fireMod(fireType)).register(blockId, () -> constructor.apply(fireType, BlockBehaviour.Properties.of().mapColor(color)));
    FIRE_SOURCE_TAGS.add(() -> DynamicTagBuilder.of(Registries.BLOCK, BlockTags.FIRE).addElement(source.get()));
    return source;
  }

  /**
   * Registers the campfire block for the specified fire.<p>
   *
   * Use the {@link #registerCampfire(ResourceLocation, String, boolean, TriFunction)} overload if you need to register more than one campfire for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the campfire block.
   */
  public static CobwebEntry<CustomCampfireBlock> registerCampfire(ResourceLocation fireType) {
    return registerCampfire(fireType, false, CustomCampfireBlock::new);
  }

  /**
   * Registers the campfire block for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerCampfire(ResourceLocation, String, boolean, TriFunction)} overload if you need to register more than one campfire for your fire.
   *
   * @param fireType fire type.
   * @param spawnParticles whether to spawn crackling particles.
   * @param constructor {@link CustomCampfireBlock} constructor.
   * @param <T> campfire block type.
   * @return {@link CobwebEntry} for the campfire block.
   */
  public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(ResourceLocation fireType, boolean spawnParticles, TriFunction<ResourceLocation, Boolean, BlockBehaviour.Properties, T> constructor) {
    return registerCampfire(fireType, (type, properties) -> constructor.apply(type, spawnParticles, properties));
  }

  /**
   * Registers the campfire block for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerCampfire(ResourceLocation, String, BiFunction)} overload if you need to register more than one campfire for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link CustomCampfireBlock} constructor.
   * @param <T> campfire block type.
   * @return {@link CobwebEntry} for the campfire block.
   */
  public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(ResourceLocation fireType, BiFunction<ResourceLocation, BlockBehaviour.Properties, T> constructor) {
    return registerCampfire(fireType, FireManager.getComponentPath(fireType, Fire.Component.CAMPFIRE_BLOCK), constructor);
  }

  /**
   * Registers the campfire block for the specified fire from the given constructor.<p>
   *
   * Use one of the other {@code registerCampfire} overloads if you intend to register just one campfire for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code blockId} parameter of this overload to specify the ID of the campfire.
   *
   * @param fireType fire type.
   * @param blockId block ID.
   * @param spawnParticles whether to spawn crackling particles.
   * @param constructor {@link CustomCampfireBlock} constructor.
   * @param <T> campfire block type.
   * @return {@link CobwebEntry} for the campfire block.
   */
  public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(ResourceLocation fireType, String blockId, boolean spawnParticles, TriFunction<ResourceLocation, Boolean, BlockBehaviour.Properties, T> constructor) {
    return registerCampfire(fireType, blockId, (type, properties) -> constructor.apply(type, spawnParticles, properties));
  }

  /**
   * Registers the campfire block for the specified fire from the given constructor.<p>
   *
   * Use one of the other {@code registerCampfire} overloads if you intend to register just one campfire for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code blockId} parameter of this overload to specify the ID of the campfire.
   *
   * @param fireType fire type.
   * @param blockId block ID.
   * @param constructor {@link CustomCampfireBlock} constructor.
   * @param <T> campfire block type.
   * @return {@link CobwebEntry} for the campfire block.
   */
  public static <T extends CustomCampfireBlock> CobwebEntry<T> registerCampfire(ResourceLocation fireType, String blockId, BiFunction<ResourceLocation, BlockBehaviour.Properties, T> constructor) {
    CobwebEntry<T> campfire = CobwebRegistry.ofBlocks(fireMod(fireType)).register(blockId, () -> constructor.apply(fireType, BlockBehaviour.Properties.of()));
    CAMPFIRE_TAGS.add(() -> DynamicTagBuilder.of(Registries.BLOCK, BlockTags.CAMPFIRES).addElement(campfire.get()));
    return campfire;
  }

  /**
   * Registers the campfire item for the specified fire.<br>
   * Must be called <strong>after</strong> {@code registerCampfire}.<p>
   *
   * Use the {@link #registerCampfireItem(ResourceLocation, String, String, BiFunction, Item.Properties)} overload if you need to register more than one campfire for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the campfire item.
   */
  public static CobwebEntry<BlockItem> registerCampfireItem(ResourceLocation fireType) {
    return registerCampfireItem(fireType, BlockItem::new);
  }

  /**
   * Registers the campfire item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@code registerCampfire}.<p>
   *
   * Use the {@link #registerCampfireItem(ResourceLocation, String, String, BiFunction, Item.Properties)} overload if you need to register more than one campfire for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link BlockItem} constructor.
   * @param <T> item type.
   * @return {@link CobwebEntry} for the campfire item.
   */
  public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(ResourceLocation fireType, BiFunction<Block, Item.Properties, T> constructor) {
    return registerCampfireItem(fireType, constructor, new Item.Properties());
  }

  /**
   * Registers the campfire item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@code registerCampfire}.<p>
   *
   * Use the {@link #registerCampfireItem(ResourceLocation, String, String, BiFunction, Item.Properties)} overload if you need to register more than one campfire for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link BlockItem} constructor.
   * @param properties item properties.
   * @param <T> item type.
   * @return {@link CobwebEntry} for the campfire item.
   */
  public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(ResourceLocation fireType, BiFunction<Block, Item.Properties, T> constructor, Item.Properties properties) {
    return registerCampfireItem(fireType, FireManager.getComponentPath(fireType, Fire.Component.CAMPFIRE_ITEM), FireManager.getComponentPath(fireType, Fire.Component.CAMPFIRE_BLOCK), constructor, properties);
  }

  /**
   * Registers the campfire item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@code registerCampfire}.<p>
   *
   * Use one of the other {@code registerCampfireItem} overloads if you intend to register just one campfire for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code itemId} and {@code blockId} parameters of this overload to specify the ID of the campfire.
   *
   * @param fireType fire type.
   * @param itemId item ID.
   * @param blockId block ID of the corresponding campfire block.
   * @param constructor {@link BlockItem} constructor.
   * @param properties item properties.
   * @param <T> item type.
   * @return {@link CobwebEntry} for the campfire item.
   */
  public static <T extends BlockItem> CobwebEntry<T> registerCampfireItem(ResourceLocation fireType, String itemId, String blockId, BiFunction<Block, Item.Properties, T> constructor, Item.Properties properties) {
    return CobwebRegistry.ofItems(fireMod(fireType)).register(itemId, () -> constructor.apply(FireManager.getRequiredComponent(fireType, Fire.Component.CAMPFIRE_BLOCK, blockId), properties.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)));
  }

  /**
   * Registers the particle type for the specified fire.<p>
   *
   * Use the {@link #registerParticle(ResourceLocation, String, Supplier)} overload if you need to register more than one flame particle for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the particle type.
   */
  public static CobwebEntry<SimpleParticleType> registerParticle(ResourceLocation fireType) {
    return registerParticle(fireType, () -> new SimpleParticleType(false));
  }

  /**
   * Registers the particle type for the specified fire from the given supplier.<br>
   * Make sure your particle implements {@link ParticleOptions} if you are going to register a custom torch too.<br>
   * If it's not a subclass of {@link SimpleParticleType}, you also need to register a {@link ParticleProvider} for your particle.<p>
   *
   * Use the {@link #registerParticle(ResourceLocation, String, Supplier)} overload if you need to register more than one flame particle for your fire.
   *
   * @param fireType fire type.
   * @param supplier {@link SimpleParticleType} supplier.
   * @param <T> particle type.
   * @return {@link CobwebEntry} for the particle type.
   */
  public static <T extends SimpleParticleType> CobwebEntry<T> registerParticle(ResourceLocation fireType, Supplier<T> supplier) {
    return registerParticle(fireType, FireManager.getComponentPath(fireType, Fire.Component.FLAME_PARTICLE), supplier);
  }

  /**
   * Registers the particle type for the specified fire from the given supplier.<br>
   * Make sure your particle implements {@link ParticleOptions} if you are going to register a custom torch too.<br>
   * If it's not a subclass of {@link SimpleParticleType}, you also need to register a {@link ParticleProvider} for your particle.<p>
   *
   * Use of the other {@code registerParticle} overloads if you intend to register just one flame particle for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code particleId} parameter of this overload to specify the ID of the flame particle.
   *
   * @param fireType fire type.
   * @param supplier {@link SimpleParticleType} supplier.
   * @param <T> particle type.
   * @return {@link CobwebEntry} for the particle type.
   */
  public static <T extends SimpleParticleType> CobwebEntry<T> registerParticle(ResourceLocation fireType, String particleId, Supplier<T> supplier) {
    return CobwebRegistry.of(Registries.PARTICLE_TYPE, fireMod(fireType)).register(particleId, supplier);
  }

  /**
   * Registers the pair of torch and wall torch blocks for the specified fire.<br>
   * Must be called <strong>after</strong> {@link #registerParticle}.<br>
   * Make sure your registered particle implements {@link ParticleOptions}.<p>
   *
   * Use the {@link #registerTorch(ResourceLocation, String, String, String, TriFunction, QuadriFunction)} overload if you need to register more than one torch for your fire.
   *
   * @param fireType fire type.
   * @return pair of {@link CobwebEntry}s for the torch and wall torch blocks.
   */
  public static Pair<CobwebEntry<CustomTorchBlock>, CobwebEntry<CustomWallTorchBlock>> registerTorch(ResourceLocation fireType) {
    return registerTorch(fireType, CustomTorchBlock::new, CustomWallTorchBlock::new);
  }

  /**
   * Registers the pair of torch and wall torch blocks for the specified fire from the given constructors.<br>
   * Must be called <strong>after</strong> {@link #registerParticle}.<br>
   * Make sure your registered particle implements {@link ParticleOptions}.<p>
   *
   * Use the {@link #registerTorch(ResourceLocation, String, String, String, TriFunction, QuadriFunction)} overload if you need to register more than one torch for your fire.
   *
   * @param fireType fire type.
   * @param torchSupplier {@link CustomTorchBlock} constructor.
   * @param wallTorchSupplier {@link CustomWallTorchBlock} constructor.
   * @param <T> torch block type.
   * @param <W> wall torch block type.
   * @return pair of {@link CobwebEntry}s for torch and wall torch blocks.
   */
  public static <T extends CustomTorchBlock, W extends CustomWallTorchBlock> Pair<CobwebEntry<T>, CobwebEntry<W>> registerTorch(
    ResourceLocation fireType,
    TriFunction<ResourceLocation, Supplier<SimpleParticleType>, BlockBehaviour.Properties, T> torchSupplier,
    TriFunction<ResourceLocation, Supplier<SimpleParticleType>, BlockBehaviour.Properties, W> wallTorchSupplier
  ) {
    return registerTorch(
      fireType,
      FireManager.getComponentPath(fireType, Fire.Component.TORCH_BLOCK),
      FireManager.getComponentPath(fireType, Fire.Component.WALL_TORCH_BLOCK),
      FireManager.getComponentPath(fireType, Fire.Component.FLAME_PARTICLE),
      torchSupplier,
      (res, sup, id, props) -> wallTorchSupplier.apply(res, sup, props)
    );
  }

  /**
   * Registers the pair of torch and wall torch blocks for the specified fire from the given constructors.<br>
   * Must be called <strong>after</strong> {@link #registerParticle}.<br>
   * Make sure your registered particle implements {@link ParticleOptions}.<p>
   *
   * Use of the other {@code registerTorch} overloads if you intend to register just one torch for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code particleId}, {@code particleId}, and {@code particleId} parameters of this overload to specify the IDs of the torch and flame particle.
   *
   * @param fireType fire type.
   * @param torchId torch block ID.
   * @param wallTorchId wall torch block ID.
   * @param particleId particle ID of the corresponding flame particle.
   * @param torchSupplier {@link CustomTorchBlock} constructor.
   * @param wallTorchSupplier {@link CustomWallTorchBlock} constructor.
   * @param <T> torch block type.
   * @param <W> wall torch block type.
   * @return pair of {@link CobwebEntry}s for torch and wall torch blocks.
   */
  public static <T extends CustomTorchBlock, W extends CustomWallTorchBlock> Pair<CobwebEntry<T>, CobwebEntry<W>> registerTorch(
    ResourceLocation fireType,
    String torchId,
    String wallTorchId,
    String particleId,
    TriFunction<ResourceLocation, Supplier<SimpleParticleType>, BlockBehaviour.Properties, T> torchSupplier,
    QuadriFunction<ResourceLocation, Supplier<SimpleParticleType>, String, BlockBehaviour.Properties, W> wallTorchSupplier
  ) {
    CobwebRegister<Block> blocks = CobwebRegistry.ofBlocks(fireMod(fireType));
    Supplier<SimpleParticleType> particle = () -> FireManager.getRequiredComponent(fireType, Fire.Component.FLAME_PARTICLE, particleId);
    return Pair.of(blocks.register(torchId, () -> torchSupplier.apply(fireType, particle, BlockBehaviour.Properties.of())), blocks.register(wallTorchId, () -> wallTorchSupplier.apply(fireType, particle, torchId, BlockBehaviour.Properties.of())));
  }

  /**
   * Registers the torch item for the specified fire.<br>
   * Must be called <strong>after</strong> {@link #registerTorch}.<p>
   *
   * Use the {@link #registerTorchItem(ResourceLocation, String, String, String, TriFunction, Item.Properties)} overload if you need to register more than one torch for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the torch item.
   */
  public static CobwebEntry<StandingAndWallBlockItem> registerTorchItem(ResourceLocation fireType) {
    return registerTorchItem(fireType, (torch, wallTorch, properties) -> new StandingAndWallBlockItem(torch, wallTorch, properties, Direction.DOWN));
  }

  /**
   * Registers the torch item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@link #registerTorch}.<p>
   *
   * Use the {@link #registerTorchItem(ResourceLocation, String, String, String, TriFunction, Item.Properties)} overload if you need to register more than one torch for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link StandingAndWallBlockItem} constructor.
   * @param <T> torch item type.
   * @return {@link CobwebEntry} for the torch item.
   */
  public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(ResourceLocation fireType, TriFunction<Block, Block, Item.Properties, T> constructor) {
    return registerTorchItem(fireType, constructor, new Item.Properties());
  }

  /**
   * Registers the torch item for the specified fire from the given constructor and properties.<br>
   * Must be called <strong>after</strong> {@link #registerTorch}.<p>
   *
   * Use the {@link #registerTorchItem(ResourceLocation, String, String, String, TriFunction, Item.Properties)} overload if you need to register more than one torch for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link StandingAndWallBlockItem} constructor.
   * @param properties item properties.
   * @param <T> torch item type.
   * @return {@link CobwebEntry} for the torch item.
   */
  public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(ResourceLocation fireType, TriFunction<Block, Block, Item.Properties, T> constructor, Item.Properties properties) {
    return registerTorchItem(
      fireType,
      FireManager.getComponentPath(fireType, Fire.Component.TORCH_ITEM),
      FireManager.getComponentPath(fireType, Fire.Component.TORCH_BLOCK),
      FireManager.getComponentPath(fireType, Fire.Component.WALL_TORCH_BLOCK),
      constructor,
      properties
    );
  }

  /**
   * Registers the torch item for the specified fire from the given constructor and properties.<br>
   * Must be called <strong>after</strong> {@link #registerTorch}.<p>
   *
   * Use of the other {@code registerTorchItem} overloads if you intend to register just one torch for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code itemId}, {@code torchId}, and {@code wallTorchId} parameters of this overload to specify the ID of the torch.
   *
   * @param fireType fire type.
   * @param itemId item ID.
   * @param torchId block ID of the corresponding torch block.
   * @param wallTorchId block ID of the corresponding wall torch block.
   * @param constructor {@link StandingAndWallBlockItem} constructor.
   * @param properties item properties.
   * @param <T> torch item type.
   * @return {@link CobwebEntry} for the torch item.
   */
  public static <T extends StandingAndWallBlockItem> CobwebEntry<T> registerTorchItem(ResourceLocation fireType, String itemId, String torchId, String wallTorchId, TriFunction<Block, Block, Item.Properties, T> constructor, Item.Properties properties) {
    return CobwebRegistry.ofItems(fireMod(fireType)).register(
      itemId,
      () -> constructor.apply(FireManager.getRequiredComponent(fireType, Fire.Component.TORCH_BLOCK, torchId), FireManager.getRequiredComponent(fireType, Fire.Component.WALL_TORCH_BLOCK, wallTorchId), properties)
    );
  }

  /**
   * Registers the lantern block for the specified fire.<p>
   *
   * Use the {@link #registerLantern(ResourceLocation, String, BiFunction)} overload if you need to register more than one lantern for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the lantern block.
   */
  public static CobwebEntry<CustomLanternBlock> registerLantern(ResourceLocation fireType) {
    return registerLantern(fireType, CustomLanternBlock::new);
  }

  /**
   * Registers the lantern block for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerLantern(ResourceLocation, String, BiFunction)} overload if you need to register more than one lantern for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link CustomLanternBlock} constructor.
   * @param <T> lantern block type.
   * @return {@link CobwebEntry} for the lantern block.
   */
  public static <T extends CustomLanternBlock> CobwebEntry<T> registerLantern(ResourceLocation fireType, BiFunction<ResourceLocation, BlockBehaviour.Properties, T> constructor) {
    return registerLantern(fireType, FireManager.getComponentPath(fireType, Fire.Component.LANTERN_BLOCK), constructor);
  }

  /**
   * Registers the lantern block for the specified fire from the given constructor.<p>
   *
   * Use of the other {@code registerLantern} overloads if you intend to register just one lantern for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code blockId} parameter of this overload to specify the ID of the lantern.
   *
   * @param fireType fire type.
   * @param blockId block ID.
   * @param constructor {@link CustomLanternBlock} constructor.
   * @param <T> lantern block type.
   * @return {@link CobwebEntry} for the lantern block.
   */
  public static <T extends CustomLanternBlock> CobwebEntry<T> registerLantern(ResourceLocation fireType, String blockId, BiFunction<ResourceLocation, BlockBehaviour.Properties, T> constructor) {
    return CobwebRegistry.ofBlocks(fireMod(fireType)).register(blockId, () -> constructor.apply(fireType, BlockBehaviour.Properties.of()));
  }

  /**
   * Registers the lantern item for the specified fire.<br>
   * Must be called <strong>after</strong> {@link #registerLantern}.<p>
   *
   * Use the {@link #registerLanternItem(ResourceLocation, String, String, BiFunction, Item.Properties)} overload if you need to register more than one lantern for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the lantern item.
   */
  public static CobwebEntry<BlockItem> registerLanternItem(ResourceLocation fireType) {
    return registerLanternItem(fireType, BlockItem::new);
  }

  /**
   * Registers the lantern item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@link #registerLantern}.<p>
   *
   * Use the {@link #registerLanternItem(ResourceLocation, String, String, BiFunction, Item.Properties)} overload if you need to register more than one lantern for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link BlockItem} constructor.
   * @return {@link CobwebEntry} for the lantern item.
   * @param <T> item type.
   */
  public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(ResourceLocation fireType, BiFunction<Block, Item.Properties, T> constructor) {
    return registerLanternItem(fireType, constructor, new Item.Properties());
  }

  /**
   * Registers the lantern item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@link #registerLantern}.<p>
   *
   * Use the {@link #registerLanternItem(ResourceLocation, String, String, BiFunction, Item.Properties)} overload if you need to register more than one lantern for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link BlockItem} constructor.
   * @param properties item properties.
   * @return {@link CobwebEntry} for the lantern item.
   * @param <T> item type.
   */
  public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(ResourceLocation fireType, BiFunction<Block, Item.Properties, T> constructor, Item.Properties properties) {
    return registerLanternItem(fireType, FireManager.getComponentPath(fireType, Fire.Component.LANTERN_ITEM), FireManager.getComponentPath(fireType, Fire.Component.LANTERN_BLOCK), constructor, properties);
  }

  /**
   * Registers the lantern item for the specified fire from the given constructor.<br>
   * Must be called <strong>after</strong> {@link #registerLantern}.<p>
   *
   * Use of the other {@code registerLanternItem} overloads if you intend to register just one lantern for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code itemId} and {@code blockId} parameters of this overload to specify the ID of the lantern.
   *
   * @param fireType fire type.
   * @param itemId item ID.
   * @param blockId block ID of the corresponding lantern block.
   * @param constructor {@link BlockItem} constructor.
   * @param properties item properties.
   * @return {@link CobwebEntry} for the lantern item.
   * @param <T> item type.
   */
  public static <T extends BlockItem> CobwebEntry<T> registerLanternItem(ResourceLocation fireType, String itemId, String blockId, BiFunction<Block, Item.Properties, T> constructor, Item.Properties properties) {
    return CobwebRegistry.ofItems(fireMod(fireType)).register(itemId, () -> constructor.apply(FireManager.getRequiredComponent(fireType, Fire.Component.LANTERN_BLOCK, blockId), properties));
  }

  /**
   * Registers the fire charge item for the specified fire.<p>
   *
   * Use the {@link #registerFireCharge(ResourceLocation, String, Function)} overload if you need to register more than one fire charge for your fire.
   *
   * @param fireType fire type.
   * @return {@link CobwebEntry} for the fire charge item.
   */
  public static CobwebEntry<FireChargeItem> registerFireCharge(ResourceLocation fireType) {
    return registerFireCharge(fireType, FireChargeItem::new);
  }

  /**
   * Registers the fire charge item for the specified fire from the given constructor.<p>
   *
   * Use the {@link #registerFireCharge(ResourceLocation, String, Function)} overload if you need to register more than one fire charge for your fire.
   *
   * @param fireType fire type.
   * @param constructor {@link FireChargeItem} constructor.
   * @return {@link CobwebEntry} for the fire charge item.
   * @param <T> item type.
   */
  public static <T extends FireChargeItem> CobwebEntry<T> registerFireCharge(ResourceLocation fireType, Function<Item.Properties, T> constructor) {
    return registerFireCharge(fireType, FireManager.getComponentPath(fireType, Fire.Component.FIRE_CHARGE_ITEM), constructor);
  }

  /**
   * Registers the fire charge item for the specified fire from the given constructor.<p>
   *
   * Use of the other {@code registerLanternItem} overloads if you intend to register just one fire charge for your fire.<br>
   * Instead, if you intend to register more than one, use the {@code itemId} parameter of this overload to specify the ID of the fire charge.
   *
   * @param fireType fire type.
   * @param itemId item ID.
   * @param constructor {@link FireChargeItem} constructor.
   * @return {@link CobwebEntry} for the fire charge item.
   * @param <T> item type.
   */
  public static <T extends FireChargeItem> CobwebEntry<T> registerFireCharge(ResourceLocation fireType, String itemId, Function<Item.Properties, T> constructor) {
    CobwebEntry<T> charge = CobwebRegistry.ofItems(fireMod(fireType)).register(itemId, () -> constructor.apply(new Item.Properties()));
    FIRE_CHARGE_TAGS.add(() -> DynamicTagBuilder.of(Registries.ITEM, ItemTags.CREEPER_IGNITERS).addElement(charge.get()));
    if (Services.PLATFORM.getPlatformName() == Platform.FABRIC) {
      DispenserBlock.registerProjectileBehavior(charge.get());
    }
    return charge;
  }

  /**
   * Returns the namespace (mod ID) of the given fire type.
   *
   * @param fireType fire type.
   * @return fire mod ID.
   */
  private static String fireMod(ResourceLocation fireType) {
    return fireType.getNamespace();
  }
}

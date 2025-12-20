package it.crystalnest.prometheus.api;

import it.crystalnest.cobweb.api.block.entity.DynamicBlockEntityType;
import it.crystalnest.cobweb.api.registry.CobwebEntry;
import it.crystalnest.cobweb.api.registry.CobwebRegistry;
import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.QuadriFunction;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.entity.CustomCampfireBlockEntity;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Static manager for registered Fires.
 */
public final class FireManager {
  /**
   * Fire type of Vanilla Fire.
   */
  public static final Identifier DEFAULT_FIRE_TYPE = Identifier.withDefaultNamespace("");

  /**
   * Fire type of Soul Fire.
   */
  public static final Identifier SOUL_FIRE_TYPE = Identifier.withDefaultNamespace("soul");

  /**
   * Fire type of Copper Fire.
   */
  public static final Identifier COPPER_FIRE_TYPE = Identifier.withDefaultNamespace("copper");

  /**
   * Default {@link Fire} used as fallback to retrieve default properties.
   */
  @SuppressWarnings("DataFlowIssue")
  public static final Fire DEFAULT_FIRE = new Fire(
    DEFAULT_FIRE_TYPE,
    Fire.Builder.DEFAULT_LIGHT,
    Fire.Builder.DEFAULT_DAMAGE,
    Fire.Builder.DEFAULT_INVERT_HEAL_AND_HARM,
    true,
    Fire.Builder.DEFAULT_ON_CAMPFIRE_GETTER,
    Fire.Builder.DEFAULT_IN_FIRE_GETTER,
    Fire.Builder.DEFAULT_ON_FIRE_GETTER,
    Fire.Builder.DEFAULT_BEHAVIOR,
    Map.ofEntries(
      Map.entry(Fire.Component.SOURCE_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.FIRE))),
      Map.entry(Fire.Component.CAMPFIRE_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.CAMPFIRE))),
      Map.entry(Fire.Component.CAMPFIRE_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.CAMPFIRE))),
      Map.entry(Fire.Component.LANTERN_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.LANTERN))),
      Map.entry(Fire.Component.LANTERN_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.LANTERN))),
      Map.entry(Fire.Component.TORCH_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.TORCH))),
      Map.entry(Fire.Component.WALL_TORCH_BLOCK, List.of(BuiltInRegistries.BLOCK.getKey(Blocks.WALL_TORCH))),
      Map.entry(Fire.Component.TORCH_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.TORCH))),
      Map.entry(Fire.Component.FLAME_PARTICLE, List.of(BuiltInRegistries.PARTICLE_TYPE.getKey(ParticleTypes.FLAME))),
      Map.entry(Fire.Component.FIRE_CHARGE_ITEM, List.of(BuiltInRegistries.ITEM.getKey(Items.FIRE_CHARGE)))
    )
  );

  /**
   * {@link ConcurrentHashMap} of all registered {@link Fire Fires}.
   */
  private static final ConcurrentHashMap<Identifier, Fire> FIRES = new ConcurrentHashMap<>();

  /**
   * Default {@link DynamicBlockEntityType} for custom campfires.
   */
  @ApiStatus.Internal
  private static CobwebEntry<DynamicBlockEntityType<@NotNull CustomCampfireBlockEntity>> CUSTOM_CAMPFIRE_ENTITY_TYPE;

  /**
   * Whether this class has already been loaded.
   */
  private static boolean LOADED = false;

  private FireManager() {}

  /**
   * Default {@link DynamicBlockEntityType} for custom campfires.<br>
   * Access only <b>during or after</b> mod initialization.
   *
   * @return default {@link DynamicBlockEntityType} for custom campfires.
   */
  public static CobwebEntry<DynamicBlockEntityType<@NotNull CustomCampfireBlockEntity>> getCustomCampfireEntityType() {
    return CUSTOM_CAMPFIRE_ENTITY_TYPE;
  }

  /**
   * Loads this class.<br>
   * <strong>Internal usage, do not call elsewhere!</strong>
   *
   * @throws IllegalStateException if called more than once.
   */
  @ApiStatus.Internal
  public static synchronized void load() {
    if (LOADED) {
      throw new IllegalStateException("FireManager was already loaded");
    }
    LOADED = true;
    CUSTOM_CAMPFIRE_ENTITY_TYPE = CobwebRegistry.of(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID).register(
      "custom_campfire",
      () -> DynamicBlockEntityType.of(CustomCampfireBlockEntity::new, state -> FireManager.getComponentList(Fire.Component.CAMPFIRE_BLOCK).stream().filter(CustomCampfireBlock.class::isInstance).toList().contains(state.getBlock()))
    );
    FireRegistrar.load();
  }

  /**
   * Returns a new {@link Fire.Builder}.
   *
   * @param modId {@code modId} of the new {@link Fire} to build.
   * @param fireId {@code fireId} of the new {@link Fire} to build.
   * @return a new {@link Fire.Builder}.
   */
  public static Fire.Builder fireBuilder(String modId, String fireId) {
    return new Fire.Builder(modId, fireId);
  }

  /**
   * Returns a new {@link Fire.Builder}.
   *
   * @param fireType {@link Identifier} of the new {@link Fire} to build.
   * @return a new {@link Fire.Builder}.
   */
  public static Fire.Builder fireBuilder(Identifier fireType) {
    return new Fire.Builder(fireType);
  }

  /**
   * Attempts to register the given {@link Fire}.<br>
   * If the {@link Fire#fireType} is already registered, logs an error.
   *
   * @param fire {@link Fire} to register.
   * @return whether the registration is successful.
   */
  @Nullable
  public static synchronized Fire registerFire(Fire fire) {
    Fire previous = FIRES.computeIfAbsent(fire.getFireType(), key -> {
      // Need to manually set the fire type for blocks registered via data packs.
      Fire.Component.SOURCE_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
      Fire.Component.CAMPFIRE_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
      Fire.Component.LANTERN_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
      Fire.Component.TORCH_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
      Fire.Component.WALL_TORCH_BLOCK.getOptionalValues(fire).forEach(setTypeOrWarn(key));
      return fire;
    });
    if (previous != fire) {
      Identifier fireType = fire.getFireType();
      Constants.LOGGER.error("Fire [{}] was already registered with the following value: {}", fireType, getFire(fireType));
      return null;
    }
    Constants.LOGGER.debug("Successfully registered Fire [{}]", fire);
    return fire;
  }

  /**
   * Attempts to set the given fire type to the given block.<br>
   * Logs a warning if unsuccessful.
   *
   * @param fireType fire type.
   * @return lambda to set the block's fire type if possible.
   */
  private static @NotNull Consumer<Optional<Block>> setTypeOrWarn(Identifier fireType) {
    return value -> value.ifPresent(block -> {
      if (block instanceof FireTypeChanger fireTypeChanger) {
        fireTypeChanger.setFireType(fireType);
      } else {
        Constants.LOGGER.warn("Could not set Fire Type [{}] for block [{}]\nThings might not work as expected!\nYou can ignore this warning if this was intended", fireType, block);
      }
    });
  }

  /**
   * Attempts to register all the given {@link Fire}s.
   *
   * @param fires {@link Fire}s to register.
   * @return an {@link Map} with the outcome of each registration attempt.
   */
  public static synchronized Map<Identifier, @Nullable Fire> registerFires(Fire... fires) {
    return registerFires(List.of(fires));
  }

  /**
   * Attempts to register all the given {@link Fire}s.
   *
   * @param fires {@link Fire}s to register.
   * @return an {@link Map} with the outcome of each registration attempt.
   */
  public static synchronized Map<Identifier, @Nullable Fire> registerFires(List<Fire> fires) {
    HashMap<Identifier, @Nullable Fire> outcomes = new HashMap<>();
    for (Fire fire : fires) {
      outcomes.put(fire.getFireType(), registerFire(fire));
    }
    return outcomes;
  }

  /**
   * Unregisters the specified fire.<br>
   * Internally use only, do not use elsewhere!
   *
   * @param fireType fire type.
   * @return whether the fire was previously registered.
   */
  @Nullable
  @ApiStatus.Internal
  public static synchronized Fire unregisterFire(Identifier fireType) {
    return FIRES.remove(fireType);
  }

  /**
   * Returns the {@link Fire} registered with the given {@code id}.<br>
   * Returns {@link #DEFAULT_FIRE} if no {@link Fire} is registered with the given {@code modId} and {@code fireId}.
   *
   * @param modId mod ID.
   * @param fireId fire ID.
   * @return registered {@link Fire} or {@link #DEFAULT_FIRE}.
   */
  public static Fire getFire(@Nullable String modId, @Nullable String fireId) {
    return isValidModId(modId) && isValidFireId(fireId) ? getFire(fireType(modId, fireId)) : DEFAULT_FIRE;
  }

  /**
   * Returns the {@link Fire} registered with the given {@code id}.<br>
   * Returns {@link #DEFAULT_FIRE} if no {@link Fire} is registered with the given {@code fireType}.
   *
   * @param fireType fire type.
   * @return registered {@link Fire} or {@link #DEFAULT_FIRE}.
   */
  public static Fire getFire(@Nullable Identifier fireType) {
    return FIRES.getOrDefault(ensure(fireType), DEFAULT_FIRE);
  }

  /**
   * Returns the list of all registered {@link Fire}s.
   *
   * @return the list of all registered {@link Fire}s.
   */
  public static List<Fire> getFires() {
    return FIRES.values().stream().toList();
  }

  /**
   * Returns the specified property of the specified fire.<br>
   * Defaults to the property of the {@link #DEFAULT_FIRE} if the specified fire is not registered.
   *
   * @param fireType fire type.
   * @param getter property getter (use a method from the {@link Fire} class).
   * @param <T> property type.
   * @return property value.
   */
  public static <T> T getProperty(Identifier fireType, Function<Fire, T> getter) {
    return getter.apply(getFire(fireType));
  }

  /**
   * Returns the list of the specified property from all the registered fires.<br>
   * This list will have as many elements as there are registered fires.
   *
   * @param getter property getter.
   * @param <T> property type.
   * @return property list.
   */
  public static <T> List<T> getPropertyList(Function<Fire, T> getter) {
    return FIRES.values().stream().map(getter).toList();
  }

  /**
   * Returns the specified damage source retrieved from the {@link Fire} damage source getter.
   *
   * @param entity entity.
   * @param fireType fire type.
   * @param getter damage source getter (use a method from the {@link Fire} class).
   * @return the correct damage source for the specified fire.
   */
  public static DamageSource getDamageSource(Entity entity, Identifier fireType, BiFunction<Fire, Entity, DamageSource> getter) {
    return getter.apply(getFire(fireType), entity);
  }

  /**
   * Returns the specified component of the specified fire.<br>
   * Defaults to the component of the {@link #DEFAULT_FIRE} if the specified fire is not registered.<br>
   * There might be multiple values for the same component; to retrieve them all use {@link #getComponentIds(Identifier, Fire.Component)} instead.
   *
   * @param fireType fire type.
   * @param component component.
   * @return component {@link Identifier}.
   */
  @Nullable
  public static Identifier getComponentId(Identifier fireType, Fire.Component<?, ?> component) {
    return getFire(fireType).getComponent(component);
  }

  /**
   * Returns the specified component of the specified fire.<br>
   * Defaults to the component of the {@link #DEFAULT_FIRE} if the specified fire is not registered.
   *
   * @param fireType fire type.
   * @param component component.
   * @return component {@link Identifier}.
   */
  @Nullable
  public static List<Identifier> getComponentIds(Identifier fireType, Fire.Component<?, ?> component) {
    return getFire(fireType).getComponentList(component);
  }

  /**
   * Returns the specified component value of the specified fire.<br>
   * Defaults to the component value of the {@link #DEFAULT_FIRE} if the specified fire is not registered.<br>
   * There might be multiple values for the same component; to retrieve them all use {@link #getComponentList(Identifier, Fire.Component)} instead.
   *
   * @param fireType fire type.
   * @param component component.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value.
   */
  @Nullable
  public static <R, T extends R> T getComponent(Identifier fireType, Fire.Component<R, T> component) {
    return component.getValue(getComponentId(fireType, component));
  }

  /**
   * Returns the specified component value of the specified fire.<br>
   * Defaults to the component value of the {@link #DEFAULT_FIRE} if the specified fire is not registered.
   *
   * @param fireType fire type.
   * @param component component.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value.
   */
  @Nullable
  public static <R, T extends R> List<T> getComponentList(Identifier fireType, Fire.Component<R, T> component) {
    List<Identifier> list = getComponentIds(fireType, component);
    return list == null ? null : list.stream().map(component::getValue).toList();
  }

  /**
   * Returns the path of the component {@link Identifier}.<br>
   * There might be multiple values for the same component; to retrieve them all use {@link #getComponentPaths(Identifier, Fire.Component)} instead.
   *
   * @param fireType fire type.
   * @param component component.
   * @return component {@link Identifier} path.
   */
  @NotNull
  static String getComponentPath(Identifier fireType, Fire.Component<?, ?> component) {
    return Objects.requireNonNull(getComponentId(fireType, component)).getPath();
  }

  /**
   * Returns the path of the component {@link Identifier}.
   *
   * @param fireType fire type.
   * @param component component.
   * @return component {@link Identifier} path.
   */
  @NotNull
  private static List<String> getComponentPaths(Identifier fireType, Fire.Component<?, ?> component) {
    return Objects.requireNonNull(getComponentIds(fireType, component)).stream().map(Identifier::getPath).toList();
  }

  /**
   * Returns the specified component value of the specified fire.<br>
   * Defaults to the component value of the {@link #DEFAULT_FIRE} if the specified fire is not registered.<br>
   * There might be multiple values for the same component; to retrieve them all use {@link #getRequiredComponentList(Identifier, Fire.Component)} instead.
   *
   * @param fireType fire type.
   * @param component component.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value.
   * @throws NullPointerException if the specified fire is registered but doesn't have the specified component.
   */
  @NotNull
  public static <R, T extends R> T getRequiredComponent(Identifier fireType, Fire.Component<R, T> component) throws NullPointerException {
    return Objects.requireNonNull(component.getValue(getComponentId(fireType, component)));
  }

  /**
   * Returns the specified component value of the specified fire.
   * Unlike {@link #getRequiredComponent(Identifier, Fire.Component)}, the parameter {@code id} of this overload allows to specify which of the (possibly) many values for this component to select.<br>
   * Defaults to the component value of the {@link #DEFAULT_FIRE} if the specified fire is not registered.<br>
   * There might be multiple values for the same component; to retrieve them all use {@link #getRequiredComponentList(Identifier, Fire.Component)} instead.
   *
   * @param fireType fire type.
   * @param component component.
   * @param id ID to specify which component value to select.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value.
   * @throws NullPointerException if the specified fire is registered but doesn't have the specified component.
   */
  @NotNull
  public static <R, T extends R> T getRequiredComponent(Identifier fireType, Fire.Component<R, T> component, String id) throws NullPointerException {
    return Objects.requireNonNull(component.getValue(Objects.requireNonNull(getComponentIds(fireType, component)).stream().filter(path -> path.getPath().equals(id)).findFirst().orElseThrow()));
  }

  /**
   * Returns the specified component value of the specified fire.<br>
   * Defaults to the component value of the {@link #DEFAULT_FIRE} if the specified fire is not registered.
   *
   * @param fireType fire type.
   * @param component component.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value.
   * @throws NullPointerException if the specified fire is registered but doesn't have the specified component.
   */
  @NotNull
  public static <R, T extends R> List<T> getRequiredComponentList(Identifier fireType, Fire.Component<R, T> component) throws NullPointerException {
    return Objects.requireNonNull(getComponentIds(fireType, component)).stream().map(component::getValue).toList();
  }

  /**
   * Returns the list of the specified component IDs from all the registered fires.<br>
   * This list won't necessarily have as many elements as there are registered fires because fires without the specified component are filtered out.<br>
   * There might be multiple values for each component; to retrieve them all use {@link #getComponentIdsList(Fire.Component)} instead.
   *
   * @param component component.
   * @return component ID list.
   */
  public static List<Identifier> getComponentIdList(Fire.Component<?, ?> component) {
    return FIRES.values().stream().map(fire -> fire.getComponent(component)).filter(Objects::nonNull).toList();
  }

  /**
   * Returns the list of the specified component IDs from all the registered fires.<br>
   * This list won't necessarily have as many elements as there are registered fires because fires without the specified component are filtered out.
   *
   * @param component component.
   * @return component ID list.
   */
  public static List<List<Identifier>> getComponentIdsList(Fire.Component<?, ?> component) {
    return FIRES.values().stream().map(fire -> fire.getComponentList(component)).filter(l -> !(l == null || l.isEmpty())).toList();
  }

  /**
   * Returns the list of the specified component values from all the registered fires.<br>
   * This list won't necessarily have as many elements as there are registered fires because fires without the specified component are filtered out.<br>
   * There might be multiple values for each component; to retrieve them all use {@link #getComponentListList(Fire.Component)} instead.
   *
   * @param component component.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value list.
   */
  public static <R, T extends R> List<T> getComponentList(Fire.Component<R, T> component) {
    return FIRES.values().stream().map(component::getValue).filter(Objects::nonNull).toList();
  }

  /**
   * Returns the list of the specified component values from all the registered fires.<br>
   * This list won't necessarily have as many elements as there are registered fires because fires without the specified component are filtered out.
   *
   * @param component component.
   * @param <R> component registry.
   * @param <T> component value type.
   * @return component value list.
   */
  public static <R, T extends R> List<List<T>> getComponentListList(Fire.Component<R, T> component) {
    return FIRES.values().stream().map(component::getValues).filter(l -> !(l == null || l.isEmpty())).toList();
  }

  /**
   * Returns whether the given {@code modId} and {@code fireId} represent a valid fire type.
   *
   * @param modId mod ID.
   * @param fireId fire ID.
   * @return whether the given values represent a valid fire type.
   */
  public static boolean isValidType(@Nullable String modId, @Nullable String fireId) {
    return isValidModId(modId) && isValidFireId(fireId);
  }

  /**
   * Returns whether the given {@code modId} and {@code fireId} represent a valid fire type.
   *
   * @param fireType fire type.
   * @return whether the given values represent a valid fire type.
   */
  public static boolean isValidType(@Nullable Identifier fireType) {
    return fireType != null && Strings.isNotBlank(fireType.getNamespace()) && Strings.isNotBlank(fireType.getPath());
  }

  /**
   * Returns whether a fire is registered with the given {@code modId} and {@code fireId}.
   *
   * @param modId mod ID.
   * @param fireId fire ID.
   * @return whether a fire is registered with the given values.
   */
  public static boolean isRegisteredType(@Nullable String modId, @Nullable String fireId) {
    return isValidModId(modId) && isValidFireId(fireId) && isRegisteredType(fireType(modId, fireId));
  }

  /**
   * Returns whether a fire is registered with the given {@code fireType}.
   *
   * @param fireType fire type.
   * @return whether a fire is registered with the given {@code fireType}.
   */
  public static boolean isRegisteredType(@Nullable Identifier fireType) {
    return fireType != null && FIRES.containsKey(fireType);
  }

  /**
   * Returns whether the given fire ID is a valid fire ID.
   *
   * @param fireId fire ID.
   * @return whether the given fire ID is a valid fire ID.
   */
  public static boolean isValidFireId(@Nullable String fireId) {
    return Strings.isNotBlank(fireId) && Identifier.isValidPath(fireId);
  }

  /**
   * Returns whether the given fire ID is a valid and registered fire ID.
   *
   * @param fireId fire ID.
   * @return whether the given fire ID is a valid and registered fire ID.
   */
  public static boolean isRegisteredFireId(@Nullable String fireId) {
    return isValidFireId(fireId) && FIRES.keySet().stream().anyMatch(fireType -> fireType.getPath().equals(fireId));
  }

  /**
   * Returns whether the given mod ID is a valid mod ID.
   *
   * @param modId mod ID
   * @return whether the given mod ID is a valid mod ID.
   */
  public static boolean isValidModId(@Nullable String modId) {
    return Strings.isNotBlank(modId) && Identifier.isValidNamespace(modId);
  }

  /**
   * Returns whether the given mod ID is a valid, loaded and registered mod ID.
   *
   * @param modId mod ID.
   * @return whether the given mod ID is a valid, loaded and registered mod ID.
   */
  public static boolean isRegisteredModId(@Nullable String modId) {
    return isValidModId(modId) && FIRES.keySet().stream().anyMatch(fireType -> fireType.getNamespace().equals(modId));
  }

  /**
   * Returns the closest well-formed fire type from the given {@code modId} and {@code fireId}.
   *
   * @param modId mod ID.
   * @param fireId fire ID.
   * @return the closest well-formed fire type.
   */
  public static Identifier sanitize(@Nullable String modId, @Nullable String fireId) {
    return isValidModId(modId) && isValidModId(fireId) ? sanitize(fireType(modId, fireId)) : DEFAULT_FIRE_TYPE;
  }

  /**
   * Returns the closest well-formed fire type from the given {@code fireType}.
   *
   * @param fireType fire type.
   * @return the closest well-formed fire type.
   */
  public static Identifier sanitize(@Nullable Identifier fireType) {
    return isValidType(fireType) ? fireType : DEFAULT_FIRE_TYPE;
  }

  /**
   * Returns the closest well-formed and registered fire type from the given {@code modId} and {@code fireId}.
   *
   * @param modId mod ID.
   * @param fireId fire ID.
   * @return the closest well-formed and registered fire type.
   */
  public static Identifier ensure(@Nullable String modId, @Nullable String fireId) {
    String trimmedModId = modId == null ? "" : modId.trim();
    String trimmedFireId = fireId == null ? "" : fireId.trim();
    return isValidModId(trimmedModId) && isValidFireId(trimmedFireId) ? ensure(Identifier.fromNamespaceAndPath(trimmedModId, trimmedFireId)) : DEFAULT_FIRE_TYPE;
  }

  /**
   * Returns the closest well-formed and registered fire type from the given {@code fireType}.
   *
   * @param fireType fire type.
   * @return the closest well-formed and registered fire type.
   */
  public static Identifier ensure(@Nullable Identifier fireType) {
    return isRegisteredType(fireType) ? fireType : DEFAULT_FIRE_TYPE;
  }

  /**
   * Returns the list of all fire types.
   *
   * @return the list of all fire types.
   */
  public static List<Identifier> getFireTypes() {
    return FIRES.keySet().stream().toList();
  }

  /**
   * Returns the list of all registered fire IDs.
   *
   * @return the list of all registered fire IDs.
   */
  public static List<String> getFireIds() {
    return FIRES.keySet().stream().map(Identifier::getPath).toList();
  }

  /**
   * Returns the list of all registered mod IDs.
   *
   * @return the list of all registered mod IDs.
   */
  public static List<String> getModIds() {
    return FIRES.keySet().stream().map(Identifier::getNamespace).toList();
  }

  /**
   * Returns the fire type associated to the specified component.
   *
   * @param component component.
   * @param object fire-related game object.
   * @param <R> object registry.
   * @param <T> object type.
   * @return the object's fire type.
   */
  public static <R, T extends R> Identifier getFireType(Fire.Component<R, T> component, T object) {
    return FireManager.getFireTypes().stream().filter(type -> FireManager.getComponent(type, component) == object).findFirst().orElse(DEFAULT_FIRE_TYPE);
  }

  /**
   * Set on fire the given entity for the given seconds with the given fire type.
   *
   * @param entity {@link Entity} to set on fire.
   * @param seconds amount of seconds the fire should last for.
   * @param fireType fire type.
   */
  public static void setOnFire(Entity entity, float seconds, Identifier fireType) {
    setOnFire(entity, seconds, fireType, Entity::igniteForSeconds);
  }

  /**
   * Set on fire the given entity for the given seconds with the given fire type.<br>
   * This is for internal use only (or for mixin usage). Use {@link #setOnFire(Entity, float, Identifier)} instead.
   *
   * @param entity {@link Entity} to set on fire.
   * @param duration amount of time the fire should last for.
   * @param fireType fire type.
   * @param setOnFireFunction how to set the entity on fire.
   */
  @ApiStatus.Internal
  public static void setOnFire(Entity entity, float duration, Identifier fireType, BiConsumer<Entity, Float> setOnFireFunction) {
    setOnFireFunction.accept(entity, duration);
    ((FireTypeChanger) entity).setFireType(ensure(fireType));
  }

  /**
   * Hurts or heals the given {@code entity}.<br>
   * Also applies the custom fire behavior.
   *
   * @param entity entity to hurt/heal.
   * @param fireType fire type.
   * @param damageSourceGetter getter for the damage source. See .
   * @return whether the {@code entity} was hurt.
   */
  public static boolean affect(Entity entity, Identifier fireType, BiFunction<Fire, Entity, DamageSource> damageSourceGetter) {
    return affect(entity, fireType, damageSourceGetter, Entity::hurtServer);
  }

  /**
   * Hurts or heals the given {@code entity}.<br>
   * Also applies the custom fire behavior.<br>
   * This is for internal use only (or for mixin usage). Use {@link #affect(Entity, Identifier, BiFunction)} instead.
   *
   * @param entity entity to hurt/heal.
   * @param fireType fire type.
   * @param damageSourceGetter getter for the damage source. See {@link #getDamageSource(Entity, Identifier, BiFunction)}.
   * @return whether the {@code entity} was hurt.
   */
  @ApiStatus.Internal
  public static boolean affect(Entity entity, Identifier fireType, BiFunction<Fire, Entity, DamageSource> damageSourceGetter, TriFunction<Entity, DamageSource, Float, Void> hurtFunction) {
    return affect(entity, fireType, damageSourceGetter, (e, l, ds, d) -> {
      hurtFunction.apply(e, ds, d);
      return true;
    });
  }

  /**
   * Hurts or heals the given {@code entity}.<br>
   * Also applies the custom fire behavior.<br>
   * This is for internal use only (or for mixin usage). Use {@link #affect(Entity, Identifier, BiFunction)} instead.
   *
   * @param entity entity to hurt/heal.
   * @param fireType fire type.
   * @param damageSourceGetter getter for the damage source. See {@link #getDamageSource(Entity, Identifier, BiFunction)}.
   * @return whether the {@code entity} was hurt.
   */
  @ApiStatus.Internal
  public static boolean affect(Entity entity, Identifier fireType, BiFunction<Fire, Entity, DamageSource> damageSourceGetter, QuadriFunction<Entity, ServerLevel, DamageSource, Float, Boolean> hurtFunction) {
    ((FireTypeChanger) entity).setFireType(ensure(fireType));
    return affect(entity, getDamageSource(entity, fireType, damageSourceGetter), FireManager.getProperty(fireType, Fire::getDamage), FireManager.getProperty(fireType, Fire::invertHealAndHarm), hurtFunction);
  }

  /**
   * Hurts or heals the given {@code entity}.<br>
   * Also applies the custom fire behavior.<br>
   * This is for internal use only (or for mixin usage). Use {@link #affect(Entity, Identifier, BiFunction)} instead.
   *
   * @param entity entity to hurt/heal.
   * @param damageSource damage source.
   * @param damage damage/heal amount.
   * @param invertHealAndHarm whether to invert heal and harm.
   * @param hurtFunction how to harm the {@code entity}.
   * @return whether the {@code entity} was hurt.
   */
  private static boolean affect(Entity entity, DamageSource damageSource, float damage, boolean invertHealAndHarm, QuadriFunction<Entity, ServerLevel, DamageSource, Float, Boolean> hurtFunction) {
    Predicate<Entity> behavior = FireManager.getProperty(((FireTyped) entity).getFireType(), Fire::getBehavior);
    if (entity.level() instanceof ServerLevel level && behavior.test(entity) && Float.compare(damage, 0) != 0) {
      if (damage > 0) {
        if (entity instanceof LivingEntity livingEntity) {
          if (livingEntity.isInvertedHealAndHarm() && invertHealAndHarm) {
            livingEntity.heal(damage);
            return false;
          }
          return hurtFunction.apply(livingEntity, level, damageSource, damage);
        }
        return hurtFunction.apply(entity, level, damageSource, damage);
      }
      if (entity instanceof LivingEntity livingEntity) {
        if (livingEntity.isInvertedHealAndHarm() && invertHealAndHarm) {
          return hurtFunction.apply(livingEntity, level, damageSource, -damage);
        }
        livingEntity.heal(-damage);
        return false;
      }
    }
    return false;
  }

  /**
   * Safety methods to avoid data flow warnings for strings that are nullable but really aren't.
   *
   * @param modId mod ID.
   * @param fireId fire ID.
   * @return {@link Identifier}.
   */
  private static Identifier fireType(@Nullable String modId, @Nullable String fireId) {
    return Identifier.fromNamespaceAndPath(Objects.requireNonNull(modId), Objects.requireNonNull(fireId));
  }

  /**
   * Shorthand for getting the {@link Fire#light} property of the specified fire.
   *
   * @param fireType fire type.
   * @return fire light property.
   */
  public static int light(Identifier fireType) {
    return FireManager.getProperty(fireType, Fire::getLight);
  }

  /**
   * Shorthand for getting the {@link Fire#light} property of the specified fire, ready for {@link BlockBehaviour.Properties#lightLevel(ToIntFunction)}.
   *
   * @param fireType fire type.
   * @return fire light property.
   */
  public static ToIntFunction<BlockState> lightLevel(Identifier fireType) {
    return state -> FireManager.light(fireType);
  }
}

package it.crystalnest.prometheus.handler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.crystalnest.prometheus.Constants;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Resource reload listener for syncing ddfires.
 */
public class FireResourceReloadListener extends SimpleJsonResourceReloadListener {
  /**
   * Current ddfires to unregister (previous registered ddfires).
   */
  private static final ArrayList<ResourceLocation> ddfiresUnregister = new ArrayList<>();

  /**
   * Current registered ddfires.
   */
  private static final ArrayList<ResourceLocation> ddfiresRegister = new ArrayList<>();

  /**
   * JSON field name for the source block Fire Component.
   */
  private static final String SOURCE_FIELD_NAME = "source";

  /**
   * JSON field name for the campfire block Fire Component.
   */
  private static final String CAMPFIRE_FIELD_NAME = "campfire";

  /**
   * JSON field name for the lantern block Fire Component.
   */
  private static final String LANTERN_FIELD_NAME = "campfire";

  /**
   * JSON field name for the torch block Fire Component.
   */
  private static final String TORCH_FIELD_NAME = "campfire";

  /**
   * JSON field name for the wall torch block Fire Component.
   */
  private static final String WALL_TORCH_FIELD_NAME = "campfire";

  public FireResourceReloadListener() {
    super(new Gson(), "fires");
  }

  /**
   * Handles datapack sync event.
   *
   * @param player {@link ServerPlayer} to which the data is being sent.
   */
  public static void handle(@Nullable ServerPlayer player) {
    for (ResourceLocation fireType : ddfiresUnregister) {
      Services.NETWORK.sendToClient(player, fireType);
    }
    for (ResourceLocation fireType : ddfiresRegister) {
      Services.NETWORK.sendToClient(player, FireManager.getFire(fireType));
    }
  }

  /**
   * Returns the given {@link JsonElement} as a {@link JsonObject}.
   *
   * @param identifier identifier of the JSON file.
   * @param element {@link JsonElement}.
   * @return the given {@link JsonElement} as a {@link JsonObject}.
   * @throws IllegalStateException if the element is not a {@link JsonObject}.
   */
  private static JsonObject getJsonObject(String identifier, JsonElement element) throws IllegalStateException {
    try {
      return element.getAsJsonObject();
    } catch (IllegalStateException e) {
      Constants.LOGGER.error(Constants.MOD_ID + " encountered a non-blocking DDFire error!\nError parsing ddfire [{}]: not a JSON object.", identifier);
      throw e;
    }
  }

  /**
   * Parses the given {@link JsonObject data} to retrieve the specified {@code field} using the provided {@code parser}.
   *
   * @param <T> element type.
   * @param identifier identifier of the JSON file.
   * @param field field to parse.
   * @param data {@link JsonObject} with data to parse.
   * @param parser function to use to retrieve parse a JSON field.
   * @return value of the field.
   * @throws NullPointerException if there's no such field.
   * @throws UnsupportedOperationException if this element is not a {@link JsonPrimitive} or {@link JsonArray}.
   * @throws IllegalStateException if this element is of the type {@link JsonArray} but contains more than a single element.
   * @throws NumberFormatException if the value contained is not a valid number and the expected type ({@code T}) was a number.
   */
  private static <T> T parse(String identifier, String field, JsonObject data, Function<JsonElement, T> parser) throws NullPointerException, UnsupportedOperationException, IllegalStateException, NumberFormatException {
    try {
      return parser.apply(data.get(field));
    } catch (NullPointerException | UnsupportedOperationException | IllegalStateException | NumberFormatException e) {
      Constants.LOGGER.error(Constants.MOD_ID + " encountered a non-blocking DDFire error!\nError parsing required field \"{}\" for ddfire [{}]: missing or malformed field.", field, identifier);
      throw e;
    }
  }

  /**
   * Parses the given {@link JsonObject data} to retrieve the specified {@code field} using the provided {@code parser}.
   *
   * @param <T> element type.
   * @param identifier identifier of the JSON file.
   * @param field field to parse.
   * @param data {@link JsonObject} with data to parse.
   * @param parser function to use to retrieve parse a JSON field.
   * @param fallback default value if no field named {@code field} exists.
   * @return value of the field or default.
   * @throws UnsupportedOperationException if this element is not a {@link JsonPrimitive} or {@link JsonArray}.
   * @throws IllegalStateException if this element is of the type {@link JsonArray} but contains more than a single element.
   * @throws NumberFormatException if the value contained is not a valid number and the expected type ({@code T}) was a number.
   */
  private static <T> T parse(String identifier, String field, JsonObject data, Function<JsonElement, T> parser, T fallback) throws UnsupportedOperationException, IllegalStateException, NumberFormatException {
    try {
      return parser.apply(data.get(field));
    } catch (NullPointerException e) {
      return fallback;
    } catch (UnsupportedOperationException | IllegalStateException | NumberFormatException e) {
      Constants.LOGGER.error(Constants.MOD_ID + " encountered a non-blocking DDFire error!\nError parsing optional field \"{}\" for ddfire [{}]: malformed field.", field, identifier);
      throw e;
    }
  }

  /**
   * Unregisters all DDFires.
   */
  private static void unregisterFires() {
    for (ResourceLocation fireType : ddfiresRegister) {
      if (FireManager.unregisterFire(fireType) != null) {
        ddfiresUnregister.add(fireType);
      }
    }
    ddfiresRegister.clear();
  }

  /**
   * Registers a DDFire.
   *
   * @param fireType fire type.
   * @param fire fire.
   */
  private static void registerFire(ResourceLocation fireType, Fire fire) {
    if (FireManager.registerFire(fire) != null) {
      ddfiresRegister.add(fireType);
    } else {
      Constants.LOGGER.error("Unable to register ddfire [{}].", fireType);
    }
  }

  /**
   * Builds and registers a DDFire.
   *
   * @param jsonFire JSON fire data.
   * @param mod related mod.
   * @param jsonIdentifier JSON ID.
   */
  private static void registerFire(JsonObject jsonFire, String mod, String jsonIdentifier) {
    ResourceLocation fireType = ResourceLocation.fromNamespaceAndPath(mod, parse(jsonIdentifier, "fire", jsonFire, JsonElement::getAsString));
    String fireTypeString = fireType.toString();
    Fire.Builder builder = FireManager.fireBuilder(fireType)
      .setDamage(parse(fireTypeString, "damage", jsonFire, JsonElement::getAsFloat, Fire.Builder.DEFAULT_DAMAGE))
      .setInvertHealAndHarm(parse(fireTypeString, "invertHealAndHarm", jsonFire, JsonElement::getAsBoolean, Fire.Builder.DEFAULT_INVERT_HEAL_AND_HARM))
      .removeComponents(Fire.Component.CAMPFIRE_ITEM, Fire.Component.LANTERN_ITEM, Fire.Component.TORCH_ITEM, Fire.Component.FLAME_PARTICLE);
    removeOrSet(fireTypeString, builder, jsonFire, SOURCE_FIELD_NAME, Fire.Component.SOURCE_BLOCK);
    removeOrSet(fireTypeString, builder, jsonFire, CAMPFIRE_FIELD_NAME, Fire.Component.CAMPFIRE_BLOCK);
    removeOrSet(fireTypeString, builder, jsonFire, LANTERN_FIELD_NAME, Fire.Component.LANTERN_BLOCK);
    removeOrSet(fireTypeString, builder, jsonFire, TORCH_FIELD_NAME, Fire.Component.TORCH_BLOCK);
    removeOrSet(fireTypeString, builder, jsonFire, WALL_TORCH_FIELD_NAME, Fire.Component.WALL_TORCH_BLOCK);
    registerFire(fireType, builder.build());
  }

  /**
   * Either removes the specified component or sets its value to the provided reference.
   *
   * @param fireType Fire Type.
   * @param builder {@link Fire.Builder}.
   * @param data {@link JsonObject} with data to parse.
   * @param field field to parse.
   * @param component {@link Fire.Component} to set.
   */
  private static void removeOrSet(String fireType, Fire.Builder builder, JsonObject data, String field, Fire.Component<?, ?> component) {
    if (data.has(field)) {
      JsonElement element = data.get(field);
      List<String> list = element.isJsonArray() ? element.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList() : List.of(element.getAsString());
      if (list.isEmpty() || list.getFirst().equals("remove")) {
        builder.removeComponent(component);
      } else {
        builder.setComponent(component, list.stream().map(ResourceLocation::parse).toArray(ResourceLocation[]::new));
      }
    }
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> fires, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
    unregisterFires();
    for (Map.Entry<ResourceLocation, JsonElement> fire : fires.entrySet()) {
      String jsonIdentifier = fire.getKey().getPath();
      try {
        JsonObject jsonData = getJsonObject(jsonIdentifier, fire.getValue());
        String mod = parse(jsonIdentifier, "mod", jsonData, JsonElement::getAsString);
        if (Services.PLATFORM.isModLoaded(mod)) {
          parse(jsonIdentifier, "fires", jsonData, JsonElement::getAsJsonArray).forEach(element -> registerFire(getJsonObject(jsonIdentifier, element), mod, jsonIdentifier));
        } else {
          Constants.LOGGER.warn("Registering of ddfires for [{}] is canceled: {} is not loaded.", mod, mod);
        }
      } catch (NullPointerException | UnsupportedOperationException | IllegalStateException | NumberFormatException e) {
        Constants.LOGGER.error("Registering of ddfires for [{}] is canceled.", jsonIdentifier);
      }
    }
  }
}

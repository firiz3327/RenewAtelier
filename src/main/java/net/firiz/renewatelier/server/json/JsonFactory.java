package net.firiz.renewatelier.server.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.alchemy.recipe.AlchemyRecipe;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.utils.CommonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.Objects;

public class JsonFactory implements TypeAdapterFactory {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapterFactory(new JsonFactory()).create();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        final Class<T> rawType = CommonUtils.cast(typeToken.getRawType());
        if (rawType == AlchemyMaterial.class) {
            return CommonUtils.cast(StringTypeAdapter.createAdapter(
                    gson,
                    AlchemyMaterial::id,
                    AlchemyMaterial::getMaterial
            ));
        } else if (rawType == AlchemyRecipe.class) {
            return CommonUtils.cast(StringTypeAdapter.createAdapter(
                    gson,
                    AlchemyRecipe::getId,
                    AlchemyRecipe::search
            ));
        } else if (rawType == Characteristic.class) {
            return CommonUtils.cast(StringTypeAdapter.createAdapter(
                    gson,
                    Characteristic::getId,
                    Characteristic::getCharacteristic
            ));
        } else if (rawType == Material.class) {
            return CommonUtils.cast(ItemTypeAdapter.createAdapter(
                    gson,
                    (jsonObject, o) -> {
                        jsonObject.add("version", new JsonPrimitive(Bukkit.getUnsafe().getDataVersion()));
                        jsonObject.add("type", new JsonPrimitive(o.name()));
                    },
                    jsonObject -> {
                        final int version = jsonObject.get("version").getAsInt();
                        final String typeName = jsonObject.get("type").getAsString();
                        Material type;
                        if (version < 0) {
                            type = Material.getMaterial("LEGACY_" + typeName);
                            type = Bukkit.getUnsafe().fromLegacy(new MaterialData(type), true);
                        } else {
                            type = Bukkit.getUnsafe().getMaterial(typeName, version);
                        }
                        return Objects.requireNonNullElse(type, Material.AIR);
                    }
            ));
        }
        return null;
    }

}

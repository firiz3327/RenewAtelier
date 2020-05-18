package net.firiz.renewatelier.item.json;

import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.utils.Chore;

class AlchemyItemStatusAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> ItemTypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        final Class<T> rawType = Chore.cast(typeToken.getRawType());
        if (rawType == AlchemyMaterial.class) {
            return Chore.cast(ItemTypeAdapter.createAdapter(
                    gson,
                    (jsonObject, t) -> jsonObject.add("id", new JsonPrimitive(t.getId())),
                    jsonObject -> AlchemyMaterial.getMaterial(jsonObject.get("id").getAsString())
            ));
        }
        return null;
    }

}

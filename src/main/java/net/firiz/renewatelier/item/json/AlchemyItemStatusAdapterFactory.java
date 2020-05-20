package net.firiz.renewatelier.item.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.firiz.renewatelier.alchemy.material.AlchemyMaterial;
import net.firiz.renewatelier.characteristic.Characteristic;
import net.firiz.renewatelier.utils.Chore;

class AlchemyItemStatusAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        final Class<T> rawType = Chore.cast(typeToken.getRawType());
        if (rawType == AlchemyMaterial.class) {
            return Chore.cast(StringTypeAdapter.createAdapter(
                    gson,
                    AlchemyMaterial::getId,
                    AlchemyMaterial::getMaterial
            ));
        } else if (rawType == Characteristic.class) {
            return Chore.cast(StringTypeAdapter.createAdapter(
                    gson,
                    Characteristic::getId,
                    Characteristic::search
            ));
        }
        return null;
    }

}

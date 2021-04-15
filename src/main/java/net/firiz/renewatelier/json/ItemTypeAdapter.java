package net.firiz.renewatelier.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

class ItemTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter<JsonElement> elementAdapter;
    private final BiConsumer<JsonObject, T> serializer;
    private final Function<JsonObject, T> deserializer;

    public static <T> ItemTypeAdapter<T> createAdapter(
            @NotNull final Gson gson,
            @NotNull final BiConsumer<JsonObject, T> write,
            @NotNull final Function<JsonObject, T> read
    ) {
        return new ItemTypeAdapter<>(gson, write, read);
    }

    private ItemTypeAdapter(Gson gson, BiConsumer<JsonObject, T> serializer, Function<JsonObject, T> deserializer) {
        this.elementAdapter = gson.getAdapter(JsonElement.class);
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void write(JsonWriter jsonWriter, T t) throws IOException {
        final JsonObject result = new JsonObject();
        serializer.accept(result, t);
        elementAdapter.write(jsonWriter, result);
    }

    @Override
    public T read(JsonReader jsonReader) throws IOException {
        return deserializer.apply(elementAdapter.read(jsonReader).getAsJsonObject());
    }
}

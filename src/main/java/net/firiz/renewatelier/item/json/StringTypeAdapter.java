package net.firiz.renewatelier.item.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Function;

class StringTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter<String> elementAdapter;
    private final Function<T, String> serializer;
    private final Function<String, T> deserializer;

    public static <T> StringTypeAdapter<T> createAdapter(
            @NotNull final Gson gson,
            @NotNull final Function<T, String> write,
            @NotNull final Function<String, T> read
    ) {
        return new StringTypeAdapter<>(gson, write, read);
    }

    private StringTypeAdapter(Gson gson, Function<T, String> serializer, Function<String, T> deserializer) {
        this.elementAdapter = gson.getAdapter(String.class);
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void write(JsonWriter jsonWriter, T t) throws IOException {
        elementAdapter.write(jsonWriter, serializer.apply(t));
    }

    @Override
    public T read(JsonReader jsonReader) throws IOException {
        return deserializer.apply(elementAdapter.read(jsonReader));
    }
}

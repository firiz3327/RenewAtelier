package net.firiz.renewatelier.tools;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.*;
import java.util.*;

public class LanguageJPPack {

    public static void main(String[] args) throws IOException {
        System.out.println("Language JP Pack");
        final File assets = new File(System.getenv("APPDATA") + "\\.minecraft\\assets");
        final File indexesFolder = new File(assets, "indexes");
        final List<File> indexes = Arrays.asList(Objects.requireNonNull(indexesFolder.listFiles()));

        final List<String> strings = new ObjectArrayList<>();
        indexes.forEach(file -> strings.add(file.getName()));
        System.out.println("Target: " + strings);

        try (final BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Input: ");
            final String input = br.readLine();
            final Optional<File> first = indexes.stream().filter(file -> file.getName().contains(input)).findFirst();
            if (first.isPresent()) {
                final File file = first.get();
                System.out.println(file);
                final Gson gson = new Gson();
                try (final FileReader reader = new FileReader(file)) {
                    final JsonObject objects = gson.fromJson(reader, JsonObject.class).get("objects").getAsJsonObject();
                    final Optional<Map.Entry<String, JsonElement>> jaEntry = objects.entrySet().stream().filter(entry -> entry.getKey().equals("minecraft/lang/ja_jp.json")).findFirst();
                    if (jaEntry.isPresent()) {
                        final String hash = jaEntry.get().getValue().getAsJsonObject().get("hash").getAsString();
                        System.out.println(hash);
                        final File objectsFolder = new File(assets, "objects");
                        for (final File folder : Objects.requireNonNull(objectsFolder.listFiles())) {
                            if (folder.isDirectory()) {
                                for (File hashFile : Objects.requireNonNull(folder.listFiles())) {
                                    if (hashFile.getName().equals(hash)) {
                                        final StringBuilder sb = new StringBuilder();
                                        try (final FileReader hashFileReader = new FileReader(hashFile);
                                             final BufferedReader hashBr = new BufferedReader(hashFileReader)) {
                                            String str;
                                            while ((str = hashBr.readLine()) != null) {
                                                sb.append(str);
                                            }
                                        }
                                        item(gson.fromJson(sb.toString(), JsonObject.class));
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("empty ja_jp lang");
                    }
                }
            } else {
                System.out.println("empty");
            }
        }
    }

    private static void item(JsonObject json) {
        int i = 0;
        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().startsWith("block.minecraft.") || entry.getKey().startsWith("item.minecraft.")) {
                System.out.println("E" + i + "(\"" + entry.getKey() + "\", \"" + entry.getValue().getAsString() + "\"),");
                i++;
            }
        }
    }

}

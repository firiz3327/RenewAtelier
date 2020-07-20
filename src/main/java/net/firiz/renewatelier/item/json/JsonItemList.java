package net.firiz.renewatelier.item.json;

import com.google.gson.annotations.Expose;

import java.util.List;

public class JsonItemList {

    @Expose
    private final List<JsonItem> jsonItems;

    public JsonItemList(List<JsonItem> jsonItems) {
        this.jsonItems = jsonItems;
    }

    public List<JsonItem> getJsonItems() {
        return jsonItems;
    }

    public String toJson() {
        return JsonFactory.toJson(this);
    }

    public static JsonItemList fromJson(String json) {
        return JsonFactory.fromJson(json, JsonItemList.class);
    }

}
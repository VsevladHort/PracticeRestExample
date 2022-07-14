package entities.utils;

import entities.Good;
import entities.GoodGroup;
import org.json.JSONObject;

public final class GoodJsonConverter {
    private GoodJsonConverter() {
    }

    public static String toJson(Good good) {
        JSONObject jsonObject = new JSONObject();
        constructJsonGood(good, jsonObject);
        return jsonObject.toString();
    }

    public static String toJson(Good good, String group) {
        JSONObject jsonObject = new JSONObject();
        constructJsonGood(good, jsonObject);
        jsonObject.put("group", group);
        return jsonObject.toString();
    }

    private static void constructJsonGood(Good good, JSONObject jsonObject) {
        jsonObject.put("name", good.getName());
        jsonObject.put("description", good.getDescription());
        jsonObject.put("producer", good.getProducer());
        jsonObject.put("price", good.getPrice());
        jsonObject.put("amount", good.getAmount());
    }

    public static String toJson(GoodGroup group) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", group.getName());
        jsonObject.put("description", group.getDescription());
        return jsonObject.toString();
    }

    public static Good fromJsonGood(String json) {
        JSONObject jsonObject = new JSONObject(json);
        Good good = new Good(jsonObject.getString("name"));
        good.setDescription(jsonObject.getString("description"));
        good.setProducer(jsonObject.getString("producer"));
        good.setPrice(jsonObject.getDouble("price"));
        good.setAmount(jsonObject.getInt("amount"));
        return good;
    }

    public static GoodGroup fromJsonGroup(String json) {
        JSONObject jsonObject = new JSONObject(json);
        return new GoodGroup(jsonObject.getString("name"), jsonObject.getString("description"));
    }
}

package com.jdp30.moodlighting2.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.jdp30.moodlighting2.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PresetDeserializer implements JsonDeserializer<Preset> {
    @Override
    public Preset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            String id = obj.get("id").getAsString();
            String name = obj.get("name").getAsString();
            if (type.equalsIgnoreCase("fade")) {
                double pauseTime = obj.get("pauseTime").getAsDouble();
                double fadeTime = obj.get("fadeTime").getAsDouble();
                ArrayList<Integer> colorInts = new ArrayList<Integer>();
                JsonArray colors = obj.getAsJsonArray("colours");
                for (JsonElement o : colors) {
                    if (o.isJsonPrimitive()) {
                        JsonPrimitive p = o.getAsJsonPrimitive();
                        String c = p.getAsString();
                        int colValue = Util.colorStringToInt(c);
                        colorInts.add(colValue);
                    }
                }
                return new FadePreset(name, id, pauseTime, fadeTime, colorInts);
            }
        }
        return null;
    }
}

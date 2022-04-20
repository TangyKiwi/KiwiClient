package com.tangykiwi.kiwiclient.modules.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public interface SettingDataHandler<T> {

    JsonElement write(T value);

    T read(JsonElement json) throws JsonParseException;

    default T readOrNull(JsonElement json) {
        try {
            return read(json);
        } catch (JsonParseException | IllegalStateException | UnsupportedOperationException e) {
            return null;
        }
    }

    SettingDataHandler<Boolean> BOOLEAN = new SettingDataHandler<>() {
        public JsonElement write(Boolean value) {
            return new JsonPrimitive(value);
        }

        public Boolean read(JsonElement json) {
            return json.getAsBoolean();
        }
    };

    SettingDataHandler<Integer> INTEGER = new SettingDataHandler<>() {
        public JsonElement write(Integer value) {
            return new JsonPrimitive(value);
        }

        public Integer read(JsonElement json) {
            return json.getAsInt();
        }
    };

    SettingDataHandler<Double> DOUBLE = new SettingDataHandler<>() {
        public JsonElement write(Double value) {
            return new JsonPrimitive(value);
        }

        public Double read(JsonElement json) {
            return json.getAsDouble();
        }
    };

}

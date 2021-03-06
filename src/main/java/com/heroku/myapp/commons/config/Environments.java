package com.heroku.myapp.commons.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.heroku.myapp.commons.exceptions.SettingNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public enum Environments {
    ENV, IRON;
    private final String path;
    private Map<String, String> map;

    private Environments() {
        this.path = "/config/" + this.name().toLowerCase(Locale.US) + ".json";
    }

    public String get(String key) {
        return this.getOr(key, key);
    }

    public String getOr(String key1, String key2) {
        return Optional.ofNullable(System.getenv(key1))
                .orElseGet(() -> Optional.ofNullable(map().get(key2))
                        .orElseThrow(() -> new SettingNotFoundException()));
    }

    private Map<String, String> map() {
        return Optional.ofNullable(map).orElseGet(() -> loadResource());
    }

    private Map<String, String> loadResource() {
        try {
            InputStream is = ClassLoader.class.getResourceAsStream(path);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            map = new Gson().fromJson(new JsonReader(isr), Map.class);
            return map;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException();
        }
    }
}

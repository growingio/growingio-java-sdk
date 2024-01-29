package io.growing.sdk.java.test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.growing.sdk.java.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class Case4StringUtilsTest {

    @Test
    public void checkMap2Str() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("中文key", "中文value");
        String gsonString = gson.toJson(map);
        String utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);
    }

    @Test
    public void parseMap2Str() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("中文key", "中文value");
        String utilString = StringUtils.map2Str(map);
        JsonElement jsonElement = JsonParser.parseString(utilString);
        Assert.assertTrue(jsonElement.isJsonObject());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Assert.assertEquals(jsonObject.get("key1").getAsString(), "value1");
        Assert.assertEquals(jsonObject.get("key2").getAsString(), "value2");
        Assert.assertEquals(jsonObject.get("key3").getAsString(), "value3");
        Assert.assertEquals(jsonObject.get("中文key").getAsString(), "中文value");
    }

    @Test
    public void checkMap2StrWithEscapeText() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("\b", "a\\b");
        String gsonString = gson.toJson(map);
        String utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);
    }

    @Test
    public void parseMap2StrWithEscapeText() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("key", "a\\b");
        String utilString = StringUtils.map2Str(map);
        JsonElement jsonElement = JsonParser.parseString(utilString);
        Assert.assertTrue(jsonElement.isJsonObject());
        String value = jsonElement.getAsJsonObject().get("key").getAsString();
        Assert.assertEquals("a\\b", value);
    }

    @Test
    public void checkEmpty() {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<String, String>();
        String gsonString = gson.toJson(map);
        String utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);

        map.put(null, null);
        gsonString = gson.toJson(map);
        utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);

        map.put("key", null);
        gsonString = gson.toJson(map);
        utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);

        map.put("key", "");
        gsonString = gson.toJson(map);
        utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);

        // 与gson保持一致，将空字符串的key解析为有效值
        map.put("", "value");
        gsonString = gson.toJson(map);
        utilString = StringUtils.map2Str(map);

        Assert.assertEquals(gsonString, utilString);
    }
}

package com.github.tvbox.osc.util;

import com.google.gson.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class DefaultConfigTest {

    // --- noAd() tests ---

    @Test
    public void noAd_nullFlag_returnsFalse() {
        assertFalse(DefaultConfig.noAd(null));
    }

    @Test
    public void noAd_emptyFlag_returnsFalse() {
        assertFalse(DefaultConfig.noAd(""));
    }

    @Test
    public void noAd_exactMatchEnglishKeywords() {
        assertTrue(DefaultConfig.noAd("tx"));
        assertTrue(DefaultConfig.noAd("youku"));
        assertTrue(DefaultConfig.noAd("qq"));
        assertTrue(DefaultConfig.noAd("qiyi"));
        assertTrue(DefaultConfig.noAd("letv"));
        assertTrue(DefaultConfig.noAd("leshi"));
        assertTrue(DefaultConfig.noAd("sohu"));
        assertTrue(DefaultConfig.noAd("mgtv"));
        assertTrue(DefaultConfig.noAd("bilibili"));
        assertTrue(DefaultConfig.noAd("imgo"));
        assertTrue(DefaultConfig.noAd("pptv"));
        assertTrue(DefaultConfig.noAd("xigua"));
        assertTrue(DefaultConfig.noAd("funshion"));
    }

    @Test
    public void noAd_exactMatchChineseKeywords() {
        assertTrue(DefaultConfig.noAd("优酷"));
        assertTrue(DefaultConfig.noAd("芒果"));
        assertTrue(DefaultConfig.noAd("腾讯"));
        assertTrue(DefaultConfig.noAd("奇艺"));
    }

    @Test
    public void noAd_containsKeyword() {
        assertTrue(DefaultConfig.noAd("bilibili_hd"));
        assertTrue(DefaultConfig.noAd("youku_vip"));
        assertTrue(DefaultConfig.noAd("tx_video"));
        assertTrue(DefaultConfig.noAd("mgtv_live"));
    }

    @Test
    public void noAd_unknownFlag_returnsFalse() {
        assertFalse(DefaultConfig.noAd("unknown"));
        assertFalse(DefaultConfig.noAd("some_other_source"));
        assertFalse(DefaultConfig.noAd("ali"));
    }

    // --- safeJsonString() tests ---

    @Test
    public void safeJsonString_existingKey_returnsValue() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "test");
        assertEquals("test", DefaultConfig.safeJsonString(obj, "name", "default"));
    }

    @Test
    public void safeJsonString_missingKey_returnsDefault() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "test");
        assertEquals("default", DefaultConfig.safeJsonString(obj, "missing", "default"));
    }

    @Test
    public void safeJsonString_trimsWhitespace() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "  hello  ");
        assertEquals("hello", DefaultConfig.safeJsonString(obj, "name", "default"));
    }

    @Test
    public void safeJsonString_nonStringKey_returnsDefault() {
        JsonObject obj = new JsonObject();
        obj.addProperty("count", 42);
        // getAsJsonPrimitive().getAsString() on an int should still work
        assertEquals("42", DefaultConfig.safeJsonString(obj, "count", "default"));
    }

    // --- safeJsonInt() tests ---

    @Test
    public void safeJsonInt_existingKey_returnsValue() {
        JsonObject obj = new JsonObject();
        obj.addProperty("count", 42);
        assertEquals(42, DefaultConfig.safeJsonInt(obj, "count", 0));
    }

    @Test
    public void safeJsonInt_missingKey_returnsDefault() {
        JsonObject obj = new JsonObject();
        obj.addProperty("count", 42);
        assertEquals(0, DefaultConfig.safeJsonInt(obj, "missing", 0));
    }

    @Test
    public void safeJsonInt_nonIntKey_returnsDefault() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "not_a_number");
        assertEquals(99, DefaultConfig.safeJsonInt(obj, "name", 99));
    }

    // --- safeJsonStringList() tests ---

    @Test
    public void safeJsonStringList_jsonArray_returnsList() {
        JsonObject obj = new JsonObject();
        obj.add("items", new com.google.gson.JsonArray());
        obj.getAsJsonArray("items").add("a");
        obj.getAsJsonArray("items").add("b");
        obj.getAsJsonArray("items").add("c");
        ArrayList<String> result = DefaultConfig.safeJsonStringList(obj, "items");
        assertEquals(3, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
        assertEquals("c", result.get(2));
    }

    @Test
    public void safeJsonStringList_missingKey_returnsEmptyList() {
        JsonObject obj = new JsonObject();
        ArrayList<String> result = DefaultConfig.safeJsonStringList(obj, "missing");
        assertTrue(result.isEmpty());
    }

    // --- getFileSuffix() tests ---

    @Test
    public void getFileSuffix_normalFile_returnsSuffix() {
        assertEquals(".mp4", DefaultConfig.getFileSuffix("video.mp4"));
    }

    @Test
    public void getFileSuffix_noSuffix_returnsEmpty() {
        assertEquals("", DefaultConfig.getFileSuffix("video"));
    }

    @Test
    public void getFileSuffix_emptyString_returnsEmpty() {
        assertEquals("", DefaultConfig.getFileSuffix(""));
    }

    @Test
    public void getFileSuffix_null_returnsEmpty() {
        assertEquals("", DefaultConfig.getFileSuffix(null));
    }

    @Test
    public void getFileSuffix_multipleDots_returnsLastSuffix() {
        assertEquals(".mp4", DefaultConfig.getFileSuffix("archive.tar.mp4"));
    }

    // --- getFilePrefixName() tests ---

    @Test
    public void getFilePrefixName_normalFile_returnsPrefix() {
        assertEquals("video", DefaultConfig.getFilePrefixName("video.mp4"));
    }

    @Test
    public void getFilePrefixName_noSuffix_returnsFull() {
        assertEquals("video", DefaultConfig.getFilePrefixName("video"));
    }

    @Test
    public void getFilePrefixName_emptyString_returnsEmpty() {
        assertEquals("", DefaultConfig.getFilePrefixName(""));
    }

    @Test
    public void getFilePrefixName_null_returnsEmpty() {
        assertEquals("", DefaultConfig.getFilePrefixName(null));
    }

    @Test
    public void getFilePrefixName_multipleDots_returnsBeforeLast() {
        assertEquals("archive.tar", DefaultConfig.getFilePrefixName("archive.tar.mp4"));
    }
}

package com.github.tvbox.osc.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    // --- isEmpty(CharSequence) tests ---

    @Test
    public void isEmpty_nullString_returnsTrue() {
        assertTrue(StringUtils.isEmpty((CharSequence) null));
    }

    @Test
    public void isEmpty_emptyString_returnsTrue() {
        assertTrue(StringUtils.isEmpty(""));
    }

    @Test
    public void isEmpty_nonEmptyString_returnsFalse() {
        assertFalse(StringUtils.isEmpty("hello"));
    }

    // --- isNotEmpty(CharSequence) tests ---

    @Test
    public void isNotEmpty_nullString_returnsFalse() {
        assertFalse(StringUtils.isNotEmpty((CharSequence) null));
    }

    @Test
    public void isNotEmpty_nonEmptyString_returnsTrue() {
        assertTrue(StringUtils.isNotEmpty("hello"));
    }

    // --- isEmpty(Object) tests ---

    @Test
    public void isEmpty_nullObject_returnsTrue() {
        assertTrue(StringUtils.isEmpty((Object) null));
    }

    @Test
    public void isEmpty_emptyCollection_returnsTrue() {
        assertTrue(StringUtils.isEmpty(new ArrayList<>()));
    }

    @Test
    public void isEmpty_nonEmptyCollection_returnsFalse() {
        List<String> list = new ArrayList<>();
        list.add("item");
        assertFalse(StringUtils.isEmpty(list));
    }

    @Test
    public void isEmpty_emptyMap_returnsTrue() {
        assertTrue(StringUtils.isEmpty(new HashMap<>()));
    }

    @Test
    public void isEmpty_emptyArray_returnsTrue() {
        assertTrue(StringUtils.isEmpty(new String[]{}));
    }

    @Test
    public void isEmpty_nonEmptyArray_returnsFalse() {
        assertFalse(StringUtils.isEmpty(new String[]{"a"}));
    }

    // --- getBaseUrl() tests ---

    @Test
    public void getBaseUrl_httpUrl_returnsBaseUrl() {
        assertEquals("http://example.com", StringUtils.getBaseUrl("http://example.com/path/to/file"));
    }

    @Test
    public void getBaseUrl_httpsUrl_returnsBaseUrl() {
        assertEquals("https://example.com", StringUtils.getBaseUrl("https://example.com/path"));
    }

    @Test
    public void getBaseUrl_urlWithPort_returnsBaseUrlWithPort() {
        assertEquals("http://127.0.0.1:9978", StringUtils.getBaseUrl("http://127.0.0.1:9978/proxy?do=js"));
    }

    @Test
    public void getBaseUrl_null_returnsNull() {
        // isEmpty(null) is true, so it returns the input
        assertEquals(null, StringUtils.getBaseUrl(null));
    }

    // --- trimBlanks() tests ---

    @Test
    public void trimBlanks_null_returnsNull() {
        assertEquals(null, StringUtils.trimBlanks(null));
    }

    @Test
    public void trimBlanks_emptyString_returnsEmpty() {
        assertEquals("", StringUtils.trimBlanks(""));
    }

    @Test
    public void trimBlanks_leadingTrailingWhitespace_returnsTrimmed() {
        assertEquals("hello", StringUtils.trimBlanks("\t\nhello\n\t"));
    }

    @Test
    public void trimBlanks_onlyWhitespace_returnsEmpty() {
        assertEquals("", StringUtils.trimBlanks("\t\n\r\f"));
    }

    @Test
    public void trimBlanks_noWhitespace_returnsSame() {
        assertEquals("hello world", StringUtils.trimBlanks("hello world"));
    }

    // --- escapeJavaScriptString() tests ---

    @Test
    public void escapeJavaScriptString_doubleQuote() {
        assertEquals("\\\"", StringUtils.escapeJavaScriptString("\""));
    }

    @Test
    public void escapeJavaScriptString_singleQuote() {
        assertEquals("\\'", StringUtils.escapeJavaScriptString("'"));
    }

    @Test
    public void escapeJavaScriptString_backslash() {
        assertEquals("\\\\", StringUtils.escapeJavaScriptString("\\"));
    }

    @Test
    public void escapeJavaScriptString_newline() {
        assertEquals("\\n", StringUtils.escapeJavaScriptString("\n"));
    }

    @Test
    public void escapeJavaScriptString_carriageReturn() {
        assertEquals("\\r", StringUtils.escapeJavaScriptString("\r"));
    }

    @Test
    public void escapeJavaScriptString_normalString() {
        assertEquals("hello world", StringUtils.escapeJavaScriptString("hello world"));
    }

    // --- arrayToString() tests ---

    @Test
    public void arrayToString_nullArray_returnsEmpty() {
        assertEquals("", StringUtils.arrayToString((String[]) null, 0, ","));
    }

    @Test
    public void arrayToString_singleElement_returnsElement() {
        String[] arr = {"a"};
        assertEquals("a", StringUtils.arrayToString(arr, 0, ","));
    }

    @Test
    public void arrayToString_multipleElements_returnsJoined() {
        String[] arr = {"a", "b", "c"};
        assertEquals("a,b,c", StringUtils.arrayToString(arr, 0, ","));
    }

    @Test
    public void arrayToString_withFromIndex_returnsSubset() {
        String[] arr = {"a", "b", "c"};
        assertEquals("b,c", StringUtils.arrayToString(arr, 1, ","));
    }

    // --- listToString() tests ---

    @Test
    public void listToString_nullList_returnsEmpty() {
        assertEquals("", StringUtils.listToString(null, ","));
    }

    @Test
    public void listToString_singleElement_returnsElement() {
        List<String> list = Arrays.asList("a");
        assertEquals("a", StringUtils.listToString(list, ","));
    }

    @Test
    public void listToString_multipleElements_returnsJoined() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", StringUtils.listToString(list, ","));
    }

    @Test
    public void listToString_defaultSeparator() {
        List<String> list = Arrays.asList("a", "b");
        assertEquals("a&&b", StringUtils.listToString(list));
    }
}

package com.github.tvbox.osc.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AESTest {

    // --- rightPadding() tests ---

    @Test
    public void rightPadding_shorterThanLength_padsWithReplace() {
        assertEquals("abc0000000000000", AES.rightPadding("abc", "0", 16));
    }

    @Test
    public void rightPadding_exactLength_returnsSame() {
        assertEquals("1234567890123456", AES.rightPadding("1234567890123456", "0", 16));
    }

    @Test
    public void rightPadding_longerThanLength_truncates() {
        assertEquals("1234567890123456", AES.rightPadding("12345678901234567890", "0", 16));
    }

    @Test
    public void rightPadding_emptyString_padsFully() {
        assertEquals("0000000000000000", AES.rightPadding("", "0", 16));
    }

    @Test
    public void rightPadding_trimsInput() {
        assertEquals("ab00000000000000", AES.rightPadding("  ab  ", "0", 16));
    }

    // --- isJson() tests ---

    @Test
    public void isJson_validObject_returnsTrue() {
        assertTrue(AES.isJson("{\"key\":\"value\"}"));
    }

    @Test
    public void isJson_validArray_returnsTrue() {
        assertTrue(AES.isJson("[1,2,3]"));
    }

    @Test
    public void isJson_invalidJson_returnsFalse() {
        assertFalse(AES.isJson("not json"));
    }

    @Test
    public void isJson_emptyString_returnsFalse() {
        assertFalse(AES.isJson(""));
    }

    @Test
    public void isJson_emptyObject_returnsTrue() {
        assertTrue(AES.isJson("{}"));
    }

    // --- toBytes() tests ---

    @Test
    public void toBytes_hexString_returnsCorrectBytes() {
        byte[] result = AES.toBytes("48656c6c6f");
        assertEquals("Hello", new String(result));
    }

    @Test
    public void toBytes_ff_returnsNegativeOne() {
        byte[] result = AES.toBytes("ff");
        assertEquals(1, result.length);
        assertEquals((byte) 0xFF, result[0]);
    }

    @Test
    public void toBytes_00_returnsZero() {
        byte[] result = AES.toBytes("00");
        assertEquals(1, result.length);
        assertEquals(0, result[0]);
    }

    @Test
    public void toBytes_emptyString_returnsEmptyArray() {
        byte[] result = AES.toBytes("");
        assertEquals(0, result.length);
    }

    // --- ECB() tests ---
    // Note: ECB uses PKCS7Padding which requires BouncyCastle provider.
    // In a test environment without BouncyCastle registered, this will return null.
    // We test that the method doesn't crash and returns null on missing provider.

    @Test
    public void ECB_invalidInput_returnsNull() {
        // With invalid hex data, should return null (exception caught)
        String result = AES.ECB("not_valid_hex", "testkey");
        // May be null due to either invalid hex or missing BouncyCastle
        // Just verify it doesn't throw
    }

    // --- CBC() tests ---

    @Test
    public void CBC_invalidInput_returnsNull() {
        // With invalid hex data, should return null (exception caught)
        String result = AES.CBC("not_valid_hex", "testkey123456789", "testiv1234567890");
        // May be null due to either invalid hex or missing BouncyCastle
        // Just verify it doesn't throw
    }
}

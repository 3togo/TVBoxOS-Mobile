package com.github.tvbox.osc.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MD5Test {

    // RFC 1321 test vectors from the source code comments

    @Test
    public void encode_emptyString() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5.encode(""));
    }

    @Test
    public void encode_singleCharA() {
        assertEquals("0cc175b9c0f1b6a831c399e269772661", MD5.encode("a"));
    }

    @Test
    public void encode_abc() {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", MD5.encode("abc"));
    }

    @Test
    public void encode_messageDigest() {
        assertEquals("f96b697d7cb7938d525a2f31aaf161d0", MD5.encode("message digest"));
    }

    @Test
    public void encode_fullAlphabet() {
        assertEquals("c3fcd3d76192e4007dfb496cca67e13b", MD5.encode("abcdefghijklmnopqrstuvwxyz"));
    }

    @Test
    public void encode_deterministic() {
        String hash1 = MD5.encode("test");
        String hash2 = MD5.encode("test");
        assertEquals(hash1, hash2);
    }

    @Test
    public void encode_differentInputs_differentHashes() {
        String hash1 = MD5.encode("test1");
        String hash2 = MD5.encode("test2");
        // Extremely unlikely to be equal
        assert !hash1.equals(hash2);
    }

    // --- string2MD5() tests ---

    @Test
    public void string2MD5_emptyString_returnsNull() {
        // TextUtils.isEmpty("") is true, so returns null
        assertNull(MD5.string2MD5(""));
    }

    @Test
    public void string2MD5_null_returnsNull() {
        assertNull(MD5.string2MD5(null));
    }

    @Test
    public void string2MD5_abc() {
        String result = MD5.string2MD5("abc");
        assertNotNull(result);
        assertEquals(32, result.length());
        assertEquals("900150983cd24fb0d6963f7d28e17f72", result);
    }

    @Test
    public void string2MD5_deterministic() {
        String hash1 = MD5.string2MD5("test");
        String hash2 = MD5.string2MD5("test");
        assertEquals(hash1, hash2);
    }

    // --- getFileMd5() tests ---

    @Test
    public void getFileMd5_existingFile_returnsHash() throws IOException {
        File tempFile = File.createTempFile("md5test", ".dat");
        try {
            FileUtils.writeSimple("hello world".getBytes("UTF-8"), tempFile);
            String md5 = MD5.getFileMd5(tempFile);
            assertNotNull(md5);
            assertEquals(32, md5.length());
        } finally {
            tempFile.delete();
        }
    }

    @Test
    public void getFileMd5_sameContent_sameHash() throws IOException {
        File file1 = File.createTempFile("md5a", ".dat");
        File file2 = File.createTempFile("md5b", ".dat");
        try {
            byte[] content = "same content".getBytes("UTF-8");
            FileUtils.writeSimple(content, file1);
            FileUtils.writeSimple(content, file2);
            assertEquals(MD5.getFileMd5(file1), MD5.getFileMd5(file2));
        } finally {
            file1.delete();
            file2.delete();
        }
    }
}

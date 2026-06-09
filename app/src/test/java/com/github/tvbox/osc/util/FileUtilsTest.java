package com.github.tvbox.osc.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FileUtilsTest {

    // --- getFormatSize() tests ---

    @Test
    public void getFormatSize_zeroBytes_returns0K() {
        assertEquals("0K", FileUtils.getFormatSize(0));
    }

    @Test
    public void getFormatSize_lessThan1KB_returns0K() {
        assertEquals("0K", FileUtils.getFormatSize(512));
    }

    @Test
    public void getFormatSize_1KB_returnsKB() {
        assertEquals("1.00KB", FileUtils.getFormatSize(1024));
    }

    @Test
    public void getFormatSize_1MB_returnsMB() {
        assertEquals("1.00MB", FileUtils.getFormatSize(1024 * 1024));
    }

    @Test
    public void getFormatSize_1GB_returnsGB() {
        assertEquals("1.00GB", FileUtils.getFormatSize(1024L * 1024 * 1024));
    }

    @Test
    public void getFormatSize_1TB_returnsTB() {
        assertEquals("1.00TB", FileUtils.getFormatSize(1024L * 1024 * 1024 * 1024));
    }

    @Test
    public void getFormatSize_1500KB_returnsMB() {
        // 1500KB = 1.46484375MB
        String result = FileUtils.getFormatSize(1500 * 1024);
        assertTrue(result.endsWith("MB"));
    }

    // --- byteMerger() tests ---

    @Test
    public void byteMerger_twoArrays_returnsMerged() {
        byte[] a = {1, 2, 3};
        byte[] b = {4, 5, 6};
        byte[] result = FileUtils.byteMerger(a, b);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6}, result);
    }

    @Test
    public void byteMerger_emptyFirstArray_returnsSecond() {
        byte[] a = {};
        byte[] b = {4, 5};
        byte[] result = FileUtils.byteMerger(a, b);
        assertArrayEquals(new byte[]{4, 5}, result);
    }

    @Test
    public void byteMerger_emptySecondArray_returnsFirst() {
        byte[] a = {1, 2};
        byte[] b = {};
        byte[] result = FileUtils.byteMerger(a, b);
        assertArrayEquals(new byte[]{1, 2}, result);
    }

    // --- getFileName() tests ---

    @Test
    public void getFileName_withPath_returnsFileName() {
        assertEquals("video.mp4", FileUtils.getFileName("/path/to/video.mp4"));
    }

    @Test
    public void getFileName_noPath_returnsSame() {
        assertEquals("video.mp4", FileUtils.getFileName("video.mp4"));
    }

    @Test
    public void getFileName_emptyString_returnsEmpty() {
        assertEquals("", FileUtils.getFileName(""));
    }

    @Test
    public void getFileName_null_returnsEmpty() {
        assertEquals("", FileUtils.getFileName(null));
    }

    // --- getFileExt() tests ---

    @Test
    public void getFileExt_normalFile_returnsLowercaseExt() {
        assertEquals(".mp4", FileUtils.getFileExt("video.MP4"));
    }

    @Test
    public void getFileExt_noExt_returnsEmpty() {
        assertEquals("", FileUtils.getFileExt("video"));
    }

    @Test
    public void getFileExt_emptyString_returnsEmpty() {
        assertEquals("", FileUtils.getFileExt(""));
    }

    @Test
    public void getFileExt_null_returnsEmpty() {
        assertEquals("", FileUtils.getFileExt(null));
    }

    // --- hasExtension() tests ---

    @Test
    public void hasExtension_normalPath_returnsTrue() {
        assertTrue(FileUtils.hasExtension("/path/to/video.mp4"));
    }

    @Test
    public void hasExtension_noExtension_returnsFalse() {
        assertFalse(FileUtils.hasExtension("/path/to/video"));
    }

    @Test
    public void hasExtension_dotInDirName_noExtAfterSlash_returnsFalse() {
        assertFalse(FileUtils.hasExtension("/path.v2/video"));
    }

    @Test
    public void hasExtension_trailingDot_returnsFalse() {
        assertFalse(FileUtils.hasExtension("/path/to/video."));
    }

    // --- genUUID() tests ---

    @Test
    public void genUUID_returnsNonEmpty() {
        String uuid = FileUtils.genUUID();
        assertFalse(uuid.isEmpty());
    }

    @Test
    public void genUUID_noDashes() {
        String uuid = FileUtils.genUUID();
        assertFalse(uuid.contains("-"));
    }

    @Test
    public void genUUID_32Chars() {
        String uuid = FileUtils.genUUID();
        assertEquals(32, uuid.length());
    }

    @Test
    public void genUUID_uniqueEachCall() {
        String uuid1 = FileUtils.genUUID();
        String uuid2 = FileUtils.genUUID();
        assertFalse(uuid1.equals(uuid2));
    }

    // --- writeSimple / readSimple round-trip tests ---

    @Test
    public void writeSimple_readSimple_roundTrip() throws IOException {
        File tempFile = File.createTempFile("test", ".dat");
        try {
            byte[] data = "Hello, World!".getBytes("UTF-8");
            assertTrue(FileUtils.writeSimple(data, tempFile));
            byte[] read = FileUtils.readSimple(tempFile);
            assertArrayEquals(data, read);
        } finally {
            tempFile.delete();
        }
    }

    // --- recursiveDelete() tests ---

    @Test
    public void recursiveDelete_deletesDirectoryTree() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "test_recursive_" + System.currentTimeMillis());
        tempDir.mkdirs();
        File subFile = new File(tempDir, "sub.txt");
        subFile.createNewFile();
        File subDir = new File(tempDir, "subdir");
        subDir.mkdirs();
        new File(subDir, "nested.txt").createNewFile();

        assertTrue(tempDir.exists());
        FileUtils.recursiveDelete(tempDir);
        assertFalse(tempDir.exists());
    }

    @Test
    public void recursiveDelete_nonExistent_doesNotThrow() {
        File nonExistent = new File("/non/existent/path_" + System.currentTimeMillis());
        // Should not throw
        FileUtils.recursiveDelete(nonExistent);
    }

    // --- copyFile() tests ---

    @Test
    public void copyFile_copiesContent() throws IOException {
        File src = File.createTempFile("src", ".txt");
        File dst = File.createTempFile("dst", ".txt");
        try {
            FileUtils.writeSimple("test content".getBytes("UTF-8"), src);
            FileUtils.copyFile(src, dst);
            byte[] result = FileUtils.readSimple(dst);
            assertEquals("test content", new String(result, "UTF-8"));
        } finally {
            src.delete();
            dst.delete();
        }
    }
}

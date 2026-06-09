package com.github.tvbox.osc.util;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class AdBlockerTest {

    @After
    public void tearDown() {
        AdBlocker.clear();
    }

    @Test
    public void isEmpty_initialState_returnsTrue() {
        assertTrue(AdBlocker.isEmpty());
    }

    @Test
    public void addAdHost_thenNotEmpty() {
        AdBlocker.addAdHost("ad.example.com");
        assertFalse(AdBlocker.isEmpty());
    }

    @Test
    public void hasHost_existingHost_returnsTrue() {
        AdBlocker.addAdHost("ad.example.com");
        assertTrue(AdBlocker.hasHost("ad.example.com"));
    }

    @Test
    public void hasHost_missingHost_returnsFalse() {
        AdBlocker.addAdHost("ad.example.com");
        assertFalse(AdBlocker.hasHost("other.example.com"));
    }

    @Test
    public void isAd_urlContainsAdHost_returnsTrue() {
        AdBlocker.addAdHost("ad.example.com");
        assertTrue(AdBlocker.isAd("http://ad.example.com/banner.js"));
    }

    @Test
    public void isAd_urlDoesNotContainAdHost_returnsFalse() {
        AdBlocker.addAdHost("ad.example.com");
        assertFalse(AdBlocker.isAd("http://video.example.com/movie.mp4"));
    }

    @Test
    public void isAd_caseInsensitive() {
        AdBlocker.addAdHost("ad.example.com");
        assertTrue(AdBlocker.isAd("http://AD.EXAMPLE.COM/banner.js"));
    }

    @Test
    public void isAd_multipleHosts() {
        AdBlocker.addAdHost("ad1.example.com");
        AdBlocker.addAdHost("ad2.example.com");
        assertTrue(AdBlocker.isAd("http://ad1.example.com/banner.js"));
        assertTrue(AdBlocker.isAd("http://ad2.example.com/popup.js"));
        assertFalse(AdBlocker.isAd("http://content.example.com/video.mp4"));
    }

    @Test
    public void clear_removesAllHosts() {
        AdBlocker.addAdHost("ad.example.com");
        assertFalse(AdBlocker.isEmpty());
        AdBlocker.clear();
        assertTrue(AdBlocker.isEmpty());
        assertFalse(AdBlocker.hasHost("ad.example.com"));
    }
}

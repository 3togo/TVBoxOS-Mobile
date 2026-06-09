package com.github.tvbox.osc.util;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VideoParseRulerTest {

    @After
    public void tearDown() {
        VideoParseRuler.clearRule();
    }

    // --- addHostRule / getHostRules tests ---

    @Test
    public void getHostRules_noRules_returnsNull() {
        assertNull(VideoParseRuler.getHostRules("example.com"));
    }

    @Test
    public void addHostRule_singleRule_getHostRulesReturnsIt() {
        ArrayList<String> rule = new ArrayList<>();
        rule.add(".*\\.mp4");
        VideoParseRuler.addHostRule("example.com", rule);

        ArrayList<ArrayList<String>> rules = VideoParseRuler.getHostRules("example.com");
        assertNotNull(rules);
        assertEquals(1, rules.size());
        assertEquals(1, rules.get(0).size());
        assertEquals(".*\\.mp4", rules.get(0).get(0));
    }

    @Test
    public void addHostRule_multipleRulesForSameHost() {
        ArrayList<String> rule1 = new ArrayList<>();
        rule1.add(".*\\.mp4");
        ArrayList<String> rule2 = new ArrayList<>();
        rule2.add(".*\\.m3u8");

        VideoParseRuler.addHostRule("example.com", rule1);
        VideoParseRuler.addHostRule("example.com", rule2);

        ArrayList<ArrayList<String>> rules = VideoParseRuler.getHostRules("example.com");
        assertNotNull(rules);
        assertEquals(2, rules.size());
    }

    @Test
    public void addHostRule_differentHosts_keptSeparate() {
        ArrayList<String> rule = new ArrayList<>();
        rule.add(".*\\.mp4");
        VideoParseRuler.addHostRule("host1.com", rule);
        VideoParseRuler.addHostRule("host2.com", rule);

        assertNotNull(VideoParseRuler.getHostRules("host1.com"));
        assertNotNull(VideoParseRuler.getHostRules("host2.com"));
        assertNull(VideoParseRuler.getHostRules("host3.com"));
    }

    // --- addHostFilter / getHostFilters tests ---

    @Test
    public void getHostFilters_noFilters_returnsNull() {
        assertNull(VideoParseRuler.getHostFilters("example.com"));
    }

    @Test
    public void addHostFilter_singleFilter_getHostFiltersReturnsIt() {
        ArrayList<String> filter = new ArrayList<>();
        filter.add(".*ad.*");
        VideoParseRuler.addHostFilter("example.com", filter);

        ArrayList<ArrayList<String>> filters = VideoParseRuler.getHostFilters("example.com");
        assertNotNull(filters);
        assertEquals(1, filters.size());
    }

    @Test
    public void addHostFilter_multipleFiltersForSameHost() {
        ArrayList<String> filter1 = new ArrayList<>();
        filter1.add(".*ad.*");
        ArrayList<String> filter2 = new ArrayList<>();
        filter2.add(".*tracker.*");

        VideoParseRuler.addHostFilter("example.com", filter1);
        VideoParseRuler.addHostFilter("example.com", filter2);

        ArrayList<ArrayList<String>> filters = VideoParseRuler.getHostFilters("example.com");
        assertNotNull(filters);
        assertEquals(2, filters.size());
    }

    // --- addHostRegex / getHostsRegex tests ---

    @Test
    public void addHostRegex_nullList_ignored() {
        VideoParseRuler.addHostRegex("example.com", null);
        assertTrue(VideoParseRuler.getHostsRegex().isEmpty() ||
                !VideoParseRuler.getHostsRegex().containsKey("example.com"));
    }

    @Test
    public void addHostRegex_emptyList_ignored() {
        VideoParseRuler.addHostRegex("example.com", new ArrayList<>());
        assertTrue(VideoParseRuler.getHostsRegex().isEmpty() ||
                !VideoParseRuler.getHostsRegex().containsKey("example.com"));
    }

    @Test
    public void addHostRegex_validRegex_stored() {
        ArrayList<String> regex = new ArrayList<>();
        regex.add(".*\\.mp4.*");
        VideoParseRuler.addHostRegex("example.com", regex);

        assertNotNull(VideoParseRuler.getHostsRegex().get("example.com"));
        assertEquals(1, VideoParseRuler.getHostsRegex().get("example.com").size());
    }

    @Test
    public void addHostRegex_appendsToExisting() {
        ArrayList<String> regex1 = new ArrayList<>();
        regex1.add("regex1");
        ArrayList<String> regex2 = new ArrayList<>();
        regex2.add("regex2");

        VideoParseRuler.addHostRegex("example.com", regex1);
        VideoParseRuler.addHostRegex("example.com", regex2);

        assertEquals(2, VideoParseRuler.getHostsRegex().get("example.com").size());
    }

    // --- addHostScript / getHostScript tests ---

    @Test
    public void addHostScript_nullList_ignored() {
        VideoParseRuler.addHostScript("example.com", null);
        assertEquals("", VideoParseRuler.getHostScript("http://example.com/video"));
    }

    @Test
    public void addHostScript_validScript_stored() {
        ArrayList<String> script = new ArrayList<>();
        script.add("javascript:removeAd()");
        VideoParseRuler.addHostScript("example.com", script);

        String result = VideoParseRuler.getHostScript("http://example.com/video");
        assertEquals("javascript:removeAd()", result);
    }

    @Test
    public void getHostScript_noMatch_returnsEmpty() {
        ArrayList<String> script = new ArrayList<>();
        script.add("javascript:removeAd()");
        VideoParseRuler.addHostScript("example.com", script);

        assertEquals("", VideoParseRuler.getHostScript("http://other.com/video"));
    }

    // --- clearRule() tests ---

    @Test
    public void clearRule_clearsAllData() {
        ArrayList<String> rule = new ArrayList<>();
        rule.add(".*\\.mp4");
        VideoParseRuler.addHostRule("example.com", rule);
        VideoParseRuler.addHostFilter("example.com", rule);

        ArrayList<String> regex = new ArrayList<>();
        regex.add(".*\\.mp4.*");
        VideoParseRuler.addHostRegex("example.com", regex);

        ArrayList<String> script = new ArrayList<>();
        script.add("js");
        VideoParseRuler.addHostScript("example.com", script);

        VideoParseRuler.clearRule();

        assertNull(VideoParseRuler.getHostRules("example.com"));
        assertNull(VideoParseRuler.getHostFilters("example.com"));
        assertTrue(VideoParseRuler.getHostsRegex().isEmpty());
        assertEquals("", VideoParseRuler.getHostScript("http://example.com/video"));
    }
}

package com.github.tvbox.osc.bean;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SourceBeanTest {

    @Test
    public void defaultValues_areDefaults() {
        SourceBean bean = new SourceBean();
        assertNull(bean.getKey());
        assertNull(bean.getName());
        assertNull(bean.getApi());
        assertEquals(0, bean.getType());
        assertEquals(0, bean.getSearchable());
        assertEquals(0, bean.getQuickSearch());
        assertEquals(0, bean.getFilterable());
        assertNull(bean.getPlayerUrl());
        assertNull(bean.getExt());
        assertNull(bean.getJar());
        assertNull(bean.getCategories());
        assertEquals(0, bean.getPlayerType());
        assertNull(bean.getClickSelector());
    }

    @Test
    public void setKey_getKey() {
        SourceBean bean = new SourceBean();
        bean.setKey("test_key");
        assertEquals("test_key", bean.getKey());
    }

    @Test
    public void setName_getName() {
        SourceBean bean = new SourceBean();
        bean.setName("Test Source");
        assertEquals("Test Source", bean.getName());
    }

    @Test
    public void setApi_getApi() {
        SourceBean bean = new SourceBean();
        bean.setApi("http://example.com/api");
        assertEquals("http://example.com/api", bean.getApi());
    }

    @Test
    public void setType_getType() {
        SourceBean bean = new SourceBean();
        bean.setType(3);
        assertEquals(3, bean.getType());
    }

    @Test
    public void setPlayerUrl_getPlayerUrl() {
        SourceBean bean = new SourceBean();
        bean.setPlayerUrl("http://parse.example.com/?url=");
        assertEquals("http://parse.example.com/?url=", bean.getPlayerUrl());
    }

    @Test
    public void setExt_getExt() {
        SourceBean bean = new SourceBean();
        bean.setExt("{\"key\":\"value\"}");
        assertEquals("{\"key\":\"value\"}", bean.getExt());
    }

    @Test
    public void setJar_getJar() {
        SourceBean bean = new SourceBean();
        bean.setJar("http://example.com/spider.jar");
        assertEquals("http://example.com/spider.jar", bean.getJar());
    }

    @Test
    public void setCategories_getCategories() {
        SourceBean bean = new SourceBean();
        ArrayList<String> cats = new ArrayList<>();
        cats.add("电影");
        cats.add("电视剧");
        bean.setCategories(cats);
        assertEquals(2, bean.getCategories().size());
        assertEquals("电影", bean.getCategories().get(0));
        assertEquals("电视剧", bean.getCategories().get(1));
    }

    @Test
    public void setPlayerType_getPlayerType() {
        SourceBean bean = new SourceBean();
        bean.setPlayerType(1);
        assertEquals(1, bean.getPlayerType());
    }

    @Test
    public void setClickSelector_getClickSelector() {
        SourceBean bean = new SourceBean();
        bean.setClickSelector("ddrk.me;#id");
        assertEquals("ddrk.me;#id", bean.getClickSelector());
    }

    // --- isSearchable() tests ---

    @Test
    public void isSearchable_zero_returnsFalse() {
        SourceBean bean = new SourceBean();
        bean.setSearchable(0);
        assertFalse(bean.isSearchable());
    }

    @Test
    public void isSearchable_one_returnsTrue() {
        SourceBean bean = new SourceBean();
        bean.setSearchable(1);
        assertTrue(bean.isSearchable());
    }

    // --- isQuickSearch() tests ---

    @Test
    public void isQuickSearch_zero_returnsFalse() {
        SourceBean bean = new SourceBean();
        bean.setQuickSearch(0);
        assertFalse(bean.isQuickSearch());
    }

    @Test
    public void isQuickSearch_one_returnsTrue() {
        SourceBean bean = new SourceBean();
        bean.setQuickSearch(1);
        assertTrue(bean.isQuickSearch());
    }

    // --- getFilterable() / setFilterable() tests ---

    @Test
    public void setFilterable_getFilterable() {
        SourceBean bean = new SourceBean();
        bean.setFilterable(1);
        assertEquals(1, bean.getFilterable());
    }
}

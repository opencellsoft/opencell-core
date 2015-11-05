package org.meveo.cache;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CacheTest {

    @Test
    public void testCFCacheClosestMatch() {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("1", "A1");
        mapValue.put("12", "A12");
        mapValue.put("123", "A123");
        mapValue.put("1234", "A1234");
        mapValue.put("12345", "A12345");
        mapValue.put("123456", "A123456");

        CachedCFPeriodValue value = new CachedCFPeriodValue(mapValue);
        Assert.assertEquals("A123456", value.getClosestMatchValue("123456789784"));
        Assert.assertEquals("A123456", value.getClosestMatchValue("123456"));
        Assert.assertEquals("A1234", value.getClosestMatchValue("1234"));
        Assert.assertEquals("A1", value.getClosestMatchValue("1"));
        Assert.assertNull(value.getClosestMatchValue("012345"));
        Assert.assertNull(value.getClosestMatchValue(null));
    }
}
package org.meveo.service.crm.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.crm.CustomFieldMapKeyEnum;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;

public class CFValueTest {

    @Test
    public void testCFClosestMatch() {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("1", "A1");
        mapValue.put("12", "A12");
        mapValue.put("123", "A123");
        mapValue.put("1234", "A1234");
        mapValue.put("12345", "A12345");
        mapValue.put("123456", "A123456");

        Assert.assertEquals("A123456", CustomFieldInstanceService.matchClosestValue(mapValue, "123456789784"));
        Assert.assertEquals("A123456", CustomFieldInstanceService.matchClosestValue(mapValue, "123456"));
        Assert.assertEquals("A1234", CustomFieldInstanceService.matchClosestValue(mapValue, "1234"));
        Assert.assertEquals("A1", CustomFieldInstanceService.matchClosestValue(mapValue, "1"));
        Assert.assertNull(CustomFieldInstanceService.matchClosestValue(mapValue, "012345"));
        Assert.assertNull(CustomFieldInstanceService.matchClosestValue(mapValue, null));
    }

    @Test
    public void testCFMatrixStringMatch() {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("France|Old", "A1");
        mapValue.put("France|New", "A12");
        mapValue.put("UK|Old", "A123");
        mapValue.put("UK|New", "A1234");
        mapValue.put("LT|Old", "A12345");
        mapValue.put("LT|New", "A123456");

        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setStorageType(CustomFieldStorageTypeEnum.MATRIX);
        cft.setMapKeyType(CustomFieldMapKeyEnum.STRING);

        Assert.assertEquals("A123456", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "LT", "New"));
        Assert.assertEquals("A12345", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "LT", "Old"));
        Assert.assertEquals("A1234", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "UK", "New"));
        Assert.assertEquals("A1", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "France", "Old"));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "Arg", "Old"));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, null, "Old"));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "UK", null));

    }

    @Test
    public void testCFRonMatch() {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("10<19", "A1");
        mapValue.put("-5<-2", "A1234");
        mapValue.put("30<", "A12");
        mapValue.put("<9", "A123");

        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setStorageType(CustomFieldStorageTypeEnum.MAP);
        cft.setMapKeyType(CustomFieldMapKeyEnum.RON);

        Assert.assertEquals("A1", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 15));
        Assert.assertEquals("A1", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 18.99));
        Assert.assertEquals("A1234", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, -5));
        Assert.assertEquals("A12", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 15000L));
        Assert.assertEquals("A123", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 7));
        Assert.assertNull(CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 19));
        Assert.assertNull(CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 9));
        Assert.assertNull(CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, null));
    }

    @Test
    public void testCFMatrixRonMatch() {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("10<19|14<17", "A1");
        mapValue.put("-5<-2|10<14", "A12");
        mapValue.put("-5<-2|<10", "A123");
        mapValue.put("-5<-2|>21", "A1234");
        mapValue.put("-5<|<-1", "A12345");

        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setStorageType(CustomFieldStorageTypeEnum.MATRIX);
        cft.setMapKeyType(CustomFieldMapKeyEnum.RON);

        Assert.assertEquals("A1", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 15, 15));
        Assert.assertEquals("A123", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, -4, 9.99));
        Assert.assertEquals("A12345", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, -2, -5));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 15, 17));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "France", "Old"));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, null, 15));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, -3, null));

    }
}
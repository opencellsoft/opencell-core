package org.meveo.service.crm.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

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

        Assert.assertEquals("A123456", ICustomFieldEntity.matchClosestValue(mapValue, "123456789784"));
        Assert.assertEquals("A123456", ICustomFieldEntity.matchClosestValue(mapValue, "123456"));
        Assert.assertEquals("A1234", ICustomFieldEntity.matchClosestValue(mapValue, "1234"));
        Assert.assertEquals("A1", ICustomFieldEntity.matchClosestValue(mapValue, "1"));
        Assert.assertNull(ICustomFieldEntity.matchClosestValue(mapValue, "012345"));
        Assert.assertNull(ICustomFieldEntity.matchClosestValue(mapValue, null));
    }

    @Test
    public void testCFMatrixMatch() {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("2001<2005|France|200<", "A1");
        mapValue.put("2001<2005|France|100<200", "A12");
        mapValue.put("2001<2005|France|<100", "A123");
        mapValue.put("2001<2005|Vilnius|200<", "A1234");
        mapValue.put("2005<2006|Vilnius|200<", "A12345");
        mapValue.put("2006<2009|Vilnius|200<205", "A123456");
        mapValue.put("2006<2009|Vilnius|205<305", "A1234567");

        CustomFieldTemplate cft = new CustomFieldTemplate();

        // Now the order is important as matrix columns are no longer resorted after retrieval - it relies on being sorted when retrieving from DB
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.RON);
        column.setPosition(1);
        cft.getMatrixColumns().add(column);

        column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.STRING);
        column.setPosition(2);
        cft.getMatrixColumns().add(column);

        column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.RON);
        column.setPosition(3);
        cft.getMatrixColumns().add(column);

        cft.setStorageType(CustomFieldStorageTypeEnum.MATRIX);

        Assert.assertEquals("A1", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2002, "France", 200));
        Assert.assertEquals("A12", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2001, "France", 105));
        Assert.assertEquals("A123", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2004.99, "France", 95));
        Assert.assertEquals("A123456", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2006, "Vilnius", 201));
        Assert.assertEquals("A1234567", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2007, "Vilnius", 304.999));
        Assert.assertEquals("A1234567", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2007, "Vilnius", new BigDecimal(304.999)));
        Assert.assertEquals("A1234567", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, (Object[]) "2007|Vilnius|304.999".split("\\|")));

        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2007, "Vilnius"));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2007, "Vilnius", 15, "Vilnius"));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, null, "Vilnius", 304.999));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2007, null, null));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2017, "Vilnius", 304.999));
        Assert.assertNull(CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2007, "Vilnius", 305));

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
        Assert.assertEquals("A1", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, new BigDecimal(18.99)));
        Assert.assertEquals("A1234", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, -5));
        Assert.assertEquals("A12", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 15000L));
        Assert.assertEquals("A123", CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 7));
        Assert.assertNull(CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 19));
        Assert.assertNull(CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, 9));
        Assert.assertNull(CustomFieldInstanceService.matchRangeOfNumbersValue(mapValue, null));
    }

    @Test
    public void testCFMatrixStringWildcardMatch() {
        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("1|*", "1");
        mapValue.put("1|2", "2");
        mapValue.put("1|3", "3");
        mapValue.put("2|1", "4");
        mapValue.put("*|2", "5");
        mapValue.put("2|2", "50");
        mapValue.put("2|3", "6");

        CustomFieldTemplate cft = new CustomFieldTemplate();

        // Now the order is important as matrix columns are no longer resorted after retrieval - it relies on being sorted when retrieving from DB
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.STRING);
        column.setPosition(1);
        cft.getMatrixColumns().add(column);

        column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.STRING);
        column.setPosition(2);
        cft.getMatrixColumns().add(column);

        cft.setStorageType(CustomFieldStorageTypeEnum.MATRIX);

        Assert.assertEquals("1", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "1", "1"));
        Assert.assertEquals("5", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, "2", "2"));
    }

    @Test
    public void testCFMatrixRONWildcardMatch() {
        Map<String, Object> mapValue = new HashMap<String, Object>();

        mapValue.put("2001<2005|France|0<", "T1");
        mapValue.put("2005<2010|France|50<100", "T2");
        mapValue.put("*|France|100<150", "T3");
        mapValue.put("2010<2015|France|*", "T4");

        CustomFieldTemplate cft = new CustomFieldTemplate();

        // Now the order is important as matrix columns are no longer resorted after
        // retrieval - it relies on being sorted when retrieving from DB
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.RON);
        column.setPosition(1);
        cft.getMatrixColumns().add(column);

        column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.STRING);
        column.setPosition(2);
        cft.getMatrixColumns().add(column);

        column = new CustomFieldMatrixColumn();
        column.setKeyType(CustomFieldMapKeyEnum.RON);
        column.setPosition(2);
        cft.getMatrixColumns().add(column);

        cft.setStorageType(CustomFieldStorageTypeEnum.MATRIX);

        Assert.assertEquals("T1", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2002, "France", 25));
        Assert.assertEquals("T2", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2006, "France", 75));
        Assert.assertEquals("T1", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2002, "France", 75));
        Assert.assertEquals("T3", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 0, "France", 120));
        Assert.assertEquals("T4", CustomFieldInstanceService.matchMatrixValue(cft, mapValue, 2010, "France", 0));
    }

    @Test
    public void testDirtyFlag() {

        Map<String, String> map = new HashMap<>();
        map.put("one", "two");
        map.put("two", "three");
        map.put("three", "four");

        CustomFieldValue cfv = new CustomFieldValue(map);
        CustomFieldValue cfvCloned = cfv.clone();

        Assert.assertFalse(cfv == cfvCloned);

        ((Map) cfv.getValue()).put("five", "5");

        Assert.assertTrue(((Map) cfv.getValue()).containsKey("five"));
        Assert.assertFalse(((Map) cfvCloned.getValue()).containsKey("five"));

        cfv = new CustomFieldValue("One");
        cfvCloned = cfv.clone();
        cfv.setValue("Two");

        Assert.assertEquals("Two", (String) cfv.getValue());
        Assert.assertEquals("One", (String) cfvCloned.getValue());

        CustomFieldValues cfvs = new CustomFieldValues();
        cfvs.setValue("mapVal", map);
        cfvs.setValue("stringVal", "test");
        cfvs.setValue("string2Val", "test");
        cfvs.setValue("map2Val", new HashMap<String, String>(map));

        Assert.assertEquals(4, cfvs.getDirtyCfPeriods().size());
        Assert.assertEquals(4, cfvs.getDirtyCfValues().size());

        Map<String, List<CustomFieldValue>> clonedValues1 = cloneValues(cfvs.getValuesByCode());
        Map<String, List<CustomFieldValue>> clonedValues2 = cloneValues(cfvs.getValuesByCode());

        cfvs.clearDirtyFlags();

        cfvs.setValuesByCode(clonedValues1);

        Assert.assertEquals(0, cfvs.getDirtyCfPeriods().size());
        Assert.assertEquals(0, cfvs.getDirtyCfValues().size());

        ((Map) ((CustomFieldValue) clonedValues2.get("mapVal").get(0)).getValue()).put("two", "twos");

        cfvs.setValues(clonedValues2);

        Assert.assertEquals(0, cfvs.getDirtyCfPeriods().size());
        Assert.assertEquals(1, cfvs.getDirtyCfValues().size());

        cfvs.clearDirtyFlags();

        Map<String, List<CustomFieldValue>> clonedValues3 = cloneValues(cfvs.getValuesByCode());

        Date dayOne = new Date();
        dayOne.setMonth(10);
        dayOne.setDate(3);

        Date dayTwo = new Date();
        dayTwo.setMonth(11);
        dayTwo.setDate(3);

        clonedValues3.put("otherVal", Arrays.asList(new CustomFieldValue(new DatePeriod(dayOne, dayOne), 0, "other")));

        cfvs.setValues(clonedValues3);

        Assert.assertEquals(1, cfvs.getDirtyCfPeriods().size());
        Assert.assertEquals(1, cfvs.getDirtyCfValues().size());

        cfvs.clearDirtyFlags();

        Map<String, List<CustomFieldValue>> clonedValues4 = cloneValues(cfvs.getValuesByCode());

        clonedValues4.get("otherVal").add(new CustomFieldValue(new DatePeriod(dayTwo, dayTwo), 1, "otherValueAgain"));
        clonedValues4.get("string2Val").get(0).setValue("testing value");

        cfvs.setValues(clonedValues4);

        Assert.assertEquals(1, cfvs.getDirtyCfPeriods().size());
        Assert.assertEquals(2, cfvs.getDirtyCfValues().size());
    }

    private Map<String, List<CustomFieldValue>> cloneValues(Map<String, List<CustomFieldValue>> valuesToClone) {
        Map<String, List<CustomFieldValue>> clonedValues = new HashMap<>();

        for (Entry<String, List<CustomFieldValue>> entry : valuesToClone.entrySet()) {
            String code = entry.getKey();

            List<CustomFieldValue> cfValuesByTemplateCloned = new ArrayList<>();
            for (CustomFieldValue cfValue : entry.getValue()) {
                cfValue = cfValue.clone(); // SerializationUtils.clone(cfValue);
                cfValuesByTemplateCloned.add(cfValue);
            }
            clonedValues.put(code, cfValuesByTemplateCloned);
        }

        return clonedValues;
    }
}
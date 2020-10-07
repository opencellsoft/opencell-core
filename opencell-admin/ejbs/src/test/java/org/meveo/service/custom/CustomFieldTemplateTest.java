/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn.CustomFieldColumnUseEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
public class CustomFieldTemplateTest {

    @Test
    public void testCFMatrix() {

        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setFieldType(CustomFieldTypeEnum.MULTI_VALUE);
        cft.setStorageType(CustomFieldStorageTypeEnum.MATRIX);

        List<CustomFieldMatrixColumn> matrixColumns = new ArrayList<CustomFieldMatrixColumn>();

        CustomFieldMatrixColumn matrixColumn = new CustomFieldMatrixColumn("item_id", "Item id");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_KEY);
        matrixColumn.setPosition(1);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("one", "One");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(1);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("two", "Two");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(2);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("three", "Three");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(3);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("four", "Four");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(4);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("five", "Five");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(5);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("six", "Six");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(6);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        matrixColumn = new CustomFieldMatrixColumn("seven", "Seven");
        matrixColumn.setColumnUse(CustomFieldColumnUseEnum.USE_VALUE);
        matrixColumn.setPosition(7);
        matrixColumn.setKeyType(CustomFieldMapKeyEnum.STRING);
        matrixColumns.add(matrixColumn);

        cft.setMatrixColumns(matrixColumns);

        Map<String, Object> values = cft.deserializeMultiValue("1|2|3|4|5|6|7", null);
        Assert.assertEquals("1", values.get("one"));
        Assert.assertEquals("2", values.get("two"));
        Assert.assertEquals("3", values.get("three"));
        Assert.assertEquals("4", values.get("four"));
        Assert.assertEquals("5", values.get("five"));
        Assert.assertEquals("6", values.get("six"));
        Assert.assertEquals("7", values.get("seven"));
        Assert.assertEquals(7, values.size());

        values = cft.deserializeMultiValue("1|2|3|4|5|6|", null);
        Assert.assertEquals("1", values.get("one"));
        Assert.assertEquals("2", values.get("two"));
        Assert.assertEquals("3", values.get("three"));
        Assert.assertEquals("4", values.get("four"));
        Assert.assertEquals("5", values.get("five"));
        Assert.assertEquals("6", values.get("six"));
        Assert.assertNull(values.get("seven"));
        Assert.assertEquals(6, values.size());

        values = cft.deserializeMultiValue("1|2|3|4|5|6|7||9", null);
        Assert.assertEquals("1", values.get("one"));
        Assert.assertEquals("2", values.get("two"));
        Assert.assertEquals("3", values.get("three"));
        Assert.assertEquals("4", values.get("four"));
        Assert.assertEquals("5", values.get("five"));
        Assert.assertEquals("6", values.get("six"));
        Assert.assertEquals("7", values.get("seven"));
        Assert.assertEquals(7, values.size());

        values = new HashMap<String, Object>();
        values.put("four", "4");
        values.put("five", "5|5");
        String serializedValue = cft.serializeMultiValue(values);
        Assert.assertEquals("|||4|5&#124;5||", serializedValue);

        values = cft.deserializeMultiValue(serializedValue, null);
        Assert.assertNull(values.get("one"));
        Assert.assertNull(values.get("two"));
        Assert.assertNull(values.get("three"));
        Assert.assertEquals("4", values.get("four"));
        Assert.assertEquals("5|5", values.get("five"));
        Assert.assertNull(values.get("six"));
        Assert.assertNull(values.get("seven"));

    }
}
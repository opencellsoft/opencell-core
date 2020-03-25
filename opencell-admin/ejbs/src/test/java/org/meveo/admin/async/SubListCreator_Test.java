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

package org.meveo.admin.async;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SubListCreator_Test {

    @Test
    public void testExportTransformation() {

        List<Long> valuesToSplit = new ArrayList<>();
        for (long i = 0L; i < 45L; i++) {
            valuesToSplit.add(i);
        }

        // Test fixed paging size
        SubListCreator<Long> subList = new SubListCreator<>(10, null);
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(10, new ArrayList<Long>());
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(-1, valuesToSplit);
        for (int i = 0; i < 45; i++) {
            Assert.assertEquals(1, subList.getNextWorkSet().size());
        }
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(10, valuesToSplit);
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(10, subList.getNextWorkSet().size());
        }
        Assert.assertEquals(5, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(50, valuesToSplit);
        Assert.assertEquals(45, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        // Test fixed number of runs

        subList = new SubListCreator<>(null, 10);
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(new ArrayList<Long>(), 10);
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(valuesToSplit, -1);
        Assert.assertEquals(45, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(valuesToSplit, 4);
        Assert.assertEquals(11, subList.getNextWorkSet().size());
        Assert.assertEquals(11, subList.getNextWorkSet().size());
        Assert.assertEquals(11, subList.getNextWorkSet().size());
        Assert.assertEquals(12, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        subList = new SubListCreator<>(valuesToSplit, 5);
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

    }
}

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
import org.junit.Before;
import org.junit.Test;

public class SubListCreator_Test {

    private List<Long> valuesToSplit;

    @Before
    public void initDataSet()
    {
        valuesToSplit = new ArrayList<>();
        for (long i = 0L; i < 45L; i++) {
            valuesToSplit.add(i);
        }
    }


    @Test
    public void test_whenItemsPerSplit_andBigListIsNull() {
        SubListCreator<Long> subList = new SubListCreator<>(10, null);
        Assert.assertFalse(subList.isHasNext());
    }

@Test
    public void test_whenNbrSplits_andBigListIsNull() {
        SubListCreator<Long> subList = new SubListCreator<>(null, 10);
        Assert.assertFalse(subList.isHasNext());
    }

    @Test
    public void test_whenItemsPerSplit_andBigListIsEmpty() {
        SubListCreator<Long> subList = new SubListCreator<>(10, new ArrayList<>());
        Assert.assertFalse(subList.isHasNext());
    }

@Test
    public void test_whenNbrSplits_andBigListIsEmpty() {
        SubListCreator<Long> subList = new SubListCreator<>( new ArrayList<>(), 10);
        Assert.assertFalse(subList.isHasNext());
    }

    @Test
    public void test_nbrSplits_isNegative() {
        SubListCreator<Long> subList  = new SubListCreator<>(valuesToSplit, -1);
        Assert.assertEquals(45, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());
    }

    @Test
    public void test_itemsPerSplit_isNegative() {
        SubListCreator<Long> subList = new SubListCreator<>(-1, valuesToSplit);
        for(int i=0;i < valuesToSplit.size(); i++) {
            Assert.assertEquals(1, subList.getNextWorkSet().size());
        }
        Assert.assertFalse(subList.isHasNext());
    }

        @Test
        public void test_bigList_nonDivisiblePer_itemsPerSplit() {

        SubListCreator<Long> subList = new SubListCreator<>(10, valuesToSplit);
        for(int i = 0; i < 4; i++){
           Assert.assertEquals(10, subList.getNextWorkSet().size());
        }
        Assert.assertEquals(5, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());
        }

        @Test
        public void test_bigList_nonDivisiblePer_nbrSplits() {

        SubListCreator<Long> subList = new SubListCreator<>(valuesToSplit, 4);
        for(int i= 0; i < 3; i++)
        {
         Assert.assertEquals(12, subList.getNextWorkSet().size());
        }
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());
        }



         @Test
        public void test_itemsPerSplit_biggerThan_bigList() {

        SubListCreator<Long> subList = new SubListCreator<>(50, valuesToSplit);
        Assert.assertEquals(45, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        }

        @Test
        public void test_FairDivision_perNbrSplits() {

        SubListCreator<Long> subList = new SubListCreator<>(valuesToSplit, 5);
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        }

        @Test
        public void test_FairDivision_perItemsPerSplit() {

        SubListCreator<Long> subList = new SubListCreator<>(9, valuesToSplit);
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertEquals(9, subList.getNextWorkSet().size());
        Assert.assertFalse(subList.isHasNext());

        }






}

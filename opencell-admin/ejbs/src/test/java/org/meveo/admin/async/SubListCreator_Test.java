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

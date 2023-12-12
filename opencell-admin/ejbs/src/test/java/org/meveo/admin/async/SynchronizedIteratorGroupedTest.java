package org.meveo.admin.async;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SynchronizedIteratorGroupedTest {

    @Test
    public void testGrouping() {

        List<TestObject> data = new ArrayList<TestObject>();
        data.add(new TestObject(0, "one"));
        data.add(new TestObject(1, "one"));
        data.add(new TestObject(2, "two"));
        data.add(new TestObject(3, "two"));
        data.add(new TestObject(4, "two"));
        data.add(new TestObject(5, "three"));
        data.add(new TestObject(6, "three"));
        data.add(new TestObject(7, "four"));
        data.add(new TestObject(8, "five"));

        SynchronizedIteratorGrouped<TestObject> iterator = new SynchronizedIteratorGrouped<TestObject>(data) {

            @Override
            public Object getGroupByValue(TestObject item) {
                return item.groupBy;
            }
        };

        List<TestObject> nextData = iterator.next();
        assertThat(nextData.size()).isEqualTo(2);
        assertThat(nextData.get(0).i).isEqualTo(0);
        assertThat(nextData.get(1).i).isEqualTo(1);

        nextData = iterator.next();
        assertThat(nextData.size()).isEqualTo(3);
        assertThat(nextData.get(0).i).isEqualTo(2);
        assertThat(nextData.get(1).i).isEqualTo(3);
        assertThat(nextData.get(2).i).isEqualTo(4);

        nextData = iterator.next();
        assertThat(nextData.size()).isEqualTo(2);
        assertThat(nextData.get(0).i).isEqualTo(5);
        assertThat(nextData.get(1).i).isEqualTo(6);

        nextData = iterator.next();
        assertThat(nextData.size()).isEqualTo(1);
        assertThat(nextData.get(0).i).isEqualTo(7);

        nextData = iterator.next();
        assertThat(nextData.size()).isEqualTo(1);
        assertThat(nextData.get(0).i).isEqualTo(8);

        nextData = iterator.next();
        assertThat(nextData).isNull();

    }

    private class TestObject {

        private int i;

        private String groupBy;

        public TestObject(int i, String groupBy) {
            this.i = i;
            this.groupBy = groupBy;
        }
    }
}
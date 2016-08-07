package org.meveo.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;

/**
 * Created by Phu Bach on 8/7/2016.
 */
public class ActionsUtil {
    public static void sortByOrderLevel(List<UserHierarchyLevel> userHierarchyLevelList) {
        Collections.sort(userHierarchyLevelList, new Comparator<HierarchyLevel>() {
            @Override
            public int compare(HierarchyLevel o1, HierarchyLevel o2) {
                return o1.getOrderLevel().compareTo(o2.getOrderLevel());
            }
        });
    }
}

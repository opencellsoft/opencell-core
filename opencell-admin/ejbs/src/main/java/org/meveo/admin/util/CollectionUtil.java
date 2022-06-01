package org.meveo.admin.util;

import java.util.Collection;

public class CollectionUtil {

    public static boolean isNullOrEmpty( final Collection< ? > c ) {
    return c == null || c.isEmpty();
}
}

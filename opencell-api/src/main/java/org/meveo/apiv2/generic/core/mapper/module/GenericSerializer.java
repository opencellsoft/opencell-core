package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;

import java.util.Set;

interface GenericSerializer {
// todo : change to simple getter if no difference
    Set<String> getNestedEntities();

    default String getPathToRoot(JsonGenerator gen){
        return gen.getOutputContext().pathAsPointer(false).toString().replaceFirst("/", "").replaceAll("\\d+/", "").replaceAll("/", ".");
    }

    default boolean isNestedEntityCandidate(String pathToRoot, String current) {
        String currentPathToRoot = pathToRoot.substring(0, pathToRoot.lastIndexOf(".") + 1) + current;
        return (pathToRoot.contains(".") && getNestedEntities().contains(currentPathToRoot.toLowerCase()));
    }
}

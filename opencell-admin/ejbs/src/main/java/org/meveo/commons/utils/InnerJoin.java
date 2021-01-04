package org.meveo.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InnerJoin {

    private final String alias;
    private String name;
    private List<InnerJoin> nextInnerJoins = new ArrayList();

    public InnerJoin(String name) {
        this.name = name;
        this.alias = name + "_" + new Random().nextInt(100);
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public void next(InnerJoin nextInnerJoin) {
        nextInnerJoins.add(nextInnerJoin);
    }

    public List<InnerJoin> getNextInnerJoins() {
        return nextInnerJoins;
    }
}

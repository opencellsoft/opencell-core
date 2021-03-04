package org.meveo.commons.utils;

public class JoinWrapper {

    private InnerJoin rootInnerJoin;
    private String joinAlias;

    public JoinWrapper(InnerJoin rootInnerJoin, String joinAlias) {
        this.rootInnerJoin = rootInnerJoin;
        this.joinAlias = joinAlias;
    }

    public InnerJoin getRootInnerJoin() {
        return rootInnerJoin;
    }

    public String getJoinAlias() {
        return joinAlias;
    }
}

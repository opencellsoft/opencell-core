package org.meveo.commons.utils;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(joinAlias);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JoinWrapper other = (JoinWrapper) obj;
		return Objects.equals(joinAlias, other.joinAlias);
	}

    
}

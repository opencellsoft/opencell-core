package org.meveo.api.dto.response;

import org.meveo.api.dto.account.ParentEntitiesDto;

/**
 * @author Tony Alejandro.
 */
public class ParentListResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;

	private ParentEntitiesDto parents;

	public ParentEntitiesDto getParents() {
		return parents;
	}

	public void setParents(ParentEntitiesDto parents) {
		this.parents = parents;
	}

	@Override
	public String toString() {
		return "ParentListResponse [parents=" + parents + "]";
	}
}

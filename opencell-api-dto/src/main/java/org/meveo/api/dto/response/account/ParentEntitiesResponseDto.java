package org.meveo.api.dto.response.account;

import org.meveo.api.dto.account.ParentEntitiesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Tony Alejandro.
 */
public class ParentEntitiesResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private ParentEntitiesDto parentEntities;

	public ParentEntitiesDto getParentEntities() {
		return parentEntities;
	}

	public void setParentEntities(ParentEntitiesDto parentEntities) {
		this.parentEntities = parentEntities;
	}

	@Override
	public String toString() {
		return "ParentEntitiesResponseDto [parentEntities=" + parentEntities + ", toString()=" + super.toString() + "]";
	}
}

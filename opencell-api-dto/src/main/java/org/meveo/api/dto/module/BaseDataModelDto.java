package org.meveo.api.dto.module;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;

abstract class BaseDataModelDto extends BusinessDto implements IEntity {
	
	public BaseDataModelDto() {
		
	}

	public BaseDataModelDto(BusinessEntity e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

package org.meveo.api.dto.catalog;

import java.io.Serializable;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.module.MeveoModule;

public class BusinessProductModelDto extends MeveoModuleDto implements Serializable{

	private static final long serialVersionUID = -4510290371772010482L;
	
	public BusinessProductModelDto() {
	}
	
	public BusinessProductModelDto(MeveoModule module) {
		super(module);
	}

}

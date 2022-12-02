package org.meveo.api.dto.billing;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

@SuppressWarnings("serial")
public class CdrListDtosList extends BaseEntityDto  {
    
    private List<CdrDto> cdrs;

	public List<CdrDto> getCdrs() {
		return cdrs;
	}

	public void setCdrs(List<CdrDto> cdrs) {
		this.cdrs = cdrs;
	}
    
    
    
}

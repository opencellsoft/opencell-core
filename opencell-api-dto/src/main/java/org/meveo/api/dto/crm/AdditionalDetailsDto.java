package org.meveo.api.dto.crm;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.intcrm.AdditionalDetails;

public class AdditionalDetailsDto  extends BaseEntityDto  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 620321903097019766L;

	private String companyName;
	
	private String position;
		
	private String instantMessengers;
	
	public AdditionalDetailsDto() {
		
	}

	public AdditionalDetailsDto(AdditionalDetails additionalDetails) {
		companyName = additionalDetails.getCompanyName();
		position = additionalDetails.getPosition();
		instantMessengers = additionalDetails.getInstantMessengers();
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getInstantMessengers() {
		return instantMessengers;
	}

	public void setInstantMessengers(String instantMessengers) {
		this.instantMessengers = instantMessengers;
	}
	
	
}

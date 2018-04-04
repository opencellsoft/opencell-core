package org.meveo.model.intcrm;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@CustomFieldEntity(cftCodePrefix = "ADDDETAILS")
@ExportIdentifier({ "code" })
//@DiscriminatorValue(value = "")
@Table(name = "crm_additionDetails")
public class AdditionalDetails {
	@Column(name = "companyname", length = 50)
	@Size(max = 50)
	private String companyName;
	
	@Column(name = "position", length = 50)
	@Size(max = 50)
	private String position;
	
	@Column(name = "websiteurl", length = 255)
	@Size(max = 255)
	private String websiteUrl;
	
	@Column(name = "instantmessaging", length = 255)
	@Size(max = 255)
	private String instantMessaging;

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

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getInstantMessaging() {
		return instantMessaging;
	}

	public void setInstantMessaging(String instantMessaging) {
		this.instantMessaging = instantMessaging;
	}
}

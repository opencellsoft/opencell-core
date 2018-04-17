package org.meveo.model.intcrm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "additionalDetails" })
@Table(name = "crm_additional_details")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_additional_details_seq") })
public class AdditionalDetails extends BaseEntity{
	@Column(name = "company_name", length = 50)
	@Size(max = 50)
	private String companyName;
	
	@Column(name = "position", length = 50)
	@Size(max = 50)
	private String position;
	
	@Column(name = "website_url", length = 255)
	@Size(max = 255)
	private String websiteUrl;
	
	@Column(name = "instant_messengers", length = 2000)
	@Size(max = 500)
	private String instantMessengers;

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

	public String getInstantMessengers() {
		return instantMessengers;
	}

	public void setInstantMessengers(String instantMessengers) {
		this.instantMessengers = instantMessengers;
	}
}

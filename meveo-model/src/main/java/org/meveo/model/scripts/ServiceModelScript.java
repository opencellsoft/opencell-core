package org.meveo.model.scripts;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.BusinessServiceModel;

@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("ServiceModel")
public class ServiceModelScript extends CustomScript {

	private static final long serialVersionUID = -2688817434026306258L;

	@OneToOne(mappedBy = "script")
	private BusinessServiceModel businessServiceModel;

	public BusinessServiceModel getBusinessServiceModel() {
		return businessServiceModel;
	}

	public void setBusinessServiceModel(BusinessServiceModel businessServiceModel) {
		this.businessServiceModel = businessServiceModel;
	}

}
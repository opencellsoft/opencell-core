package org.meveo.model.scripts;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.catalog.BusinessOfferModel;

@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("OfferModel")
public class OfferModelScript extends CustomScript {

	private static final long serialVersionUID = -2688817434026306258L;

	@OneToOne(mappedBy = "script")
	private BusinessOfferModel businessOfferModel;

	public OfferModelScript() {

	}

	public BusinessOfferModel getBusinessOfferModel() {
		return businessOfferModel;
	}

	public void setBusinessOfferModel(BusinessOfferModel businessOfferModel) {
		this.businessOfferModel = businessOfferModel;
	}

}

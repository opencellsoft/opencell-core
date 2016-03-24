package org.meveo.model.scripts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("OfferModel")
public class OfferModelScript extends CustomScript {

	private static final long serialVersionUID = -2688817434026306258L;

	@Column(name = "APPLIES_TO", nullable = false, length = 100)
	private String appliesTo;

	public OfferModelScript() {

	}

	public String getAppliesTo() {
		return appliesTo;
	}

	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}

}

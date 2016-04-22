package org.meveo.model.scripts;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("OfferModel")
public class OfferModelScript extends CustomScript {

	private static final long serialVersionUID = -2688817434026306258L;

	public OfferModelScript() {

	}

}

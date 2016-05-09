package org.meveo.model.scripts;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("RevenueRecognition")
public class RevenueRecognitionScriptEntity extends CustomScript {

	private static final long serialVersionUID = 4421247873127057237L;
	

}
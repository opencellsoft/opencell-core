package org.meveo.model.crm;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.scripts.CustomScript;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue("AccountModel")
public class AccountModelScript extends CustomScript {

	private static final long serialVersionUID = 4168804933774888922L;
	
	@OneToOne(mappedBy = "script")
	private BusinessAccountModel businessAccountModel;

	public BusinessAccountModel getBusinessAccountModel() {
		return businessAccountModel;
	}

	public void setBusinessAccountModel(BusinessAccountModel businessAccountModel) {
		this.businessAccountModel = businessAccountModel;
	}

}

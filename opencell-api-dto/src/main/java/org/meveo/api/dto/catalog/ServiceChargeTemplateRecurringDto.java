package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateRecurringDto extends BaseServiceChargeTemplateDto implements Serializable {

	private static final long serialVersionUID = 4021985900952093283L;

	@Override
	public String toString() {
		return "ServiceChargeTemplateRecurringDto [getCode()=" + getCode() + ", getWallets()=" + getWallets() + "]";
	}

}

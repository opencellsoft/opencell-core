package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ServiceChargeTemplateSubscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateSubscriptionDto extends BaseServiceChargeTemplateDto {

	private static final long serialVersionUID = 6508584475693802506L;

	@Override
	public String toString() {
		return "ServiceChargeTemplateSubscriptionDto [getCode()=" + getCode() + ", getWallets()=" + getWallets() + "]";
	}

}

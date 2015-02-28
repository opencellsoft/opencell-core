package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "ServiceChargeTemplateTermination")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateTerminationDto extends BaseServiceChargeTemplateDto {

	private static final long serialVersionUID = -191541706032220541L;

	@Override
	public String toString() {
		return "ServiceChargeTemplateTerminationDto [getCode()=" + getCode() + ", getWallets()=" + getWallets() + "]";
	}

}

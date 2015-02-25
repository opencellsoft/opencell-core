package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "OtherCreditAndCharge")
@XmlAccessorType(XmlAccessType.FIELD)
public class OtherCreditAndChargeDto extends AccountOperationDto implements Serializable {

	private static final long serialVersionUID = 5458679584153463383L;

	public OtherCreditAndChargeDto() {
		super.setType("OCC");
	}

	private Date operationDate;

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

}

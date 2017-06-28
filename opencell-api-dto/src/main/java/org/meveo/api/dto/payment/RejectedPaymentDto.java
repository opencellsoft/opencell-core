package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.payments.RejectedType;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectedPaymentDto extends AccountOperationDto implements Serializable {

	private static final long serialVersionUID = 4498720672406401363L;

	private RejectedType rejectedType;
	private Date rejectedDate;
	private String rejectedDescription;
	private String rejectedCode;

	public RejectedPaymentDto() {
		super.setType("R");
	}

	public RejectedType getRejectedType() {
		return rejectedType;
	}

	public void setRejectedType(RejectedType rejectedType) {
		this.rejectedType = rejectedType;
	}

	public Date getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(Date rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public String getRejectedDescription() {
		return rejectedDescription;
	}

	public void setRejectedDescription(String rejectedDescription) {
		this.rejectedDescription = rejectedDescription;
	}

	public String getRejectedCode() {
		return rejectedCode;
	}

	public void setRejectedCode(String rejectedCode) {
		this.rejectedCode = rejectedCode;
	}

}

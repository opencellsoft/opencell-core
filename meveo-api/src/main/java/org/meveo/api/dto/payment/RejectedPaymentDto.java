package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "RejectedPayment")
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectedPaymentDto extends AccountOperationDto implements Serializable {

	private static final long serialVersionUID = 4498720672406401363L;

	private String rejectedType;
	private String bankLot;
	private String bankReference;
	private Date rejectedDate;
	private String rejectedDescription;
	private String rejectedCode;

	public RejectedPaymentDto() {
		super.setType("R");
	}

	public String getRejectedType() {
		return rejectedType;
	}

	public void setRejectedType(String rejectedType) {
		this.rejectedType = rejectedType;
	}

	public String getBankLot() {
		return bankLot;
	}

	public void setBankLot(String bankLot) {
		this.bankLot = bankLot;
	}

	public String getBankReference() {
		return bankReference;
	}

	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
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

package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.SubscriptionTerminationReason;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TerminationReasonDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminationReasonDto implements Serializable {

	private static final long serialVersionUID = 6013621511892042364L;

	private String code;
	private String description;
	private boolean applyAgreement;
	private boolean applyReimbursment;
	private boolean applyTerminationCharges;

	public TerminationReasonDto() {

	}

	public TerminationReasonDto(SubscriptionTerminationReason e) {
		code = e.getCode();
		description = e.getDescription();
		applyAgreement = e.isApplyAgreement();
		applyReimbursment = e.isApplyReimbursment();
		applyTerminationCharges = e.isApplyTerminationCharges();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isApplyAgreement() {
		return applyAgreement;
	}

	public void setApplyAgreement(boolean applyAgreement) {
		this.applyAgreement = applyAgreement;
	}

	public boolean isApplyReimbursment() {
		return applyReimbursment;
	}

	public void setApplyReimbursment(boolean applyReimbursment) {
		this.applyReimbursment = applyReimbursment;
	}

	public boolean isApplyTerminationCharges() {
		return applyTerminationCharges;
	}

	public void setApplyTerminationCharges(boolean applyTerminationCharges) {
		this.applyTerminationCharges = applyTerminationCharges;
	}

	@Override
	public String toString() {
		return "TerminationReasonDto [code=" + code + ", description=" + description + ", applyAgreement="
				+ applyAgreement + ", applyReimbursment=" + applyReimbursment + ", applyTerminationCharges="
				+ applyTerminationCharges + "]";
	}

}

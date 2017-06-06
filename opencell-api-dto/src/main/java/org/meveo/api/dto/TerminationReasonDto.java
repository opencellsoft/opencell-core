package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.SubscriptionTerminationReason;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TerminationReasonDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminationReasonDto extends BusinessDto {

    private static final long serialVersionUID = 6013621511892042364L;

    private boolean applyAgreement;

    private boolean applyReimbursment;

    private boolean applyTerminationCharges;

    public TerminationReasonDto() {

    }

    public TerminationReasonDto(SubscriptionTerminationReason e) {
        setCode(e.getCode());
        setDescription(e.getDescription());
        applyAgreement = e.isApplyAgreement();
        applyReimbursment = e.isApplyReimbursment();
        applyTerminationCharges = e.isApplyTerminationCharges();
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
        return "TerminationReasonDto [code=" + getCode() + ", description=" + getDescription() + ", applyAgreement=" + applyAgreement + ", applyReimbursment=" + applyReimbursment
                + ", applyTerminationCharges=" + applyTerminationCharges + "]";
    }

}

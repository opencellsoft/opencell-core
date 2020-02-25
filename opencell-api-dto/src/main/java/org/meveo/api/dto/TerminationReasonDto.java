package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.OverrideProrataEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;

/**
 * The Class TerminationReasonDto.
 *
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 */
@XmlRootElement(name = "TerminationReasonDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminationReasonDto extends BusinessEntityDto {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 6013621511892042364L;

    /**
     * The apply agreement.
     */
    private boolean applyAgreement;

    /**
     * The apply reimbursment.
     */
    private boolean applyReimbursment;

    /**
     * The apply termination charges.
     */
    private boolean applyTerminationCharges;

    /**
     * Override the prorata setting in the charge.
     */
    private OverrideProrataEnum overrideProrata;

    /**
     * Instantiates a new termination reason dto.
     */
    public TerminationReasonDto() {

    }

    /**
     * Instantiates a new termination reason dto.
     *
     * @param subscriptionTerminationReason the SubscriptionTerminationReason entity
     */
    public TerminationReasonDto(SubscriptionTerminationReason subscriptionTerminationReason) {
        super(subscriptionTerminationReason);
        applyAgreement = subscriptionTerminationReason.isApplyAgreement();
        applyReimbursment = subscriptionTerminationReason.isApplyReimbursment();
        applyTerminationCharges = subscriptionTerminationReason.isApplyTerminationCharges();
        overrideProrata = subscriptionTerminationReason.getOverrideProrata();
    }

    /**
     * Checks if is apply agreement.
     *
     * @return true, if is apply agreement
     */
    public boolean isApplyAgreement() {
        return applyAgreement;
    }

    /**
     * Sets the apply agreement.
     *
     * @param applyAgreement the new apply agreement
     */
    public void setApplyAgreement(boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    /**
     * Checks if is apply reimbursment.
     *
     * @return true, if is apply reimbursment
     */
    public boolean isApplyReimbursment() {
        return applyReimbursment;
    }

    /**
     * Sets the apply reimbursment.
     *
     * @param applyReimbursment the new apply reimbursment
     */
    public void setApplyReimbursment(boolean applyReimbursment) {
        this.applyReimbursment = applyReimbursment;
    }

    /**
     * Checks if is apply termination charges.
     *
     * @return true, if is apply termination charges
     */
    public boolean isApplyTerminationCharges() {
        return applyTerminationCharges;
    }

    /**
     * Sets the apply termination charges.
     *
     * @param applyTerminationCharges the new apply termination charges
     */
    public void setApplyTerminationCharges(boolean applyTerminationCharges) {
        this.applyTerminationCharges = applyTerminationCharges;
    }

    /**
     * Gets the override prorata value.
     *
     * @return the override prorata value
     */
    public OverrideProrataEnum getOverrideProrata() {
        return overrideProrata;
    }

    /**
     * Sets the override prorata value.
     *
     * @param overrideProrata the the override prorata.
     */
    public void setOverrideProrata(OverrideProrataEnum overrideProrata) {
        this.overrideProrata = overrideProrata;
    }

    @Override
    public String toString() {
        return "TerminationReasonDto [code=" + getCode() + ", description=" + getDescription() + ", applyAgreement=" + applyAgreement + ", applyReimbursment=" + applyReimbursment
                + ", applyTerminationCharges=" + applyTerminationCharges + "]";
    }
}
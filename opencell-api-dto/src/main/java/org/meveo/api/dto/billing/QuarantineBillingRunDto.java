package org.meveo.api.dto.billing;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class QuarantineBillingRunDto.
 *
 * @author Tarik Rabeh
 */
@XmlRootElement(name = "QuarantineBillingRunRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuarantineBillingRunDto implements Serializable {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The quarantineBillingRunId. */
    @XmlElement(required = false)
    private Long quarantineBillingRunId;

    /**
     * Gets the quarantine billingRun id.
     *
     * @return the quarantine billingRun id
     */
    public Long getQuarantineBillingRunId() {
        return quarantineBillingRunId;
    }

    /**
     * Sets the quarantine billingRun Id.
     *
     * @param quarantineBillingRunId the quarantine billingRun id
     */
    public void setQuarantineBillingRunId(Long quarantineBillingRunId) {
        this.quarantineBillingRunId = quarantineBillingRunId;
    }
}

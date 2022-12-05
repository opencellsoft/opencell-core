package org.meveo.api.dto.billing;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class ValidateBillingRunRequestDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "ValidateBillingRunRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidateBillingRunRequestDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4477259461644796968L;

    /** The billingRunId. */
    @XmlElement(required = true)
    private Long billingRunId;

    /**
     * Gets the billingRun id.
     *
     * @return the billingRun id
     */
    public Long getBillingRunId() {
        return billingRunId;
    }

    /**
     * Sets the billingRun Id.
     *
     * @param billingRunId the new billingRun id
     */
    public void setBillingRunId(Long billingRunId) {
        this.billingRunId = billingRunId;
    }
}

package org.meveo.api.dto.billing;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CancelBillingRunRequestDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "CancelBillingRunRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelBillingRunRequestDto {

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

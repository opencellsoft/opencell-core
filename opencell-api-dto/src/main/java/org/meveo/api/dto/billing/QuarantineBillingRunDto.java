package org.meveo.api.dto.billing;


import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.LanguageDescriptionDto;

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
     * The description I18N.
     */
    @XmlElement(required = false)
    private List<LanguageDescriptionDto> descriptionsTranslated;

    
    
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

    public List<LanguageDescriptionDto> getDescriptionsTranslated() {
		return descriptionsTranslated;
	}

	public void setDescriptionsTranslated(List<LanguageDescriptionDto> descriptionsTranslated) {
		this.descriptionsTranslated = descriptionsTranslated;
	}
	
}

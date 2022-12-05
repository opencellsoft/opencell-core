package org.meveo.api.dto.cpq.xml;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.PriceDTO;

@XmlAccessorType(XmlAccessType.FIELD)
public class BillableAccount {
    @XmlAttribute
    private String billingAccountCode;

    @XmlElementWrapper(name = "quoteLots")
    @XmlElement(name = "quoteLot")
    private List<QuoteLot> quoteLots;

    @XmlElementWrapper(name = "billingAccountPrices")
    @XmlElement(name = "price")
    List<PriceDTO> billingAccountPrices;
    
    private CustomFieldsDto customFields;

    public BillableAccount(String code, List<QuoteLot> quoteLots, List<PriceDTO> billingAccountPrices
    		) {
        this.billingAccountCode = code;
        this.quoteLots = quoteLots;
        this.billingAccountPrices = billingAccountPrices;
    }
 
    public String getBillingAccountCode() {
        return billingAccountCode;
    }

    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    public List<QuoteLot> getQuoteLots() {
        return quoteLots;
    }

    public void setQuoteLots(List<QuoteLot> quoteLots) {
        this.quoteLots = quoteLots;
    }

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}


    
    
}

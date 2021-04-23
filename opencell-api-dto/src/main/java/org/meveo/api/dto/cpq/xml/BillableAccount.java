package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.cpq.PriceDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

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

    public BillableAccount(String code, List<QuoteLot> quoteLots, List<PriceDTO> billingAccountPrices) {
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
}

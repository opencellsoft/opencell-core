package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.SequenceDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;

@XmlRootElement(name = "InvoiceType")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceTypeDto extends BusinessDto {

    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    private String occTemplateCode;

    private String occTemplateNegativeCode;

    private SequenceDto sequenceDto;

    @XmlElementWrapper
    @XmlElement(name = "sellerSequence")
    private Map<String, SequenceDto> sellerSequences = new HashMap<String, SequenceDto>();

    private List<String> appliesTo = new ArrayList<String>();

    private boolean matchingAuto = false;

    private String billingTemplateName;

    public InvoiceTypeDto() {

    }

    public InvoiceTypeDto(InvoiceType invoiceType) {
        super(invoiceType);

        this.occTemplateCode = invoiceType.getOccTemplate() != null ? invoiceType.getOccTemplate().getCode() : null;
        this.occTemplateNegativeCode = invoiceType.getOccTemplateNegative() != null ? invoiceType.getOccTemplateNegative().getCode() : null;
        this.sequenceDto = new SequenceDto(invoiceType.getSequence());
        if (invoiceType.getAppliesTo() != null) {
            for (InvoiceType tmpInvoiceType : invoiceType.getAppliesTo()) {
                this.getAppliesTo().add(tmpInvoiceType.getCode());
            }
        }
        for (InvoiceTypeSellerSequence seq : invoiceType.getSellerSequence()) {
            sellerSequences.put(seq.getSeller().getCode(), new SequenceDto(seq.getSequence()));
        }
        this.matchingAuto = invoiceType.isMatchingAuto();
        this.billingTemplateName = invoiceType.getBillingTemplateName();
    }

    public String getOccTemplateCode() {
        return occTemplateCode;
    }

    public void setOccTemplateCode(String occTemplateCode) {
        this.occTemplateCode = occTemplateCode;
    }

    public List<String> getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(List<String> appliesTo) {
        this.appliesTo = appliesTo;
    }

    public boolean isMatchingAuto() {
        return matchingAuto;
    }

    public void setMatchingAuto(boolean matchingAuto) {
        this.matchingAuto = matchingAuto;
    }

    public SequenceDto getSequenceDto() {
        return sequenceDto;
    }

    public void setSequenceDto(SequenceDto sequenceDto) {
        this.sequenceDto = sequenceDto;
    }

    public Map<String, SequenceDto> getSellerSequences() {
        return sellerSequences;
    }

    public void setSellerSequences(Map<String, SequenceDto> sellerSequences) {
        this.sellerSequences = sellerSequences;
    }

    public String getOccTemplateNegativeCode() {
        return occTemplateNegativeCode;
    }

    public void setOccTemplateNegativeCode(String occTemplateNegativeCode) {
        this.occTemplateNegativeCode = occTemplateNegativeCode;
    }

    public String getBillingTemplateName() {
        return billingTemplateName;
    }

    public void setBillingTemplateName(String billingTemplateName) {
        this.billingTemplateName = billingTemplateName;
    }

    @Override
    public String toString() {
        return "InvoiceTypeDto [code=" + getCode() + ", description=" + getDescription() + ", occTemplateCode=" + occTemplateCode + ", occTemplateNegativeCode="
                + occTemplateNegativeCode + ", sequenceDto=" + sequenceDto + ", sellerSequences=" + sellerSequences + ", appliesTo=" + appliesTo + ", matchingAuto=" + matchingAuto
                + "]";
    }
}

/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.SequenceDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.scripts.ScriptInstance;

/**
 * The Class InvoiceTypeDto.
 *
 * @author anasseh
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 */
@XmlRootElement(name = "InvoiceType")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceTypeDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The occ template code. */
    @XmlElement(required = true)
    private String occTemplateCode;

    /** The occ template negative code. */
    private String occTemplateNegativeCode;

    /** The occ template code EL. */
    private String occTemplateCodeEl;

    /** The occ template negative code EL. */
    private String occTemplateNegativeCodeEl;

    /** The script instance code. */
    private String customInvoiceXmlScriptInstanceCode;

    /** The sequence dto. */
    private SequenceDto sequenceDto;
    
    /** The script to be used on invoice auto validation. */
    private String invoiceValidationScriptCode;

    /** The seller sequences. */
    @XmlElementWrapper
    @XmlElement(name = "sellerSequence")
    private Map<String, SequenceDto> sellerSequences = new HashMap<>();

    /** The applies to. */
    private List<String> appliesTo = new ArrayList<>();

    /** The matching auto. */
    private Boolean matchingAuto;
    
    /** Used to decide if AccountOperations will be created or not , during AO_Job execution */
    private Boolean invoiceAccountable;

    /** The billing template name. */
    private String billingTemplateName;

    /**
     * An EL expression to customize invoice PDF file name.
     */
    private String pdfFilenameEL;

    /**
     * An EL expression to customize invoice XML file name.
     */
    private String xmlFilenameEL;

    /** The billing template name EL. */
    private String billingTemplateNameEL;

    private CustomFieldsDto customFields;

    /** The use Self Sequence . */
    private Boolean useSelfSequence;

    private String mailingType;

    private String emailTemplateCode;

    /**
     * Instantiates a new invoice type dto.
     */
    public InvoiceTypeDto() {

    }

    /**
     * Instantiates a new invoice type dto
     *
     * @param invoiceType Invoice type
     * @param customFieldInstances Custom field values DTO
     */
    public InvoiceTypeDto(InvoiceType invoiceType, CustomFieldsDto customFieldInstances) {
        super(invoiceType);
        this.occTemplateCode = invoiceType.getOccTemplate() != null ? invoiceType.getOccTemplate().getCode() : null;
        this.occTemplateNegativeCode = invoiceType.getOccTemplateNegative() != null ? invoiceType.getOccTemplateNegative().getCode() : null;
        this.occTemplateCodeEl = invoiceType.getOccTemplateCodeEl();
        this.occTemplateNegativeCodeEl = invoiceType.getOccTemplateNegativeCodeEl();
        this.customInvoiceXmlScriptInstanceCode = invoiceType.getCustomInvoiceXmlScriptInstance() == null ? null : invoiceType.getCustomInvoiceXmlScriptInstance().getCode();
        this.sequenceDto = new SequenceDto(invoiceType.getInvoiceSequence(), invoiceType.getPrefixEL());
        if (invoiceType.getAppliesTo() != null) {
            for (InvoiceType tmpInvoiceType : invoiceType.getAppliesTo()) {
                this.getAppliesTo().add(tmpInvoiceType.getCode());
            }
        }
        for (InvoiceTypeSellerSequence seq : invoiceType.getSellerSequence()) {
            sellerSequences.put(seq.getSeller().getCode(), new SequenceDto(seq.getInvoiceSequence(), seq.getPrefixEL()));
        }
        this.matchingAuto = invoiceType.isMatchingAuto();
        this.pdfFilenameEL = invoiceType.getPdfFilenameEL();
        this.xmlFilenameEL = invoiceType.getXmlFilenameEL();
        this.billingTemplateNameEL = invoiceType.getBillingTemplateNameEL();
        this.mailingType = invoiceType.getMailingType() != null ? invoiceType.getMailingType().getLabel() : null;
        this.emailTemplateCode = invoiceType.getEmailTemplate() != null ? invoiceType.getEmailTemplate().getCode() : null;
        customFields = customFieldInstances;
        this.useSelfSequence = invoiceType.isUseSelfSequence();
    }

    /**
     * Gets the occ template code.
     *
     * @return the occ template code
     */
    public String getOccTemplateCode() {
        return occTemplateCode;
    }

    /**
     * Sets the occ template code.
     *
     * @param occTemplateCode the new occ template code
     */
    public void setOccTemplateCode(String occTemplateCode) {
        this.occTemplateCode = occTemplateCode;
    }

    /**
     * Gets the applies to.
     *
     * @return the applies to
     */
    public List<String> getAppliesTo() {
        return appliesTo;
    }

    /**
     * Sets the applies to.
     *
     * @param appliesTo the new applies to
     */
    public void setAppliesTo(List<String> appliesTo) {
        this.appliesTo = appliesTo;
    }

    /**
     * Checks if is matching auto.
     *
     * @return true, if is matching auto
     */
    public Boolean isMatchingAuto() {
        return matchingAuto;
    }

    /**
     * Sets the matching auto.
     *
     * @param matchingAuto the new matching auto
     */
    public void setMatchingAuto(Boolean matchingAuto) {
        this.matchingAuto = matchingAuto;
    }

    /**
     * Gets the sequence dto.
     *
     * @return the sequence dto
     */
    public SequenceDto getSequenceDto() {
        return sequenceDto;
    }

    /**
     * Sets the sequence dto.
     *
     * @param sequenceDto the new sequence dto
     */
    public void setSequenceDto(SequenceDto sequenceDto) {
        this.sequenceDto = sequenceDto;
    }

    /**
     * Gets the seller sequences.
     *
     * @return the seller sequences
     */
    public Map<String, SequenceDto> getSellerSequences() {
        return sellerSequences;
    }

    /**
     * Sets the seller sequences.
     *
     * @param sellerSequences the seller sequences
     */
    public void setSellerSequences(Map<String, SequenceDto> sellerSequences) {
        this.sellerSequences = sellerSequences;
    }

    /**
     * Gets the occ template negative code.
     *
     * @return the occ template negative code
     */
    public String getOccTemplateNegativeCode() {
        return occTemplateNegativeCode;
    }

    /**
     * Sets the occ template negative code.
     *
     * @param occTemplateNegativeCode the new occ template negative code
     */
    public void setOccTemplateNegativeCode(String occTemplateNegativeCode) {
        this.occTemplateNegativeCode = occTemplateNegativeCode;
    }

    /**
     * Gets the billing template name.
     *
     * @return the billing template name
     */
    public String getBillingTemplateName() {
        return billingTemplateName;
    }

    /**
     * Sets the billing template name.
     *
     * @param billingTemplateName the new billing template name
     */
    public void setBillingTemplateName(String billingTemplateName) {
        this.billingTemplateName = billingTemplateName;
    }

    /**
     * Gets the pdf filename EL.
     *
     * @return the pdf filename EL
     */
    public String getPdfFilenameEL() {
        return pdfFilenameEL;
    }

    /**
     * Sets the pdf filename EL.
     *
     * @param pdfFilenameEL the new pdf filename EL
     */
    public void setPdfFilenameEL(String pdfFilenameEL) {
        this.pdfFilenameEL = pdfFilenameEL;
    }

    /**
     * Gets the xml filename EL.
     *
     * @return the xml filename EL
     */
    public String getXmlFilenameEL() {
        return xmlFilenameEL;
    }

    /**
     * Sets the xml filename EL.
     *
     * @param xmlFilenameEL the new xml filename EL
     */
    public void setXmlFilenameEL(String xmlFilenameEL) {
        this.xmlFilenameEL = xmlFilenameEL;
    }

    /**
     * Gets the billing template name EL.
     *
     * @return the billing template name EL
     */
    public String getBillingTemplateNameEL() {
        return billingTemplateNameEL;
    }

    /**
     * Sets the billing template name EL.
     *
     * @param billingTemplateNameEL the new billing template name EL
     */
    public void setBillingTemplateNameEL(String billingTemplateNameEL) {
        this.billingTemplateNameEL = billingTemplateNameEL;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * @return the useSelfSequence
     */
    public Boolean isUseSelfSequence() {
        return useSelfSequence;
    }

    /**
     * @param useSelfSequence the useSelfSequence to set
     */
    public void setUseSelfSequence(Boolean useSelfSequence) {
        this.useSelfSequence = useSelfSequence;
    }

    /**
     * Gets the occ template code EL.
     *
     * @return the occ template code EL
     */
    public String getOccTemplateCodeEl() {
        return occTemplateCodeEl;
    }

    /**
     * Sets the occ template code EL.
     *
     * @param occTemplateCodeEl the new occ template code EL
     */
    public void setOccTemplateCodeEl(String occTemplateCodeEl) {
        this.occTemplateCodeEl = occTemplateCodeEl;
    }

    /**
     * Gets the occ template negative code EL.
     *
     * @return the occ template negative code EL
     */
    public String getOccTemplateNegativeCodeEl() {
        return occTemplateNegativeCodeEl;
    }

    /**
     * Sets the occ template negative code EL.
     *
     * @param occTemplateNegativeCodeEl the new occ template negative code EL
     */
    public void setOccTemplateNegativeCodeEl(String occTemplateNegativeCodeEl) {
        this.occTemplateNegativeCodeEl = occTemplateNegativeCodeEl;
    }

    /**
     * Gets the script instance code.
     *
     * @return the customInvoiceXmlScriptInstanceCode
     */
    public String getCustomInvoiceXmlScriptInstanceCode() {
        return customInvoiceXmlScriptInstanceCode;
    }

    /**
     * Sets the script instance code.
     *
     * @param customInvoiceXmlScriptInstanceCode the scriptInstanceCode to set
     */
    public void setCustomInvoiceXmlScriptInstanceCode(String customInvoiceXmlScriptInstanceCode) {
        this.customInvoiceXmlScriptInstanceCode = customInvoiceXmlScriptInstanceCode;
    }

    public String getMailingType() {
        return mailingType;
    }

    public void setMailingType(String mailingType) {
        this.mailingType = mailingType;
    }

    public String getEmailTemplateCode() {
        return emailTemplateCode;
    }

    public void setEmailTemplateCode(String emailTemplateCode) {
        this.emailTemplateCode = emailTemplateCode;
    }

    public Boolean isInvoiceAccountable() {
		return invoiceAccountable;
	}

	public void setInvoiceAccountable(Boolean invoiceAccountable) {
		this.invoiceAccountable = invoiceAccountable;
	}

	@Override
    public String toString() {
        return "InvoiceTypeDto [code=" + getCode() + ", description=" + getDescription() + ", occTemplateCode=" + occTemplateCode + ", occTemplateNegativeCode="
                + occTemplateNegativeCode + ", customInvoiceXmlScriptInstanceCode=" + customInvoiceXmlScriptInstanceCode + ", sequenceDto=" + sequenceDto + ", sellerSequences="
                + sellerSequences + ", appliesTo=" + appliesTo + ", matchingAuto=" + matchingAuto + ", useSelfSequence=" + useSelfSequence + ", invoiceAccountable=" + invoiceAccountable + "]";
    }

	/**
	 * @return the invoiceValidationScript
	 */
	public String getInvoiceValidationScriptCode() {
		return invoiceValidationScriptCode;
	}

	/**
	 * @param invoiceValidationScript the invoiceValidationScript to set
	 */
	public void setInvoiceValidationScriptCode(String invoiceValidationScriptCode) {
		this.invoiceValidationScriptCode = invoiceValidationScriptCode;
	}
}
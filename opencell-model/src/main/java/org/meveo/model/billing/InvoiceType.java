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
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.scripts.ScriptInstance;

/**
 * @author Edward P. Legaspi
 * @author Bahije Mounir
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_invoice_type", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@CustomFieldEntity(cftCodePrefix = "InvoiceType")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_type_seq"), })
public class InvoiceType extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_template_id")
    private OCCTemplate occTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_templ_negative_id")
    private OCCTemplate occTemplateNegative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance customInvoiceXmlScriptInstance;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_invoice_type_applies_to", joinColumns = @JoinColumn(name = "invoice_type_id"), inverseJoinColumns = @JoinColumn(name = "applies_to_id"))
    private List<InvoiceType> appliesTo = new ArrayList<InvoiceType>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sequence_id", nullable = true)
    private InvoiceSequence invoiceSequence;

    @Column(name = "prefix_el", length = 2000)
    @Size(max = 2000)
    private String prefixEL = "";

    @OneToMany(mappedBy = "invoiceType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceTypeSellerSequence> sellerSequence = new ArrayList<InvoiceTypeSellerSequence>();

    @Type(type = "numeric_boolean")
    @Column(name = "matching_auto")
    private boolean matchingAuto = false;
    
    /** 
     * Used to decide if AccountOperations will be created or not , during AO_Job execution
     */
    @Type(type = "numeric_boolean")
    @Column(name = "invoice_accountable")
    private boolean invoiceAccountable = true;

    @Type(type = "numeric_boolean")
    @Column(name = "use_self_sequence")
    private boolean useSelfSequence = true;

    /**
     * An EL expression to customize invoice PDF file name.
     */
    @Column(name = "pdf_filename_el", length = 2000)
    @Size(max = 2000)
    private String pdfFilenameEL;

    /**
     * An EL expression to customize invoice XML file name.
     */
    @Column(name = "xml_filename_el", length = 2000)
    @Size(max = 2000)
    private String xmlFilenameEL;

    @Column(name = "billing_template_name_el", length = 2000)
    @Size(max = 2000)
    private String billingTemplateNameEL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_script_instance_id")
    private ScriptInstance taxScript;

    @Column(name = "occ_template_code_el", length = 2000)
    @Size(max = 2000)
    private String occTemplateCodeEl;

    @Column(name = "occ_template_negative_code_el", length = 2000)
    @Size(max = 2000)
    private String occTemplateNegativeCodeEl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_template_id")
    private EmailTemplate emailTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "mailing_type")
    private MailingTypeEnum mailingType;
    
    /**
     * executed for each invoice, Will raise an exception if the invoice is invalid. Context will contain billingRun and invoice.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_validation_script_id")
    private ScriptInstance invoiceValidationScript;

    /**
     * executed for each invoice, Will raise an exception if the invoice is invalid. Context will contain billingRun and invoice.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_validation_script_id")
    private ScriptInstance billingRunValidationScript;
    
    public ScriptInstance getInvoiceValidationScript() {
		return invoiceValidationScript;
	}

	public void setInvoiceValidationScript(ScriptInstance invoiceValidationScript) {
		this.invoiceValidationScript = invoiceValidationScript;
	}

	public ScriptInstance getBillingRunValidationScript() {
		return billingRunValidationScript;
	}

	public void setBillingRunValidationScript(ScriptInstance billingRunValidationScript) {
		this.billingRunValidationScript = billingRunValidationScript;
	}

	public OCCTemplate getOccTemplate() {
        return occTemplate;
    }

    public void setOccTemplate(OCCTemplate occTemplate) {
        this.occTemplate = occTemplate;
    }

    public List<InvoiceType> getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(List<InvoiceType> appliesTo) {
        this.appliesTo = appliesTo;
    }

    public boolean isMatchingAuto() {
        return matchingAuto;
    }

    public void setMatchingAuto(boolean matchingAuto) {
        this.matchingAuto = matchingAuto;
    }

    public InvoiceSequence getInvoiceSequence() {
        return invoiceSequence;
    }

    public void setInvoiceSequence(InvoiceSequence invoiceSequence) {
        this.invoiceSequence = invoiceSequence;
    }

    public List<InvoiceTypeSellerSequence> getSellerSequence() {
        return sellerSequence;
    }

    public void setSellerSequence(List<InvoiceTypeSellerSequence> sellerSequence) {
        this.sellerSequence = sellerSequence;
    }

    public OCCTemplate getOccTemplateNegative() {
        return occTemplateNegative;
    }

    public void setOccTemplateNegative(OCCTemplate occTemplateNegative) {
        this.occTemplateNegative = occTemplateNegative;
    }

    public InvoiceTypeSellerSequence getSellerSequenceByType(Seller seller) {
        for (InvoiceTypeSellerSequence seq : sellerSequence) {
            if (seq.getSeller().equals(seller)) {
                return seq;
            }
        }
        return null;
    }

    public InvoiceSequence getSellerSequenceSequenceByType(Seller seller) {
        InvoiceTypeSellerSequence seq = getSellerSequenceByType(seller);
        if (seq != null) {
            return seq.getInvoiceSequence();
        }
        return null;
    }

    public boolean isContainsSellerSequence(Seller seller) {
        InvoiceTypeSellerSequence seq = getSellerSequenceByType(seller);
        return seq != null;
    }

    public String getPdfFilenameEL() {
        return pdfFilenameEL;
    }

    public void setPdfFilenameEL(String pdfFilenameEL) {
        this.pdfFilenameEL = pdfFilenameEL;
    }

    public String getXmlFilenameEL() {
        return xmlFilenameEL;
    }

    public void setXmlFilenameEL(String xmlFilenameEL) {
        this.xmlFilenameEL = xmlFilenameEL;
    }

    public String getBillingTemplateNameEL() {
        return billingTemplateNameEL;
    }

    public void setBillingTemplateNameEL(String billingTemplateNameEL) {
        this.billingTemplateNameEL = billingTemplateNameEL;
    }

    public ScriptInstance getTaxScript() {
        return taxScript;
    }

    public void setTaxScript(ScriptInstance taxScript) {
        this.taxScript = taxScript;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    /**
     * @return the useSelfSequence
     */
    public boolean isUseSelfSequence() {
        return useSelfSequence;
    }

    /**
     * @param useSelfSequence the useSelfSequence to set
     */
    public void setUseSelfSequence(boolean useSelfSequence) {
        this.useSelfSequence = useSelfSequence;
    }

    public String getOccTemplateCodeEl() {
        return occTemplateCodeEl;
    }

    public void setOccTemplateCodeEl(String occTemplateCodeEl) {
        this.occTemplateCodeEl = occTemplateCodeEl;
    }

    public String getOccTemplateNegativeCodeEl() {
        return occTemplateNegativeCodeEl;
    }

    public void setOccTemplateNegativeCodeEl(String occTemplateNegativeCodeEl) {
        this.occTemplateNegativeCodeEl = occTemplateNegativeCodeEl;
    }

    public String getPrefixEL() {
        return prefixEL;
    }

    public void setPrefixEL(String prefixEL) {
        this.prefixEL = prefixEL;
    }

    public ScriptInstance getCustomInvoiceXmlScriptInstance() {
        return customInvoiceXmlScriptInstance;
    }

    public void setCustomInvoiceXmlScriptInstance(ScriptInstance customInvoiceXmlScriptInstance) {
        this.customInvoiceXmlScriptInstance = customInvoiceXmlScriptInstance;
    }

    /**
     * Gets Email Template.
     * @return Email Template.
     */
    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * Sets Email template.
     * @param emailTemplate the Email template.
     */
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * Gets Mailing Type.
     * @return Mailing Type.
     */
    public MailingTypeEnum getMailingType() {
        return mailingType;
    }

    /**
     * Sets Mailing Type.
     * @param mailingType mailing type
     */
    public void setMailingType(MailingTypeEnum mailingType) {
        this.mailingType = mailingType;
    }

    public boolean isInvoiceAccountable() {
		return invoiceAccountable;
	}

	public void setInvoiceAccountable(boolean invoiceAccountable) {
		this.invoiceAccountable = invoiceAccountable;
	}
}
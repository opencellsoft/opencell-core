/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_invoice_type", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@CustomFieldEntity(cftCodePrefix = "INVOICE_TYPE")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_type_seq"), })
@NamedQueries({ @NamedQuery(name = "InvoiceType.currentInvoiceNb", query = "select max(sequence.currentInvoiceNb) from InvoiceType i where i.code=:invoiceTypeCode") })
public class InvoiceType extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_template_id")
    private OCCTemplate occTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_templ_negative_id")
    private OCCTemplate occTemplateNegative;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_invoice_type_applies_to", joinColumns = @JoinColumn(name = "invoice_type_id"), inverseJoinColumns = @JoinColumn(name = "applies_to_id"))
    private List<InvoiceType> appliesTo = new ArrayList<InvoiceType>();

    @Embedded
    private Sequence sequence = new Sequence();

    @OneToMany(mappedBy = "invoiceType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceTypeSellerSequence> sellerSequence = new ArrayList<InvoiceTypeSellerSequence>();

    @Type(type = "numeric_boolean")
    @Column(name = "matching_auto")
    private boolean matchingAuto = false;

    @Column(name = "billing_template_name")
    @Size(max = 50)
    private String billingTemplateName;
    
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
    
    @ManyToOne()
    @JoinColumn(name = "tax_script_instance_id")
    private ScriptInstance taxScript;

    @Column(name = "occ_template_code_el", length = 2000)
    @Size(max = 2000)
    private String occTemplateCodeEl;
    
    @Column(name = "occ_template_negative_code_el", length = 2000)
    @Size(max = 2000)
    private String occTemplateNegativeCodeEl;

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

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
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

    public Sequence getSellerSequenceSequenceByType(Seller seller) {
        InvoiceTypeSellerSequence seq = getSellerSequenceByType(seller);
        if (seq != null) {
            return seq.getSequence();
        }
        return null;
    }

    public boolean isContainsSellerSequence(Seller seller) {
        InvoiceTypeSellerSequence seq = getSellerSequenceByType(seller);
        return seq != null;
    }

    public String getBillingTemplateName() {
        return billingTemplateName;
    }

    public void setBillingTemplateName(String billingTemplateName) {
        this.billingTemplateName = billingTemplateName;
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
    
}
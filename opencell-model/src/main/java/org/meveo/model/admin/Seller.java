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
package org.meveo.model.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.GeneralLedger;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;

/**
 * Seller
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Khalid HORRI
 * @author Amine BEN AICHA
 * @lastModifiedVersion 5.3
 **/

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "SELLER", inheritCFValuesFrom = "seller", inheritFromProvider = true)
@ExportIdentifier({ "code" })
@Table(name = "crm_seller", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_seller_seq"), })
public class Seller extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Currency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    /**
     * Country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    /**
     * Language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    private TradingLanguage tradingLanguage;

    /**
     * Address
     */
    @Embedded
    private Address address;

    /**
     * Contact information
     */
    @Embedded
    private ContactInformation contactInformation;

    /**
     * The seller VAT No
     */
    @Size(max = 100)
    @Column(name = "vat_no", length = 100)
    private String vatNo;

    /**
     * The seller registration No
     */
    @Size(max = 100)
    @Column(name = "registration_no", length = 100)
    private String registrationNo;

    /**
     * A legal text for the seller
     */
    @Size(max = 2000)
    @Column(name = "legal_text", columnDefinition = "text")
    private String legalText;

    /**
     * The legal type of the seller
     */
    @Size(max = 100)
    @Column(name = "legal_type", length = 255)
    private String legalType;

    /**
     * Parent seller in seller hierarchy
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_seller_id")
    private Seller seller;

    /**
     * Invoice numbering sequence
     */
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceTypeSellerSequence> invoiceTypeSequence = new ArrayList<>();

    /**
     * Business account model that created this Seller
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bam_id")
    private BusinessAccountModel businessAccountModel;

    /**
     * Customer invoice numbering sequences
     */
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerSequence> customerSequences = new ArrayList<>();
    
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentGateway> paymentGateways = new ArrayList<>();

    /**
     * General Ledger association
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "general_ledger_id")
    private GeneralLedger generalLedger;

    public Seller() {
        super();
    }

    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public TradingCountry getTradingCountry() {
        return tradingCountry;
    }

    public void setTradingCountry(TradingCountry tradingCountry) {
        this.tradingCountry = tradingCountry;
    }

    public TradingLanguage getTradingLanguage() {
        return tradingLanguage;
    }

    public void setTradingLanguage(TradingLanguage tradingLanguage) {
        this.tradingLanguage = tradingLanguage;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets the seller's VAT No
     * 
     * @return a VAT No
     *
     */
    public String getVatNo() {
        return vatNo;
    }

    /**
     * Sets the seller's VAT No
     * 
     * @param vatNo new VAT No
     */
    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    /**
     * Gets the seller's registration No
     * 
     * @return a registration No
     *
     */
    public String getRegistrationNo() {
        return registrationNo;
    }

    /**
     * Sets the seller's registration No
     * 
     * @param registrationNo new registration No
     */
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    /**
     * Gets the seller's legal text
     * 
     * @return a legal text
     */
    public String getLegalText() {
        return legalText;
    }

    /**
     * Sets the seller's legal text
     * 
     * @param legalText new legal text
     */
    public void setLegalText(String legalText) {
        this.legalText = legalText;
    }

    /**
     * Gets the seller's legal type
     * 
     * @return a legal type
     */
    public String getLegalType() {
        return legalType;
    }

    /**
     * Sets the seller's legal type
     * 
     * @param legalType new legal type
     */
    public void setLegalType(String legalType) {
        this.legalType = legalType;
    }

    /**
     * @return A parent seller in seller hierarchy
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller A parent seller in seller hierarchy
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * Get contact informations
     * 
     * @return contactInformation
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    /**
     * Set contact informations
     * 
     * @param contactInformation contactInformation
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public void setContactInformation(ContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (seller != null) {
            return new ICustomFieldEntity[] { seller };
        }
        return new ICustomFieldEntity[] { new Provider() };
    }

    public List<InvoiceTypeSellerSequence> getInvoiceTypeSequence() {
        return invoiceTypeSequence;
    }

    public void setInvoiceTypeSequence(List<InvoiceTypeSellerSequence> invoiceTypeSequence) {
        this.invoiceTypeSequence = invoiceTypeSequence;
    }

    public BusinessAccountModel getBusinessAccountModel() {
        return businessAccountModel;
    }

    public void setBusinessAccountModel(BusinessAccountModel businessAccountModel) {
        this.businessAccountModel = businessAccountModel;
    }

    @Override
    public BusinessEntity getParentEntity() {
        return seller;
    }

    public InvoiceTypeSellerSequence getInvoiceTypeSequenceByType(InvoiceType invoiceType) {
        for (InvoiceTypeSellerSequence seq : invoiceTypeSequence) {
            if (seq.getInvoiceType().equals(invoiceType)) {
                return seq;
            }
        }
        return null;
    }

    public boolean isContainsInvoiceTypeSequence(InvoiceType invoiceType) {
        InvoiceTypeSellerSequence seq = getInvoiceTypeSequenceByType(invoiceType);
        return seq != null;
    }

    @Override
    public Class<? extends BusinessEntity> getParentEntityType() {
        return Seller.class;
    }

    /**
     * Traverse seller hierarchy and find a seller that has a invoice numbering sequence for a given invoice type If the sequence not found on cust.seller, we try in seller.parent
     * (until seller.parent=null).
     * 
     * @param cfName Custom field name storing invoice numbering sequence
     * @param date Date
     * @param invoiceType Type of invoice
     * @return Chosen seller
     */
    public Seller findSellerForInvoiceNumberingSequence(String cfName, Date date, InvoiceType invoiceType) {
        if (getSeller() == null) {
            return this;
        }
        if (hasCfValue(cfName)) {
            return this;
        }
        if (invoiceType.getSellerSequence() != null && invoiceType.isContainsSellerSequence(this)) {
            return this;
        }

        return getSeller().findSellerForInvoiceNumberingSequence(cfName, date, invoiceType);
    }

    public List<CustomerSequence> getCustomerSequences() {
        return customerSequences;
    }

    public void setCustomerSequences(List<CustomerSequence> customerSequences) {
        this.customerSequences = customerSequences;
    }

    /**
     * @return the paymentGateways
     */
    public List<PaymentGateway> getPaymentGateways() {
        return paymentGateways;
    }

    /**
     * @param paymentGateways the paymentGateways to set
     */
    public void setPaymentGateways(List<PaymentGateway> paymentGateways) {
        this.paymentGateways = paymentGateways;
    }
    

    public GeneralLedger getGeneralLedger() {
        return generalLedger;
    }

    public void setGeneralLedger(GeneralLedger generalLedger) {
        this.generalLedger = generalLedger;
    }
}
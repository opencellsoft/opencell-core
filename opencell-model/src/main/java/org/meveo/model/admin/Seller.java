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
package org.meveo.model.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.GeneralLedger;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.PaymentGateway;

/**
 * Seller
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Khalid HORRI
 * @author Amine BEN AICHA
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */

@Entity
@WorkflowedEntity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "Seller", inheritCFValuesFrom = "seller", inheritFromProvider = true)
@ExportIdentifier({ "code" })
@DiscriminatorValue(value = "ACCT_S")
@Table(name = "crm_seller")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "crm_seller_seq"), })
public class Seller extends AccountEntity implements IWFEntity {

    public static final String ACCOUNT_TYPE = Seller.class.getAnnotation(DiscriminatorValue.class).value();
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
     * Customer invoice numbering sequences
     */
    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerSequence> customerSequences = new ArrayList<>();

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentGateway> paymentGateways = new ArrayList<>();

    /**
     * General Ledger association
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "general_ledger_id")
    private GeneralLedger generalLedger;

    public Seller() {
        accountType = ACCOUNT_TYPE;
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
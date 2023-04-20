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
package org.meveo.model.accountingScheme;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;

import javax.persistence.*;

@Entity
@Table(name = "accounting_accountingcode_mapping")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "accounting_accountingcode_mapping_seq")})
@NamedQueries({
        @NamedQuery(name = "AccountingCodeMapping.findByAccountingArticle",
                query = "SELECT accMap FROM AccountingCodeMapping accMap WHERE accMap.accountingArticle.id =:ACCOUNTING_ARTICLE_ID")
})
@Cacheable
public class AccountingCodeMapping extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_article_id")
    private AccountingArticle accountingArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_country_id")
    private TradingCountry billingCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_currency_id")
    private TradingCurrency billingCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_country_id")
    private TradingCountry sellerCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @Column(name = "criteria_el_value", length = 500)
    private String criteriaElValue;

    public AccountingArticle getAccountingArticle() {
        return accountingArticle;
    }

    public void setAccountingArticle(AccountingArticle accountingArticle) {
        this.accountingArticle = accountingArticle;
    }

    public TradingCountry getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(TradingCountry billingCountry) {
        this.billingCountry = billingCountry;
    }

    public TradingCurrency getBillingCurrency() {
        return billingCurrency;
    }

    public void setBillingCurrency(TradingCurrency billingCurrency) {
        this.billingCurrency = billingCurrency;
    }

    public TradingCountry getSellerCountry() {
        return sellerCountry;
    }

    public void setSellerCountry(TradingCountry sellerCountry) {
        this.sellerCountry = sellerCountry;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public String getCriteriaElValue() {
        return criteriaElValue;
    }

    public void setCriteriaElValue(String criteriaElValue) {
        this.criteriaElValue = criteriaElValue;
    }

}

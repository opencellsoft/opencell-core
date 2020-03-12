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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "billing_billing_run_list")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_billing_run_list_seq"), })
public class BillingRunList extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @Type(type = "numeric_boolean")
    @Column(name = "invoice")
    private Boolean invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    private BillingAccount billingAccount;

    @Column(name = "rated_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal RatedAmountWithoutTax = BigDecimal.ZERO;

    @Column(name = "rated_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal RatedAmountTax = BigDecimal.ZERO;

    @Column(name = "rated_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal RatedAmountWithTax = BigDecimal.ZERO;

    @Column(name = "rated_amount2_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal RatedAmount2WithoutTax = BigDecimal.ZERO;

    public Boolean getInvoice() {
        return invoice;
    }

    public void setInvoice(Boolean invoice) {
        this.invoice = invoice;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
        if (billingRun != null) {
            billingRun.getBillingRunLists().add(this);
        }
    }

    public BigDecimal getRatedAmountWithoutTax() {
        return RatedAmountWithoutTax;
    }

    public void setRatedAmountWithoutTax(BigDecimal ratedAmountWithoutTax) {
        RatedAmountWithoutTax = ratedAmountWithoutTax;
    }

    public BigDecimal getRatedAmountTax() {
        return RatedAmountTax;
    }

    public void setRatedAmountTax(BigDecimal ratedAmountTax) {
        RatedAmountTax = ratedAmountTax;
    }

    public BigDecimal getRatedAmountWithTax() {
        return RatedAmountWithTax;
    }

    public void setRatedAmountWithTax(BigDecimal ratedAmountWithTax) {
        RatedAmountWithTax = ratedAmountWithTax;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BigDecimal getRatedAmount2WithoutTax() {
        return RatedAmount2WithoutTax;
    }

    public void setRatedAmount2WithoutTax(BigDecimal ratedAmount2WithoutTax) {
        RatedAmount2WithoutTax = ratedAmount2WithoutTax;
    }

}

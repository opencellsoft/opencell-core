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


import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * View to enhance billing performance
 *
 * @author Wassim Drira
 */
@Entity
@Immutable
@Table(name="view_usage_charge_instances")
public class UsageChargeInstancesView {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "usage_charge_id")
    private Long usageChargeId;

    @Column(name = "charge_template_id")
    private Long chargeTemplateId;

    @Column(name = "user_account_id")
    private Long userAccountId;

    @Column(name="service_instance_id")
    private Long serviceInstanceId;

    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name="trading_currency")
    private Long tradingCurrency;

    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "charge_type")
    @Size(max = 1)
    private String chargeType;

    @Column(name = "status")
    private String status;

    @Column(name = "termination_date")
    private Date terminationDate;

    @Column(name = "subscription_id")
    private Long subscription;

    @Column(name = "priority")
    private int priority = 1;

    public Long getId() {
        return id;
    }

    public Long getUsageChargeId() {
        return usageChargeId;
    }

    public Long getChargeTemplateId() {
        return chargeTemplateId;
    }

    public Long getUserAccountId() {
        return userAccountId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public Long getTradingCurrency() {
        return tradingCurrency;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public String getChargeType() {
        return chargeType;
    }

    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    public String getStatus() {
        return status;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public Long getSubscription() {
        return subscription;
    }

    public int getPriority() {
        return priority;
    }
}

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

package org.meveo.apiv2.standardReport;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.payments.DunningLevelEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@XmlType(name = "agedReceivable", propOrder = {"customerAccountCode", "NotYetDue", "sum_1_30", "sum_31_60", "sum_61_90", "sum_90_up", "general_total"})
@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAgedReceivable.class)
public interface AgedReceivable extends Resource {
    @Nullable
    String getCustomerAccountCode();

    @Nullable
    BigDecimal getNotYetDue();

    @Nullable
    BigDecimal getSum_1_30();

    @Nullable
    BigDecimal getSum_31_60();

    @Nullable
    BigDecimal getSum_61_90();

    @Nullable
    BigDecimal getSum_90_up();

    @Nullable
    BigDecimal getGeneral_total();

    @Nullable
    Date dueDate();

    @Nullable
    String getCustomerAccountName();

    @Nullable
    String getCustomerAccountDescription();

    @Nullable
    String getSellerDescription();

    @Nullable
    String getSellerCode();

    @Nullable
    DunningLevelEnum getDunningLevel();

    @Nullable
    String getFuncCurrency();

    @Nullable
    List<BigDecimal> getNetAmountByPeriod();

    @Nullable
    List<BigDecimal> getTaxAmountByPeriod();

    @Nullable
    List<BigDecimal> getTotalAmountByPeriod();

    @Nullable
    String getInvoiceNumber();

    @Nullable
    Long getInvoiceId();

    @Nullable
    String getTradingCurrency();

    @Nullable
    BigDecimal getBilledAmount();

    @Nullable
    Long getCustomerId();

    @Nullable
    BigDecimal getTransactional_NotYetDue();

    @Nullable
    BigDecimal getTransactionalSum_1_30();

    @Nullable
    BigDecimal getTransactionalSum_31_60();

    @Nullable
    BigDecimal getTransactionalSum_61_90();

    @Nullable
    BigDecimal getTransactionalSum_90_Up();

    @Nullable
    BigDecimal getTransactional_GeneralTotal();

    @Nullable
    List<BigDecimal> getTransactional_NetAmountByPeriod();

    @Nullable
    List<BigDecimal> getTransactional_TaxAmountByPeriod();

    @Nullable
    List<BigDecimal> getTransactional_TotalAmountByPeriod();
}

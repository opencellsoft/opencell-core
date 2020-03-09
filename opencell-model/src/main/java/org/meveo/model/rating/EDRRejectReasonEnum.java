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

package org.meveo.model.rating;

public enum EDRRejectReasonEnum {

    SUBSCRIPTION_IS_NULL("SUBSCRIPTION_IS_NULL"), NO_MATCHING_CHARGE("NO_MATCHING_CHARGE"), SUBSCRIPTION_HAS_NO_CHARGE("SUBSCRIPTION_HAS_NO_CHARGE"), QUANTITY_IS_NULL(
            "QUANTITY_IS_NULL"), NO_PRICEPLAN("NO_PRICEPLAN"), NO_TAX("NO_TAX"), RATING_SCRIPT_EXECUTION_ERROR(
                    "RATING_SCRIPT_EXECUTION_ERROR"), PRICE_EL_ERROR("PRICE_EL_ERROR"), INSUFFICIENT_BALANCE(
                            "INSUFFICIENT_BALANCE"), CHARGING_EDR_ON_REMOTE_INSTANCE_ERROR("CHARGING_EDR_ON_REMOTE_INSTANCE_ERROR"), WALLET_NOT_FOUND("WALLET_NOT_FOUND");

    private String code;

    private EDRRejectReasonEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name();
    }

}

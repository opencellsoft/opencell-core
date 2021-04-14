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

package org.meveo.service.script.payment;

import java.util.List;
import java.util.Map;

import org.meveo.model.payments.AccountOperation;
import org.meveo.service.script.Script;

/**
 * An abstract class extending ScriptInterface, to be used by a custom script, basically to filter AOs to pay
 * 
 * @author Said Ramli
 */
public abstract class AccountOperationFilterScript extends Script {

    private static final long serialVersionUID = 5329117277858473758L;

    public static final String LIST_AO_TO_PAY = "LIST_AO_TO_PAY";
    public static final String FROM_DUE_DATE = "FROM_DUE_DATE";
    public static final String TO_DUE_DATE = "TO_DUE_DATE";
    public static final String PAYMENT_METHOD = "PAYMENT_METHOD";
    public static final String CAT_TO_PROCESS = "CAT_TO_PROCESS";

    public abstract List<AccountOperation> filterAoToPay(Map<String, Object> methodContext);

    public void checkPaymentRetry(Map<String, Object> methodContext) {
        // To be overridden by the custom script implementation ...
    }
}
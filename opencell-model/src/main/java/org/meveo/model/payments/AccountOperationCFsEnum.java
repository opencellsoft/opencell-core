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
package org.meveo.model.payments;

/**
 * EIR Account operation CFs enum
 *
 * @author Abdellatif BARI
 */
public enum AccountOperationCFsEnum {

    BILLING_ACCOUNT_NUMBER,
    TAX_RATES_CODE,
    PAYMENT_FILE_NAME,
    PAYMENT_FILE_ACCOUNT_CUSTOMER_NUMBER,
    FROM_BILLING_ACCOUNT_NUMBER,
    TO_BILLING_ACCOUNT_NUMBER,
    OPERATION_REASON
}
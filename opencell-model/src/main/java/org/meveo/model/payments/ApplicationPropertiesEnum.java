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

import org.meveo.commons.utils.ParamBean;

/**
 * EIR Application properties enum
 *
 * @author Abdellatif BARI
 */
public enum ApplicationPropertiesEnum {

    EIRCODE_ADDRESS_API_URL("eir.eircode.address.api.url"),
    ARD_ADDRESS_API_URL("eir.ard.address.api.url"),
    BROKER_ADDRESS_API_USER("broker_address_api_user", "opencell.admin"),
    BROKER_ADDRESS_API_PASSWORD("broker_address_api_password", "eir.admin"),
    DOWNLOAD_MAX_FILE_SIZE_MEGA_BYTE("eir.download.max.file.size.mega.byte"),
    RETRIES_NUMBER_ADDRESS_API("eir.retries.number.address.api", "1"),
    RETRY_DELAY_ADDRESS_API("eir.retry.delay.address.api", "2000"),
    TEMPLATE_TRANSFER_ACCOUNT_CREDIT("occ.templateTransferAccountCredit", "CRD_TRS"),
    TEMPLATE_TRANSFER_ACCOUNT_DEBIT("occ.templateTransferAccountDebit", "DBT_TRS"),
    TEMPLATE_REVERSE_PAYMENT_DEBIT("occ.templateReversePaymentDebit", "REV_PAY"),
    TEMPLATE_BATCH_PAYMENT_CREDIT("occ.templateBatchPaymentCredit", "PAY_BATCH"),
    DESCRIPTION_TRANSFER_FROM("occ.descTransferFrom", "transfer from"),
    DESCRIPTION_TRANSFER_TO("occ.descTransferTo", "transfer to"),
    OPENEIR_SELLER_CODE("eir.openeir.seller.code", "OPENEIR"),
    SUSPENSE_BILLING_ACCOUNT_CODE("eir.suspense.billingAccount.code", "SUSP_Payments"),
    INVOICE_DATE_FORMAT("invoice.dateFormat",  "dd/MM/yyyy"),
    INVOICE_DATE_TIME_FORMAT("invoice.dateTimeFormat",  "yyyy-MM-dd'T'HH:mm:ss");

    private String key;
    private String defaultValue;

    ApplicationPropertiesEnum(String key) {
        this.key = key;
    }

    ApplicationPropertiesEnum(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getProperty() {
        return ParamBean.getInstance().getProperty(key, defaultValue);
    }
}


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

package org.meveo.model.communication.email;

/**
 * An Enumeration for invoice mailing behavior:
 * If mailingType is "auto", automatically send the invoice after PDF is produced and when invoice.alreadySent is false.
 * If mailingType is "batch", the Job SendInvoiceJob  sends the invoice when PDF is available and invoice.alreadySent is false.
 * If mailingType is "manual", only the GUI action and API can send the invoice.
 *
 * @author HORRI Khalid
 * @lastModifiedVersion 7.0
 */
public enum MailingTypeEnum {

    MANUAL(1, "mailingType.manual","manual"), AUTO(2, "mailingType.auto","auto"), BATCH(3, "mailingType.batch","batch");

    private Integer id;
    private String label;
    private String name;

    private MailingTypeEnum(Integer id, String label,String name) {
        this.id = id;
        this.label = label;
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return name();
    }

    /**
     * Gets MailingTypeEnum basing on it's label
     * @param name's MailingTypeEnum
     * @return a MailingTypeEnum
     */
    public static MailingTypeEnum getByLabel(String name) {
        for (MailingTypeEnum mailingType : MailingTypeEnum.values()) {
            if (mailingType.getName().equalsIgnoreCase(name)) {
                return mailingType;
            }
        }
        return null;
    }
}

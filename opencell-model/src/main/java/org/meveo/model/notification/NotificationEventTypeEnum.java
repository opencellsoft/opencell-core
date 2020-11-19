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

package org.meveo.model.notification;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */

public enum NotificationEventTypeEnum {
    /**
     * Entity was created
     */
    CREATED,

    /**
     * Entity was updated
     */
    UPDATED,

    /**
     * Entity was deleted
     */
    REMOVED,

    /**
     * Entity was terminated
     */
    TERMINATED,

    /**
     * Entity was enabled
     */
    ENABLED,

    /**
     * Entity was disabled
     */
    DISABLED,

    /**
     * Job execution has completed
     */
    PROCESSED,

    /**
     * Failed to rate EDR or a recurring charge
     */
    REJECTED,

    /**
     * Failed to process CDR
     */
    REJECTED_CDR,

    /**
     * User has logged in
     */
    LOGGED_IN,

    /**
     * Received inbound request
     */
    INBOUND_REQ,

    /**
     * Reached low balance on prepaid wallet instance
     */
    LOW_BALANCE,

    /**
     * File was uploaded via FTP
     */
    FILE_UPLOAD,

    /**
     * File was downloaded via FTP
     */
    FILE_DOWNLOAD,

    /**
     * File was renamed via FTP
     */
    FILE_RENAME,

    /**
     * File was deleted via FTP
     */
    FILE_DELETE,

    /**
     * Counter has reached the value to alert on
     */
    COUNTER_DEDUCED,

    /**
     * Subscription or service renewal period is approaching
     */
    END_OF_TERM,

    /**
     * Subscription or service status was updated
     */
    STATUS_UPDATED,

    /**
     * Subscription or service renewal condition was updated
     */
    RENEWAL_UPDATED,

    /**
     * Generated XML for the invoice
     */
    XML_GENERATED,

    /**
     * Generated PDF for the invoice
     */
    PDF_GENERATED,

    /**
     * Invoice number assigned
     */
    INVOICE_NUMBER_ASSIGNED,

    /**
     * Dunning workflow status to R0
     */
    TO_R0,

    /**
     * Dunning workflow status to R1
     */
    TO_R1,

    /**
     * Dunning workflow status to R2
     */
    TO_R2,

    /**
     * New subscription version has been created
     */
    VERSION_CREATED,

    /**
     * Subscription version has been removed
     */
    VERSION_REMOVED;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}

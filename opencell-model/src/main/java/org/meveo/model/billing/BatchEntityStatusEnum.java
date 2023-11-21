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

/**
 * Batch entity status enum.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
public enum BatchEntityStatusEnum {

    /**
     * Batch is ready to be processed
     */
    OPEN,

    /**
     * Batch is being processed by a job instance
     */
    PROCESSING,

    /**
     * Batch has been successfully processed
     */
    SUCCESS,

    /**
     * Batch has been manually canceled
     */
    CANCELED,

    /**
     * Batch processing has failed (more details in job report)
     */
    FAILURE;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
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

package org.meveo.service.audit;

import org.meveo.model.audit.ChangeOriginEnum;

/**
 * Keep track of invocation source for the the auditing
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public class AuditOrigin {

    /**
     * Source of change
     */
    private static final ThreadLocal<ChangeOriginEnum> auditOrigin = new ThreadLocal<ChangeOriginEnum>() {
        @Override
        protected ChangeOriginEnum initialValue() {
            return ChangeOriginEnum.OTHER;
        }
    };

    /**
     * Source name of change
     */
    private static final ThreadLocal<String> auditOriginName = new ThreadLocal<String>();

    /**
     * Get source of change value
     *
     * @return Source of change
     */
    public static ChangeOriginEnum getAuditOrigin() {
        return auditOrigin.get();
    }

    /**
     * Set source of change value
     *
     * @param auditOriginNew Source of change
     */
    public static void setAuditOrigin(ChangeOriginEnum auditOriginNew) {
        auditOrigin.set(auditOriginNew);
    }

    /**
     * Get source name of change value
     *
     * @return Source name of change
     */
    public static String getAuditOriginName() {
        return auditOriginName.get();
    }

    /**
     * Set source name of change value
     *
     * @param auditOriginNameNew Source name of change
     */
    public static void setAuditOriginName(String auditOriginNameNew) {
        auditOriginName.set(auditOriginNameNew);
    }
    
    public static void setAuditOriginAndName(ChangeOriginEnum auditOriginNew, String auditOriginNameNew) {
        auditOrigin.set(auditOriginNew);
        auditOriginName.set(auditOriginNameNew);
    }
}
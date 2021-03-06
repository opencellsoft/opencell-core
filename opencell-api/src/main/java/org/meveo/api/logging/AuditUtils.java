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

package org.meveo.api.logging;

import org.meveo.commons.utils.ReflectionUtils;

import javax.interceptor.InvocationContext;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Path;
import java.lang.reflect.Method;

/**
 * Represents the audit utils class
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public class AuditUtils {

    public static String getAuditOrigin(InvocationContext invocationContext) {

        //REST API
        Method method = ReflectionUtils.getMethodFromInterface(invocationContext.getMethod(), Path.class);
        if (method != null) {
            StringBuilder auditOriginName = new StringBuilder();
            if (method.getDeclaringClass().getAnnotation(Path.class) != null) {
                auditOriginName.append(method.getDeclaringClass().getAnnotation(Path.class).value());
            }
            if (method.getAnnotation(Path.class) != null) {
                auditOriginName.append(method.getAnnotation(Path.class).value());
            }
            return auditOriginName.toString();
        //SOAP API
        } else {
            method = ReflectionUtils.getMethodFromInterface(invocationContext.getMethod(), WebService.class);
            if (method != null) {
                StringBuilder auditOriginName = new StringBuilder();
                auditOriginName.append(method.getDeclaringClass().getSimpleName());
                if (method.getAnnotation(WebMethod.class) != null) {
                    auditOriginName.append("/").append(method.getName());
                }
                return auditOriginName.toString();

            }
        }
        return null;
    }
}

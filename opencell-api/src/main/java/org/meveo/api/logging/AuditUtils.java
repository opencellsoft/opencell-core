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

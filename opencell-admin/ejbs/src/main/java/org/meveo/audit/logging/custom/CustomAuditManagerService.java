package org.meveo.audit.logging.custom;

import org.meveo.admin.exception.BusinessException;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author Khalid HORRI
 * @lastModifiedVersion 7.2
 **/
public interface CustomAuditManagerService extends Serializable {

    /**
     * Extends the audit method.
     *
     * @param clazz       the auditable class
     * @param method      the auditable method
     * @param paramValues param values
     * @throws BusinessException BusinessException
     */
    void audit(Class<? extends Object> clazz, Method method, Object[] paramValues) throws BusinessException;

}

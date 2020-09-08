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
package org.meveo.admin.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.ApplicationException;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.event.monitoring.CreateEventHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General business exception. Will result in data rollback
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 */
@ApplicationException(rollback = true)
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final boolean sendException;

    /**
     * An attribute name for capturing addition error information
     */
    public enum ErrorContextAttributeEnum {

        /**
         * Recurring rating period
         */
        RATING_PERIOD,

        /**
         * Charge instance
         */
        CHARGE_INSTANCE;
    }

    /**
     * Can provide any additional information needed to identify better the error occurred
     */
    private Map<String, Object> errorContext = null;

    static {
        ParamBean paramBean = ParamBean.getInstance();
        if (paramBean != null) {
            sendException = "true".equals(ParamBean.getInstance().getProperty("monitoring.sendException", "true"));
        } else {
            sendException = false;
        }
    }

    /**
     * Constructs a new exception
     */
    public BusinessException() {
        super();
        registerEvent();
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        registerEvent();
    }

    /**
     * Constructs a new exception with the specified detail message
     * 
     * @param message The detail message
     */
    public BusinessException(String message) {
        super(message);
        registerEvent();
    }

    /**
     * Constructs a new exception with the specified cause
     * 
     * @param cause the cause
     */
    public BusinessException(Throwable cause) {
        super(cause);
        registerEvent();
    }

    public void registerEvent() {
        if (sendException) {
            try {
                CreateEventHelper createEventHelper = (CreateEventHelper) EjbUtils.getServiceInterface("CreateEventHelper");
                createEventHelper.register(this);
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger(this.getClass());
                log.error("Failed to access event helper", e);
            }
        }
    }

    /**
     * @return Any additional information needed to identify better the error occurred
     */
    public Map<String, Object> getErrorContext() {
        return errorContext;
    }

    /**
     * @param errorContext Any additional information needed to identify better the error occurred
     */
    public void setErrorContext(Map<String, Object> errorContext) {
        this.errorContext = errorContext;
    }

    /**
     * Add Any additional information needed to identify better the error occurred
     * 
     * @param attribute Additional information attribute name
     * @param value Value
     * @return Additional information context to be able to chain methods
     */
    public Map<String, Object> addErrorContext(ErrorContextAttributeEnum attribute, Object value) {

        return addErrorContext(attribute.name(), value);
    }

    /**
     * Add Any additional information needed to identify better the error occurred
     * 
     * @param attribute Additional information attribute name
     * @param value Value
     * @return Additional information context to be able to chain methods
     */
    public Map<String, Object> addErrorContext(String attribute, Object value) {

        if (errorContext == null) {
            errorContext = new HashMap<>();
        }
        errorContext.put(attribute, value);

        return errorContext;
    }
}
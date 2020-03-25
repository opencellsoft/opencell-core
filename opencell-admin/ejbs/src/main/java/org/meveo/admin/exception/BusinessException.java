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

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.event.monitoring.CreateEventHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ApplicationException;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 */
@ApplicationException(rollback = true)
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private static final boolean sendException;
    
    static {
        sendException = "true".equals(ParamBean.getInstance().getProperty("monitoring.sendException", "true"));
    }
    
    public BusinessException() {
        super();
        registerEvent();
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        registerEvent();
    }
    
    public BusinessException(String message) {
        super(message);
        registerEvent();
    }
    
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
}

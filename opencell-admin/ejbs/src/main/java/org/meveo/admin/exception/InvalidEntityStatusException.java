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
package org.meveo.admin.exception;

import javax.ejb.ApplicationException;

import org.meveo.commons.utils.StringUtils;

/**
 * Indicates that action can not be performed because of a current entity status
 * 
 * @author Andrius Karpavicius
 *
 */
@ApplicationException(rollback = true)
public class InvalidEntityStatusException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param entityClass Entity class
     * @param code Entity code
     * @param action Action being performed
     * @param currentStatus Current entity status
     * @param requiredStatuses Statuses required to perform an action
     */
    @SuppressWarnings("rawtypes")
    public InvalidEntityStatusException(Class entityClass, String code, String action, Enum currentStatus, Enum... requiredStatuses) {
        super("Action " + action + " can not be performed on " + entityClass.getSimpleName() + "/" + code + " with status " + currentStatus + ". Allowed statuses are "
                + StringUtils.concatenateWithSeparator(", ", requiredStatuses));
    }
}
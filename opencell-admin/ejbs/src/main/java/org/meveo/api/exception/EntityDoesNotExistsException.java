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

package org.meveo.api.exception;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * @author Edward P. Legaspi
 **/
public class EntityDoesNotExistsException extends MeveoApiException {

	private static final long serialVersionUID = 4814463369593237028L;

	public EntityDoesNotExistsException(String entityName, String code) {
		super(entityName + " with code=" + code + " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(String message) {
		super(message);
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String code) {
		super(clazz.getSimpleName() + " with code=" + code
				+ " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, Long id) {
		super(clazz.getSimpleName() + " with id=" + id + " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String value,
			String field) {
		super(clazz.getSimpleName() + " with " + field + "=" + value
				+ " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}
	
	public EntityDoesNotExistsException(Class<?> clazz, String value1,
			String field1,String value2,String field2) {
		super(clazz.getSimpleName() + " with " + field1 + "=" + value1 +" and/or " + field2 + "=" + value2+ " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}
	
	public EntityDoesNotExistsException(String dbTableName, List<Long> ids) {
		super(dbTableName + " with ids in( " + ids.stream().map(x->""+x).collect(Collectors.joining(",")) + " ) does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

	public EntityDoesNotExistsException(Class<?> clazz, String code, Date date){
		super(clazz.getSimpleName() + " with code=" + code + (date == null ? "" : " and valid on " + date)  + " does not exists.");
		setErrorCode(MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
	}

    /**
     * Stacktrace is not of interest here
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}

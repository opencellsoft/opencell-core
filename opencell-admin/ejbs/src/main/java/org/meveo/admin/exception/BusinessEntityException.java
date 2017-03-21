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

public class BusinessEntityException extends BusinessException {

	private static final long serialVersionUID = 1L;

	private String id;
	
	public BusinessEntityException() {
		super();
	}

	public BusinessEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessEntityException(String message) {
		super(message);
	}

	public BusinessEntityException(Throwable cause) {
		super(cause);
	}

	public BusinessEntityException(String message, String id,Throwable cause) {
		super(message, cause);
		this.id=id;
	}

	public BusinessEntityException(String message, String id) {
		super(message);
		this.id=id;
	}

	public BusinessEntityException(Throwable cause, String id) {
		super(cause);
		this.id=id;
	}

	public String getId() {
		return id;
	}
}

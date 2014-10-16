/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.admin.exception;



/**
 * @author R.AITYAAZZA
 *
 */
public class DuplicateDefaultAccountException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public DuplicateDefaultAccountException(String description) {
		super(description);
	}
	public DuplicateDefaultAccountException() {
		super("default account is already exist for this level");
	}
	

	
	

}

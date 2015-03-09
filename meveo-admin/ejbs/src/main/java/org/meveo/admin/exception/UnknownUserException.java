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
package org.meveo.admin.exception;import org.meveo.model.admin.User;

/**
 * Exception thrown when {@link User} entity does not exist
 * 
 */
public class UnknownUserException extends LoginException {

	/** */
	private static final long serialVersionUID = 1L;

	public UnknownUserException(Long id) {
		super("User with id='" + id + "' does not  exist.");
	}

	public UnknownUserException(String username) {
		super("User with username=" + username + " does not  exist.");
	}

}
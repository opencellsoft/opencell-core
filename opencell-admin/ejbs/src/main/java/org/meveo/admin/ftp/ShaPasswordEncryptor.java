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

package org.meveo.admin.ftp;

import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.meveo.admin.util.security.Sha1Encrypt;

public class ShaPasswordEncryptor implements PasswordEncryptor {

	@Override
	public String encrypt(String password) {
		return Sha1Encrypt.encodePassword(password);
	}

	@Override
	public boolean matches(String passwordToCheck, String storedPassword) {
		if (storedPassword == null) {
			throw new NullPointerException("storedPassword can not be null");
		}
		if (passwordToCheck == null) {
			throw new NullPointerException("passwordToCheck can not be null");
		}

		return encrypt(passwordToCheck).equalsIgnoreCase(storedPassword);
	}

}

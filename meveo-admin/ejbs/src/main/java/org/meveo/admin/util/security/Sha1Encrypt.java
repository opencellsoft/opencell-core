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
package org.meveo.admin.util.security;

import java.security.MessageDigest;
/**
 * Secure Hash Algorithm 1, a message-digest algorithm
 * @author liur
 *
 */
public class Sha1Encrypt {
	
	/**
	 * Encode a string, return the resulting encrypted password.
	 * @param password
	 * @return String
	 */
	public static String encodePassword(String password) {
		byte[] unencodedPassword = password.getBytes();
		MessageDigest md = null;
		try {
			
			md = MessageDigest.getInstance("SHA-1");

		} catch (Exception e) {

			return password;
		}
		md.reset();
		md.update(unencodedPassword);

		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < encodedPassword.length; i++) {
			if (((int) encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) encodedPassword[i] & 0xff, 16));
		}
		return buf.toString();
	}

}

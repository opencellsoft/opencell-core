/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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

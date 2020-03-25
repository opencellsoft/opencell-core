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
package org.meveo.admin.util.security;

import java.security.MessageDigest;
/**
 * Secure Hash Algorithm 1, a message-digest algorithm
 * @author liur
 *
 */
public class Sha1Encrypt {

    public static final String SHA1 = "SHA-1";
    public static final String SHA256 = "SHA-256";
    public static final String SHA224 = "SHA-224";

    /**
     * Encode a string, return the resulting encrypted password.
     * 
     * @param password password
     * @return encoded password
     */
    public static String encodePassword(String password) {
        return encodePassword(password, SHA1);
    }

    /**
     * @param password password
     * @param algorithm algorith used to encode
     * @return encoded password
     */
    public static String encodePassword(String password, String algorithm) {
        byte[] unencodedPassword = password.getBytes();
        MessageDigest md = null;
        try {

            md = MessageDigest.getInstance(algorithm);

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

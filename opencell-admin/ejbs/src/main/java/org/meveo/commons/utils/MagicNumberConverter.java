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
package org.meveo.commons.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


/**
 * Class to help converting MagicNumber values to different representations.
 * 
 * @author Ignas Lelys
 *
 */
public class MagicNumberConverter {

    /** Characters representing hexadecimal bytes. */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * Converts MagicNumber from string to byte array representation.
     * 
     * @param hashString Hash value as String.
     * @return Hash value as byte array.
     */
    public static byte[] convertToArray(String hashString) {
        int byteCount = hashString.length() / 2 + (hashString.length() % 2);
        byte[] decoded = new byte[byteCount];
        CharacterIterator it = new StringCharacterIterator(hashString);
        int i = 0;
        for (char firstHex = it.first(), secondHex = it.next(); firstHex != CharacterIterator.DONE
                && secondHex != CharacterIterator.DONE; firstHex = it.next(), secondHex = it.next()) {
            int hashByte = Character.digit(firstHex, 16);
            hashByte = hashByte << 4;
            hashByte += Character.digit(secondHex, 16);
            decoded[i] = (byte) hashByte;
            i++;
        }
        return decoded;
    }

    /**
     * Converts MagicNumber from byte array to string representation.
     * 
     * @param hashValue Hash value as byte array.
     * @return Hash value as String.
     */
    public static String convertToString(byte[] hashValue) {
        StringBuilder result = new StringBuilder(hashValue.length * 2);
        for (int i = 0; i < hashValue.length; i++) {
            byte b = hashValue[i];
            result.append(DIGITS[(b & 0xf0) >> 4]);
            result.append(DIGITS[b & 0x0f]);
        }
        return result.toString();
    }

}

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

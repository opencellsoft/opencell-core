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
package org.meveo.commons.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


/**
 * Class to help converting MagicNumber values to different representations.
 * 
 * @author Ignas Lelys
 * @created Apr 7, 2009
 *
 */
public class MagicNumberConverter {
    
    /** Characters representing hexadecimal bytes. */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };
    
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
     * @param hashString Hash value as byte array.
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

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

/**
 * Utils class to help for ASN1 files parsing. 
 * 
 * @author Ignas Lelys
 * @created Apr 7, 2009
 *
 */
public class ASN1Utils {

    /**
     * Checks if 6th bit is one or zero (0x20 = 00100000b). If bit 1 - type is
     * contructed, and if 0 - otherwise.
     * 
     * @param type
     *            Type to check.
     * @return true if that type is constructed, false otherwise.
     */
    public static boolean isTypeConstructed(int type) {
        return (type & 0x20) == 0x20;
    }

    /**
     * Checks if 8th bit is one or zero (0x80 = 10000000b). If length octet is
     * long form then last seven bits shows how much octets are used for lenght.
     * Otherwise last 7 bits is actual length.
     * 
     * @param lenghtOctet
     *            Lenght octet to check
     * @return
     */
    public static boolean isLongFormLenghtOctet(int lenghtOctet) {
        return (lenghtOctet & 0x80) == 0x80;
    }

    /**
     * Return what is real tag value (last 5 bits).
     * 
     * @param tag
     *            tag to check.
     * @return Tag value.
     */
    public static int getTagValue(int tag) {
        return tag & 31;
    }

}

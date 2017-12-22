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

/**
 * Utils class to help for ASN1 files parsing. 
 * 
 * @author Ignas Lelys
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
     * @return true/false
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

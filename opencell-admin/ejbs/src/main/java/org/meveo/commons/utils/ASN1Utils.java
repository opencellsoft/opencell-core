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

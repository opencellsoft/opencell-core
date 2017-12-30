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
 * File Format enumeration used in Medina.
 * 
 * @author Donatas Remeika
 * 
 */
public enum FileFormat {

    ASN, CSV, TXT, OTHER;

    /**
     * Parse FileFormat from extension. Return OTHER if extension is not
     * defined.
     * 
     * @param extension
     *            File name extension.
     * @return FileFormat enum.
     */
    public static FileFormat parseFromExtension(String extension) {
        if (extension == null) {
            return OTHER;
        }
        extension = extension.toUpperCase();
        try {
            return FileFormat.valueOf(extension);
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}

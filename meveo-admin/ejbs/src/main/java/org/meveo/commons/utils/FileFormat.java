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
 * File Format enumeration used in Medina.
 * 
 * @author Donatas Remeika
 * @created Mar 6, 2009
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

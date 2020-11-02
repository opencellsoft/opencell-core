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

package org.meveo.service.index;

import java.io.Serializable;

import org.meveo.model.BaseEntity;
import org.meveo.model.customEntities.CustomTableRecord;

/**
 * A mapping between providerCode, classname, custom entity code (if applicable) and index name and type (if applicable)
 * 
 * A cache key: providerCode, classname, custom entity code (if applicable)
 * 
 * @author Andrius Karpavicius
 */
@Deprecated
public class CacheKeyESIndex implements Serializable {

    private static final long serialVersionUID = -7102146427175802501L;
    /**
     * Provider code
     */
    private String providerCode;

    /**
     * Full class name
     */
    private String classname;

    /**
     * Custom entity template code
     */
    private String cetCode;

    /**
     * Constructor
     */
    public CacheKeyESIndex() {
    }

    /**
     * Constructor
     * 
     * @param providerCode Provider code
     * @param classname Full class name
     * @param cetCode Custom entity template code
     */
    public CacheKeyESIndex(String providerCode, String classname, String cetCode) {
        this.providerCode = providerCode;
        this.classname = classname;

        if (classname.equals(CustomTableRecord.class.getName())) {
            cetCode = BaseEntity.cleanUpAndLowercaseCodeOrId(cetCode);
        }
        this.cetCode = cetCode;
    }

    @Override
    public String toString() {
        return providerCode + ", " + classname + ", " + cetCode;
    }

    @Override
    public boolean equals(Object obj) {
        CacheKeyESIndex other = (CacheKeyESIndex) obj;

        if (other == null) {
            return false;
        }

        return (providerCode + "_" + classname + "_" + cetCode).equals(other.providerCode + "_" + other.classname + "_" + other.cetCode);
    }

    @Override
    public int hashCode() {
        return (providerCode + "_" + classname + "_" + cetCode).hashCode();
    }

    /**
     * Does provider value match
     * 
     * @param codeToMatch Provider code to match
     * @return True of provider code matches
     */
    public boolean isMatchProvider(String codeToMatch) {
        return (providerCode == null && codeToMatch == null) || (providerCode != null && providerCode.equals(codeToMatch));
    }

    /**
     * @return Provider code
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * @return Full class name
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @return Custom entity template code
     */
    public String getCetCode() {
        return cetCode;
    }
}

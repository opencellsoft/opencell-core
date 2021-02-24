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

/**
 * A mapping between providerCode, classname, custom entity code (if applicable) and index name and type (if applicable)
 * 
 * A cache value: index name and type (if applicable)
 * 
 * @author Andrius Karpavicius
 */
@Deprecated
public class ESIndexNameAndType implements Serializable {

    private static final long serialVersionUID = 2183194936642780997L;

    /**
     * Index name
     */
    private String indexName;

    /**
     * Entity type
     */
    private String type;

    /**
     * Constructor
     */
    public ESIndexNameAndType() {
    }

    public ESIndexNameAndType(String indexName, String type) {
        super();
        this.indexName = indexName;
        this.type = type;
    }

    @Override
    public String toString() {
        return indexName + ", " + type;
    }

    @Override
    public boolean equals(Object obj) {
        ESIndexNameAndType other = (ESIndexNameAndType) obj;

        if (other == null) {
            return false;
        }

        return (indexName + "_" + type).equals(other.getIndexName() + "_" + other.getType());
    }

    @Override
    public int hashCode() {
        return (indexName + "_" + type).hashCode();
    }

    public String getIndexName() {
        return indexName;
    }

    public String getType() {
        return type;
    }

    /**
     * Does the index and type match
     * 
     * @param matchIndexName Full index name to match
     * @param matchType Type to match (optional)
     * @return True if index and type match
     */
    public boolean isMatchIndexNameAndType(String matchIndexName, String matchType) {

        return (this.indexName.equals(matchIndexName) || matchIndexName.startsWith(this.indexName))
                && ((this.type == null && matchType == null) || (this.type != null && this.type.equals(matchType)));
    }
}
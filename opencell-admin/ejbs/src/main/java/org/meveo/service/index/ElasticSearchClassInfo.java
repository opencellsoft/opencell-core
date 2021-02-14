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

import org.meveo.model.ISearchable;

/**
 * Contains info for search scope (index and type) calculation - either by a class name or CET code
 * 
 * @author Andrius Karpavicius
 */
@Deprecated
public class ElasticSearchClassInfo {

    /**
     * Entity class
     */
    private Class<? extends ISearchable> clazz;

    /**
     * Custom entity template code
     */
    private String cetCode;

    public ElasticSearchClassInfo(Class<? extends ISearchable> clazz, String cetCode) {
        super();
        this.clazz = clazz;
        this.cetCode = cetCode;
    }

    /**
     * 
     * @return Entity class
     */
    public Class<? extends ISearchable> getClazz() {
        return clazz;
    }

    /**
     * @return Custom entity template code
     */
    public String getCetCode() {
        return cetCode;
    }

    @Override
    public String toString() {
        return String.format("ElasticSearchClassInfo [clazz=%s, cetCode=%s]", clazz, cetCode);
    }
}
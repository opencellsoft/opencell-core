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
package org.meveo.api.dto;

import java.util.Date;

/**
 * Represent an entity that is versioned by validity dates.
 * 
 * @author Andrius Karpavicius
 * @since 5.1
 */
public interface IVersionedDto {

    /**
     * Gets the valid from.
     *
     * @return the valid from
     */
    Date getValidFrom();

    /**
     * Sets the valid from.
     *
     * @param validFrom the new valid from
     */
    void setValidFrom(Date validFrom);

    /**
     * Gets the valid to.
     *
     * @return the valid to
     */
    Date getValidTo();

    /**
     * Sets the valid to.
     *
     * @param validTo the new valid to
     */
    void setValidTo(Date validTo);
}
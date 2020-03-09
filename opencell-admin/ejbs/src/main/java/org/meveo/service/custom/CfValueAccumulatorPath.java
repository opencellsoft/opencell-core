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

package org.meveo.service.custom;

import java.io.Serializable;

/**
 * Identifies a path to get to an entity of a given class. Used in custom field value inheritance to traverse inheritance hierarchy
 * 
 * @author Andrius Karpavicius
 *
 */
public class CfValueAccumulatorPath implements Serializable {

    private static final long serialVersionUID = -2728458306097788409L;

    /**
     * Target entity class
     */
    private Class<?> clazz;

    /**
     * Path to get to the target entity
     */
    private String path;

    /**
     * Constructor
     */
    public CfValueAccumulatorPath() {

    }

    /**
     * Construct a path for a given targe entity
     * 
     * @param clazz Target entity class
     * @param path Path to get to the target entity
     */
    public CfValueAccumulatorPath(Class<?> clazz, String path) {
        super();
        this.clazz = clazz;
        this.path = path;
    }

    /**
     * 
     * @return Target entity class
     */
    public Class<?> getClazz() {
        return clazz;
    }

    /**
     * 
     * @param clazz Target entity class
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 
     * @return Path to get to the target entity
     */
    public String getPath() {
        return path;
    }

    /**
     * 
     * @param path Path to get to the target entity
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CfValueAccumulatorPath [clazz=" + clazz + ", path=" + path + "]";
    }

    @Override
    public boolean equals(Object obj) {
        CfValueAccumulatorPath other = (CfValueAccumulatorPath) obj;
        return clazz.equals(other.getClazz()) && ((path == null && other.getPath() == null) || (path != null && path.equals(other.getPath())));
    }
}
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
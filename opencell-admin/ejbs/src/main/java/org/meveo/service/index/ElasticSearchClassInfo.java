package org.meveo.service.index;

import org.meveo.model.ISearchable;

/**
 * Contains info for search scope (index and type) calculation - either by a class name or CET code
 * 
 * @author Andrius Karpavicius
 */
public class ElasticSearchClassInfo {

    /**
     * Entity class
     */
    private Class<? extends ISearchable> clazz;

    /**
     * CET template code
     */
    private String cetCode;

    public ElasticSearchClassInfo(Class<? extends ISearchable> clazz, String cetCode) {
        super();
        this.clazz = clazz;
        this.cetCode = cetCode;
    }

    public Class<? extends ISearchable> getClazz() {
        return clazz;
    }

    public String getCetCode() {
        return cetCode;
    }

    @Override
    public String toString() {
        return String.format("ElasticSearchClassInfo [clazz=%s, cetCode=%s]", clazz, cetCode);
    }
}
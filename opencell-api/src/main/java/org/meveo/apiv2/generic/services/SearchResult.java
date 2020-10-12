package org.meveo.apiv2.generic.services;

import org.meveo.model.IEntity;

import java.util.List;

/**
 * Class to wrap the search result of an entity
 *
 * @author mounir Boukayoua
 * @since 10.X
 */
public class SearchResult {

    private List<? extends IEntity> entityList;

    private long count;

    /**
     * default constructor
     */
    public SearchResult(){}


    public SearchResult(List<? extends IEntity> entityList, long count) {
        this.entityList = entityList;
        this.count = count;
    }

    /**
     * @return List of the searched entity
     */
    public List<? extends IEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<? extends IEntity> entityList) {
        this.entityList = entityList;
    }

    /**
     * @return The total count of the searched entity
     */
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

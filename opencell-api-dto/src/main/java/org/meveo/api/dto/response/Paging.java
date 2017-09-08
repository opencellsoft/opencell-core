package org.meveo.api.dto.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Paging implements Serializable {

    private static final long serialVersionUID = 4367485228070123385L;

    /**
     * Pagination - from item number
     */
    public Integer from;

    /**
     * Pagination - number of items per page
     */
    public Integer nrOfItems = 100;

    /**
     * Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     */
    public String sortBy;

    /**
     * Sorting - sort order.
     */
    public SortOrder sortOrder;

    /**
     * Total of items. Note - filled on response only.
     */
    public Integer count;

    public enum SortOrder {
        ASCENDING, DESCENDING;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getNrOfItems() {
        return nrOfItems;
    }

    public void setNrOfItems(Integer nrOfItems) {
        this.nrOfItems = nrOfItems;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
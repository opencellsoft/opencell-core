package org.meveo.api.dto.response;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Paging implements Serializable {

    private static final long serialVersionUID = 4367485228070123385L;

    /**
     * Pagination - from record number
     */
    public Integer offset;

    /**
     * Pagination - number of items to retrieve
     */
    public Integer limit = 100;

    /**
     * Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     */
    public String sortBy;

    /**
     * Sorting - sort order.
     */
    public SortOrder sortOrder;

    /**
     * Total number of records. Note - filled on response only.
     */
    public Integer totalNumberOfRecords;

    public enum SortOrder {
        ASCENDING, DESCENDING;
    }

    public Paging() {

    }

    public Paging(Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super();
        this.offset = offset;
        this.limit = limit;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
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

    public Integer getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public void setTotalNumberOfRecords(Integer count) {
        this.totalNumberOfRecords = count;
    }
}
package org.meveo.api.security.config;

public class FilterResultsConfig {

    /**
     * Identifies the DTO property that returns the child entities to be iterated for filtering. e.g. if "customerAccounts.customerAccount" is passed into this attribute, then the
     * value of "dto.customerAccounts.customerAccount" will be parsed and filtered.
     *
     * If not specified - an object itself will be filtered
     *
     * @return name of property to be filtered
     */
    private String propertyToFilter = "";

    /**
     * Identifies the filtering rule to apply to items selected for filtering.
     *
     * @return array of property to be filtered.
     */
    private FilterPropertyConfig[] itemPropertiesToFilter;

    /**
     * Identifies the DTO property containing the number of records returned to refresh after applying filter
     *
     */
    private String totalRecords = "";

    public String getPropertyToFilter() {
        return propertyToFilter;
    }

    public void setPropertyToFilter(String propertyToFilter) {
        this.propertyToFilter = propertyToFilter;
    }

    public FilterPropertyConfig[] getItemPropertiesToFilter() {
        return itemPropertiesToFilter;
    }

    public void setItemPropertiesToFilter(FilterPropertyConfig[] itemPropertiesToFilter) {
        this.itemPropertiesToFilter = itemPropertiesToFilter;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }
}

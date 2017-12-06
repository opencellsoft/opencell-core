package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PagingAndFiltering implements Serializable {

    private static final long serialVersionUID = 4367485228070123385L;

    /**
     * Full text search filter. Mutually exclusive with filters attribute. fullTextFilter has priority
     */
    private String fullTextFilter;

    /**
     * Search filters (key = Filter key, value = search pattern or value).
     * 
     * Filter key can be:
     * <ul>
     * <li>"$FILTER". Value is a filter name</li>
     * <li>"type_class". Value is a full classname. Used to limit search results to a particular entity type in case of entity subclasses. Can be combined to condition "ne" to
     * exclude those classes.</li>
     * <li>SQL. Additional sql to apply. Value is an array consisting of sql query and one or more paramaters to apply</li>
     * <li>&lt;condition&gt; &lt;fieldname1&gt; &lt;fieldname2&gt; ... &lt;fieldnameN&gt;. Value is a value to apply in condition</li>
     * </ul>
     * 
     * A union between different filter items is AND.<br/>
     * <br/>
     * 
     * Condition is optional. Number of fieldnames depend on condition used. If no condition is specified an "equals ignoring case" operation is considered.<br/>
     * <br/>
     * 
     * Following conditions are supported:
     * <ul>
     * <li>fromRange. Ranged search - field value in between from - to values. Specifies "from" part value: e.g value<=field.value. Applies to date and number type fields.</li>
     * <li>toRange. Ranged search - field value in between from - to values. Specifies "to" part value: e.g field.value<=value</li>
     * <li>list. Value is in field's list value. Applies to date and number type fields.</li>
     * <li>inList/not-inList. Field value is [not] in value (list). A comma separated string will be parsed into a list if values. A single value will be considered as a list value of one item</li>
     * <li>minmaxRange. The value is in between two field values. TWO field names must be provided. Applies to date and number type fields.</li>
     * <li>minmaxOptionalRange. Similar to minmaxRange. The value is in between two field values with either them being optional. TWO fieldnames must be specified.</li>
     * <li>overlapOptionalRange. The value range is overlapping two field values with either them being optional. TWO fieldnames must be specified. Value must be an array of two
     * values.</li>
     * <li>likeCriterias. Multiple fieldnames can be specified. Any of the multiple field values match the value (OR criteria). In case value contains *, a like criteria match will
     * be used. In either case case insensative matching is used. Applies to String type fields.</li>
     * <li>wildcardOr. Similar to likeCriterias. A wildcard match will always used. A * will be appended to start and end of the value automatically if not present. Applies to
     * String type fields.</li>
     * <li>ne. Not equal.
     * </ul>
     * 
     * Following special meaning values are supported:
     * <ul>
     * <li>IS_NULL. Field value is null</li>
     * <li>IS_NOT_NULL. Field value is not null</li>
     * </ul>
     * 
     * 
     * To filter by a related entity's field you can either filter by related entity's field or by related entity itself specifying code as value. These two example will do the
     * same in case when quering a customer account: customer.code=aaa OR customer=aaa<br/>
     * 
     * To filter a list of related entities by a list of entity codes use "inList" on related entity field. e.g. for quering offer template by sellers: inList
     * sellers=code1,code2<br/><br/>
     * 
     * <b>Note:</b> Quering by related entity field directly will result in exception when entity with a specified code does not exists <br/>
     * <br/>
     * 
     * 
     * Examples:<br/>
     * <ul>
     * <li>invoice number equals "1578AU":<br/>
     * Filter key: invoiceNumber. Filter value: 1578AU</li>
     * <li>invoice number is not "1578AU":<br/>
     * Filter key: ne invoiceNumber. Filter value: 1578AU</li>
     * <li>invoice number is null:<br/>
     * Filter key: invoiceNumber. Filter value: IS_NULL</li>
     * <li>invoice number is not empty:<br/>
     * Filter key: invoiceNumber. Filter value: IS_NOT_NULL</li>
     * <li>Invoice date is between 2017-05-01 and 2017-06-01:<br/>
     * Filter key: fromRange invoiceDate. Filter value: 2017-05-01<br/>
     * Filter key: toRange invoiceDate. Filter value: 2017-06-01</li>
     * <li>Date is between creation and update dates:<br/>
     * Filter key: minmaxRange audit.created audit.updated. Filter value: 2017-05-25</li>
     * <li>invoice number is any of 158AU, 159KU or 189LL:<br/>
     * Filter key: inList invoiceNumber. Filter value: 158AU,159KU,189LL</li>
     * <li>any of param1, param2 or param3 fields contains "energy":<br/>
     * Filter key: wildcardOr param1 param2 param3. Filter value: energy</li>
     * <li>any of param1, param2 or param3 fields start with "energy":<br/>
     * Filter key: likeCriterias param1 param2 param3. Filter value: *energy</li>
     * <li>any of param1, param2 or param3 fields is "energy":<br/>
     * Filter key: likeCriterias param1 param2 param3. Filter value: energy</li>
     * </ul>
     * 
     * NOTE: Filters passed as string in Rest GET type method are in the following format: filterKey1:filterValue1|filterKey2:filterValue2
     * 
     */
    private Map<String, Object> filters;

    /**
     * Data retrieval options/fieldnames separated by a comma
     */
    private String fields;

    /**
     * Pagination - from record number
     */
    private Integer offset;

    /**
     * Pagination - number of items to retrieve
     */
    private Integer limit = 100;

    /**
     * Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     */
    private String sortBy;

    /**
     * Sorting - sort order.
     */
    private SortOrder sortOrder;

    /**
     * Total number of records. Note - filled on response only.
     */
    private Integer totalNumberOfRecords;

    public enum SortOrder {
        ASCENDING, DESCENDING;
    }

    public PagingAndFiltering() {

    }

    /**
     * Paging and filtering criteria
     * 
     * @param encodedQuery Encoded query in format: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Fields to retrieve
     * @param offset Retrieve from record number
     * @param limit How many records to retrieve
     * @param sortBy Sort by field name
     * @param sortOrder Sort order
     */
    public PagingAndFiltering(String encodedQuery, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super();
        this.filters = decodeQuery(encodedQuery);
        this.fields = fields;
        this.offset = offset;
        this.limit = limit;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    /**
     * Paging and filtering criteria
     * 
     * @param fullTextFilter Full text filter query
     * @param filters Filtering criteria - a map of field names and values. See PersistenceService.getQuery for more details.
     * @param fields Fields to retrieve
     * @param offset Retrieve from record number
     * @param limit How many records to retrieve
     * @param sortBy Sort by field name
     * @param sortOrder Sort order
     */
    public PagingAndFiltering(String fullTextFilter, Map<String, Object> filters, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        super();
        this.fullTextFilter = fullTextFilter;
        this.filters = filters;
        this.fields = fields;
        this.offset = offset;
        this.limit = limit;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public String getFullTextFilter() {
        return fullTextFilter;
    }

    public String getFields() {
        return fields;
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

    /**
     * Decode string type query to a filter criteria map. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * 
     * @param query Encoded filter criteria as a string.
     * @return A decoded filter criteria map
     */
    private Map<String, Object> decodeQuery(String query) {

        if (query == null) {
            return null;
        }

        Map<String, Object> filters = new HashMap<String, Object>();

        String[] splitByItem = query.split("\\|");

        for (String filterItem : splitByItem) {
            String[] filterItems = filterItem.split(":");
            filters.put(filterItems[0], filterItems[1]);
        }

        return filters;
    }

    /**
     * Check if a given field retrieval option is enabled
     * 
     * @param fieldOption
     * @return
     */
    public boolean hasFieldOption(String fieldOption) {
        if (fields == null) {
            return false;
        } else {
            return fields.contains(fieldOption);
        }
    }

    public void addFilter(String key, Object value) {
        if (filters == null) {
            filters = new HashMap<>();
        }

        filters.put(key, value);
    }
}
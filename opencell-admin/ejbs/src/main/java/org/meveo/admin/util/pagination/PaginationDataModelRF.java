package org.meveo.admin.util.pagination;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

//import org.ajax4jsf.model.DataVisitor;
//import org.ajax4jsf.model.ExtendedDataModel;
//import org.ajax4jsf.model.Range;
//import org.ajax4jsf.model.SequenceRange;
import org.meveo.model.IEntity;
//import org.richfaces.component.SortOrder;
//import org.richfaces.model.Arrangeable;
//import org.richfaces.model.ArrangeableState;
//import org.richfaces.model.FilterField;
//import org.richfaces.model.SortField;
import org.primefaces.model.SortOrder;

/**
 * Pagination model implementation. To make sorting work, initiate object with sorting options.
 * 
 * @author Andrius Karpavicius
 * 
 * @param <T> Base class
 */
//public abstract class PaginationDataModelRF<T> extends ExtendedDataModel<T> implements Arrangeable, Serializable {

public abstract class PaginationDataModelRF<T> extends DataModel<T> implements Serializable {

    private static final long serialVersionUID = -4528523844716548059L;

    private Integer rowCount;
    private boolean isDetached = false;

    private Map<String, SortOrder> sortOrders = new HashMap<String, SortOrder>();

    private Map<String, String> filterValues = new HashMap<String, String>();

    private Object rowKey;

    private String sortProperty;

//    private ArrangeableState arrangeableState;

    private int currentFirstRow = -1;

    private int currentPage = 1;

    private final List<Serializable> wrappedKeys = new LinkedList<Serializable>();

    private final Map<Serializable, T> wrappedData = new HashMap<Serializable, T>();

    protected abstract List<T> loadData(PaginationConfiguration paginatingData);

    protected abstract int countRecords(PaginationConfiguration paginatingData);

    public PaginationDataModelRF() {
        super();
    }

    /**
     * Setup ordering of fields
     * 
     * @param defaultSortField Field that should be sorted by default (or null if none)
     * @param sortAscending Sort order for default sorting field
     * @param unsortedFields Remaining fields
     */
    public PaginationDataModelRF(String defaultSortField, boolean sortAscending, String... unsortedFields) {
        super();

        for (String fieldName : unsortedFields) {
            sortOrders.put(fieldName, SortOrder.UNSORTED);
        }

        if (defaultSortField != null && defaultSortField.length() > 0) {
            sortOrders.put(defaultSortField, sortAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING);
        }
    }

//    @Override
//    public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) {
//
//        final int firstRow = ((SequenceRange) range).getFirstRow();
//        final int numberOfRows = ((SequenceRange) range).getRows();
//
//        if (isDetached && (firstRow == currentFirstRow)) {
//            for (Serializable key : wrappedKeys) {
//                setRowKey(key);
//                visitor.process(context, key, argument);
//            }
//        } else {
//            wrappedKeys.clear();
//            wrappedData.clear();
//
//            final List<T> objects = loadData(new PaginationConfiguration(firstRow, numberOfRows, retrieveFilters(), null,null,null,retrieveSorting()));
//            for (T object : objects) {
//                final Serializable id = getId(object);
//                wrappedKeys.add(id);
//                wrappedData.put(id, object);
//                visitor.process(context, id, argument);
//            }
//            currentFirstRow = firstRow;
//        }
//    }
//
//    private LinkedHashMap<String, String> retrieveSorting() {
//        if (arrangeableState == null) {
//            return null;
//        }
//
//        List<SortField> sortFields = arrangeableState.getSortFields();
//        if (sortFields == null || sortFields.isEmpty()) {
//            return null;
//        }

//        LinkedHashMap<String, String> sortingMap = new LinkedHashMap<String, String>();
//
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//
//        for (SortField sortField : sortFields) {
//
//            String propertyName = (String) sortField.getSortBy().getValue(facesContext.getELContext());
//
//            SortOrder sortOrder = sortField.getSortOrder();
//
//            if (sortOrder == SortOrder.ascending || sortOrder == SortOrder.descending) {
//                sortingMap.put(propertyName, sortOrder.toString());
//            } else {
//                throw new IllegalArgumentException(sortOrder.toString());
//            }
//        }
//
//        return sortingMap;
//    }

//    private LinkedHashMap<String, Object> retrieveFilters() {
//
//        if (arrangeableState == null) {
//            return null;
//        }
//
//        List<FilterField> filterFields = arrangeableState.getFilterFields();
//        if (filterFields == null || filterFields.isEmpty()) {
//            return null;
//        }
//        LinkedHashMap<String, Object> filterMap = new LinkedHashMap<String, Object>();
//
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//
//        for (FilterField filterField : filterFields) {
//            String propertyName = (String) filterField.getFilterExpression().getValue(facesContext.getELContext());
//            Object filterValue = filterField.getFilterValue();
//
//            filterMap.put(propertyName, filterValue);
//        }
//        return filterMap;

//    }

    @Override
    public int getRowCount() {
        if (rowCount == null) {
//            updateRowCount();
        }
        return rowCount;
    }

    @Override
    public T getRowData() {

        if (rowKey == null) {
            return null;
        } else {
            T object = wrappedData.get(rowKey);
            /**
             * if (object == null) { object = getObjectById(rowKey); wrappedData.put(rowKey, object); }
             **/
            return object;
        }
    }

    @Override
    public boolean isRowAvailable() {
        if (rowKey == null) {
            return false;
        }
        if (wrappedKeys.contains(rowKey)) {
            return true;
        }
        if (wrappedData.entrySet().contains(rowKey)) {
            return true;
        }
        /**
         * if (getObjectById(currentPk) != null) { return true; }
         **/
        return false;
    }

    //
    // public void modify(List<FilterField> filterFields, List<SortField2>
    // sortFields) {
    //
    // boolean changed = false;
    //
    // Map<String, String> filterMapNew = new HashMap<String, String>();
    // String sortFieldNameNew = null;
    // Ordering orderingNew = null;
    //
    // // Construct a new filter map
    // if (!filterFields.isEmpty()) {
    // for (FilterField filterField : filterFields) {
    // final ExtendedFilterField extendedFilterField = (ExtendedFilterField)
    // filterField;
    // final String filterValue = extendedFilterField.getFilterValue();
    // if (filterValue != null && filterValue.length() > 0) {
    //
    // final String expressionString =
    // extendedFilterField.getExpression().getExpressionString();
    // final String filterFieldName = getFilterExpressionName(expressionString);
    // filterMapNew.put(filterFieldName, filterValue);
    // }
    // }
    // }
    // // Compare new and old filter maps. If they differ - update with new
    // // values
    // // If one filter map is empty while other is not -
    // if (filterMapNew.isEmpty() != filterMap.isEmpty()) {
    // changed = true;
    //
    // // If both are not empty, compare field by field
    // } else if (!filterMap.isEmpty()) {
    // // Compare keys - Keys dont match
    // if (!(filterMap.keySet().containsAll(filterMapNew.keySet()) &&
    // filterMapNew.keySet().containsAll(filterMap.keySet()))) {
    // changed = true;
    //
    // // Compare values
    // } else {
    // for (String key : filterMap.keySet()) {
    // if (!StringUtils.isValuesEqual(filterMap.get(key),
    // filterMapNew.get(key))) {
    // changed = true;
    // break;
    // }
    // }
    // }
    // }
    //
    // if (!sortFields.isEmpty()) {
    // final SortField2 sortFild = sortFields.get(0);
    // final Expression expression = sortFild.getExpression();
    // final String expressionString = expression.getExpressionString();
    // orderingNew = sortFild.getOrdering();
    // sortFieldNameNew = getExpressionName(expressionString);
    // }
    //
    // // Compare new and old sorting fields
    // // Note: there is no way that user can unset filtering, or set only
    // // field or order, thus ignore if any value is null
    // if (sortFieldNameNew != null && orderingNew != null && (orderingNew !=
    // ordering || !StringUtils.isValuesEqual(sortFieldNameNew, sortFieldName)))
    // {
    // changed = true;
    // }
    //
    // // Mark as changed
    // if (changed) {
    //
    // isDetached = false;
    // filterMap.clear();
    // for (String key : filterMapNew.keySet()) {
    // filterMap.put(key, filterMapNew.get(key));
    // }
    // ordering = orderingNew;
    // sortFieldName = sortFieldNameNew;
    // }
    // }

//    public void arrange(FacesContext context, ArrangeableState state) {
//        arrangeableState = state;
//    }
//
//    protected ArrangeableState getArrangeableState() {
//        return arrangeableState;
//    }
//
//    @Override
//    public void setRowKey(Object key) {
//        rowKey = key;
//    }
//
//    @Override
//    public Object getRowKey() {
//        return rowKey;
//    }

    public void setSortProperty(String sortProperty) {
        this.sortProperty = sortProperty;
    }

    public String getSortProperty() {
        return sortProperty;
    }

    @Override
    public void setRowIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowIndex() {
        return -1;// 0
    }

    /**
     * Force a complete database refresh at the next call
     */
    public void forceRefresh() {
        rowCount = null;
        currentFirstRow = -1;
    }

//    private void updateRowCount() {
//        rowCount = countRecords(new PaginationConfiguration(retrieveFilters()));
//    }

    protected Serializable getId(T object) {
        return ((IEntity) object).getId();
    }
//
//    public void setSortOrders(Map<String, SortOrder> sortOrders) {
//        this.sortOrders = sortOrders;
//    }
//
//    public Map<String, SortOrder> getSortOrders() {
//        return sortOrders;
//    }

    public void setFilterValues(Map<String, String> filterValues) {
        this.filterValues = filterValues;
    }

    public Map<String, String> getFilterValues() {
        return filterValues;
    }

    @Override
    public Object getWrappedData() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWrappedData(Object data) {
        throw new UnsupportedOperationException();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void toggleSort() {

//        for (Entry<String, SortOrder> orderEntry : sortOrders.entrySet()) {
//            SortOrder newOrder;
//
//            if (orderEntry.getKey().equals(sortProperty)) {
//                if (orderEntry.getValue() == SortOrder.ascending) {
//                    newOrder = SortOrder.descending;
//                } else {
//                    newOrder = SortOrder.ascending;
//                }
//            } else {
//                newOrder = SortOrder.unsorted;
//            }
//
//            orderEntry.setValue(newOrder);
//        }

        forceRefresh();
    }

    public void toggleSort(String propertyName) {

//        for (Entry<String, SortOrder> orderEntry : sortOrders.entrySet()) {
//            SortOrder newOrder;
//
//            if (orderEntry.getKey().equals(propertyName)) {
//                if (orderEntry.getValue() == SortOrder.ascending) {
//                    newOrder = SortOrder.descending;
//                } else {
//                    newOrder = SortOrder.ascending;
//                }
//            } else {
//                newOrder = SortOrder.unsorted;
//            }
//
//            orderEntry.setValue(newOrder);
//        }

        forceRefresh();
    }
}
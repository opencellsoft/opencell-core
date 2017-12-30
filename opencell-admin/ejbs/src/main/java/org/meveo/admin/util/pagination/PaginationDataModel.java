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
package org.meveo.admin.util.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

//import org.ajax4jsf.model.DataVisitor;
//import org.ajax4jsf.model.ExtendedDataModel;
//import org.ajax4jsf.model.Range;
//import org.ajax4jsf.model.SequenceRange;
import org.meveo.model.IEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.SortOrder;
//import org.richfaces.model.Arrangeable;
//import org.richfaces.model.ArrangeableState;

/**
 * Pagination data model. This data model is used together with
 * PersistenceService, which is responsible for actual data load from the
 * database using specific filtering and sorting. Concrete implementation of
 * Persistence service is passed through constructor.
 * 
 */
//public class PaginationDataModel<T>extends ExtendedDataModel<T> implements Arrangeable, Serializable  {
public class PaginationDataModel<T>extends DataModel<T> implements Serializable  {

    private static final long serialVersionUID = 1L;

    private Integer rowCount;
    private boolean isDetached = false;
    private Serializable rowKey;
//    private ArrangeableState arrangeableState;
    private int currentFirstRow = -1;

	@SuppressWarnings("rawtypes")
	private IPersistenceService service;

    private final List<Serializable> wrappedKeys = new LinkedList<Serializable>();
    private final Map<Serializable, T> wrappedData = new HashMap<Serializable, T>();
    private Map<String, Object> filters = new HashMap<String, Object>();
    private List<String> fetchFields = new ArrayList<String>();

    public PaginationDataModel(){
        super();
    }
    
    /**
     * Constructor.
     * 
     * @param service
     *            Persistence service for concrete entity implementation.
     */
    @SuppressWarnings("rawtypes")
    public PaginationDataModel(IPersistenceService service) {
        this.service = service;
    }

    /**
     * @return sort field.
     */
    protected String getSortField() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final String sortField = context.getExternalContext().getRequestParameterMap().get("sortField");
        return sortField;
    }

    /**
     * @param filters map of filter.
     */
    public void addFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    /**
     * @param fetchFields fetch fields.
     */
    public void addFetchFields(List<String> fetchFields) {
        this.fetchFields = fetchFields;
    }

    /**
     * @return sort order.
     */
    private SortOrder getSortOrdering() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final String sortField = context.getExternalContext().getRequestParameterMap().get("sortOrder");
        return "desc".equals(sortField) ? SortOrder.DESCENDING
                : SortOrder.ASCENDING;
    }

    /**
     * Load data using pagination configuration information for sorting, paging
     * and filtering.
     * 
     * @param paginatingData
     *            Paginating data.
     * @return list of entity.
     */
    @SuppressWarnings("unchecked")
    protected List<T> loadData(PaginationConfiguration paginatingData) {
        return service.list(paginatingData);
    }

    /**
     * @param paginatingData pagination data.
     * @return number of records.
     */
    protected int countRecords(PaginationConfiguration paginatingData) {
        return (int) service.count(paginatingData);
    }

    /**
     * This is main part of Visitor pattern. Method called by framework many
     * times during request processing.
     */
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
//            final List<T> objects = loadData(new PaginationConfiguration(firstRow, numberOfRows, filters, fetchFields,
//                    getSortField(), getSortOrdering()));
//            for (T object : objects) {
//                final Serializable id = getId(object);
//                wrappedKeys.add(id);
//                wrappedData.put(id, object);
//                visitor.process(context, id, argument);
//            }
//
//            currentFirstRow = firstRow;
//        }
//    }

    /**
     * This method return list of filtered entities.
     * @return list of entity.
     */
    public List<T> list() {
        final int numberOfRows = this.getRowCount();
        final List<T> objects = loadData(new PaginationConfiguration(0, numberOfRows, filters, null, fetchFields,
                getSortField(), getSortOrdering()));

        return objects;
    }

//    /**
//     * This method suppose to produce SerializableDataModel that will be
//     * serialized into View State and used on a post-back.
//     */
//    @Override
//    public SerializableDataModel getSerializableModel(Range range) {
//        if (wrappedKeys != null) {
//            isDetached = true;
//            return this;
//        }
//
//        return null;
//    }
//
//    /**
//     * This is helper method that is called by framework after model update.
//     */
//    @Override
//    public void update() {
//    }

    /**
     * This method must return actual data rows count from the Data Provider. It
     * is used by pagination control to determine total number of data items.
     */
    @Override
    public int getRowCount() {
        if (rowCount == null) {
            rowCount = countRecords(new PaginationConfiguration(filters));
        }
        return rowCount;
    }

    /**
     * This is main way to obtain data row. It is intensively used by framework.
     * (Recommend use of local cache in that method).
     */
    @Override
    public T getRowData() {
        return getRowDataT();
    }

    /**
     * Used by getRowData() method, also if needed to get row data of T type
     * (not as object).
     * @return entity.
     */
    public T getRowDataT() {
        if (rowKey == null) {
            return null;
        } else {
            T object = wrappedData.get(rowKey);
            /**
             * if (object == null) { object = getObjectById(currentPk);
             * wrappedData.put(currentPk, object); }
             **/
            return object;
        }
    }

    /**
     * @param currentRow current row.
     * @return row data.
     */
    public Object getRowData(Serializable currentRow) {
        if (currentRow == null) {
            return null;
        } else {
            T object = wrappedData.get(currentRow);
            /**
             * if (object == null) { object = getObjectById(currentPk);
             * wrappedData.put(currentPk, object); }
             **/
            return object;
        }
    }

    /**
     * Never called by framework.
     */
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

//    public void arrange(FacesContext context, ArrangeableState state) {
//        arrangeableState = state;
//    }
//
//    protected ArrangeableState getArrangeableState() {
//        return arrangeableState;
//    }
    
    /**
     * This method never called from framework.
     * 
     * @see org.ajax4jsf.model.ExtendedDataModel#getRowKey()
     */
//    @Override
//    public Object getRowKey() {
//        return rowKey;
//    }

    /**
     * This method normally called by Visitor before request Data Row.
     */
//    @Override
//    public void setRowKey(final Object key) {
//        this.rowKey = (Serializable) key;
//    }

    /**
     * Force a complete database refresh at the next call.
     */
    public void forceRefresh() {
        rowCount = null;
        currentFirstRow = -1;
    }

    protected Serializable getId(T object) {
        return ((IEntity) object).getId();
    }

    /**
     * Unused rudiment from old JSF staff.
     */
    @Override
    public Object getWrappedData() {
        return wrappedData.values();
    }

    public Set<Serializable> getKeySet() {
        return wrappedData.keySet();
    }

    /**
     * Unused rudiment from old JSF staff.
     */
    @Override
    public void setWrappedData(Object data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRowIndex(int index) {
    }

    @Override
    public int getRowIndex() {
        return 0;
    }

//    public void modify(List<FilterField> filterFields, List<SortField2> sortFields) {
//        isDetached = false;
//    }
}
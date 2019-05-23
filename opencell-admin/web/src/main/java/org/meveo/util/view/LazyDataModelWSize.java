package org.meveo.util.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyDataModelWSize<T> extends LazyDataModel<T> {

    private static final long serialVersionUID = -20655217804181429L;

    public Integer size() {
        return getRowCount();
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        return new ArrayList<>();
    }
}
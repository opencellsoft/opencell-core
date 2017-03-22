package org.meveo.util.view;

import org.primefaces.model.LazyDataModel;

public class LazyDataModelWSize<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = -20655217804181429L;

	public Integer size() {
		return getRowCount();
	}
}
/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.util.pagination;

import java.util.Arrays;
import java.util.List;

import javax.faces.model.ListDataModel;
import javax.inject.Inject;

import org.meveo.model.IEntity;
import org.primefaces.model.SelectableDataModel;

public class EntityListDataModelPF<T extends IEntity> extends ListDataModel<T> implements
		SelectableDataModel<T> {

	@Inject
	protected org.slf4j.Logger log;

	private T[] selectedItems;

	public EntityListDataModelPF() {
	}

	public EntityListDataModelPF(List<T> data) {
		super(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getRowData(String rowKey) {
		// In a real app, a more efficient way like a query by rowKey should be
		// implemented to deal with huge data

		List<T> entities = (List<T>) getWrappedData();

		for (T entity : entities) {
			if (((Long) entity.getId()).equals(Long.parseLong(rowKey))) {
				return entity;
			}
		}

		log.error("No matching record found with rowKey " + rowKey);
		return null;
	}

	@Override
	public Object getRowKey(T entity) {
		return entity.getId();
	}

	@SuppressWarnings("unchecked")
	public void add(T entity) {
		((List<T>) getWrappedData()).add(entity);
	}

	@SuppressWarnings("unchecked")
	public void addAll(List<T> entities) {
		((List<T>) getWrappedData()).addAll(entities);
	}

	@SuppressWarnings("unchecked")
	public void remove(T entity) {
		((List<T>) getWrappedData()).remove(entity);
	}

	@SuppressWarnings("unchecked")
	public int getSize() {
		return ((List<T>) getWrappedData()).size();
	}

	public T[] getSelectedItems() {
		return selectedItems;
	}

	public List<T> getSelectedItemsAsList() {
		return Arrays.asList(selectedItems);
	}

	public void setSelectedItems(T[] selectedItems) {
		this.selectedItems = selectedItems;
	}
}
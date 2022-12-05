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
package org.meveo.admin.action.catalog;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

/**
 * Standard backing bean use to keep price plan 
 * columns selected to be viewed 
 */
@Named
@SessionScoped
public class PricePlanMatrixColumnSelectorBean implements Serializable {

	private static final long serialVersionUID = 6332101600511550577L;

	private List<Boolean> columnVisibilitylist;

	public PricePlanMatrixColumnSelectorBean() {}

	/**
	 * initialize the list of table columns to be visible
	 */
	@PostConstruct
	 public void init() {
		columnVisibilitylist = Arrays.asList(true, true, true, true, true, true, true, true, 
	     		false, false, false, false, false, false, false, 
	     		false, false, false, false, false, false);
	 }
	 public List<Boolean> getColumnVisibilitylist() {
	     return columnVisibilitylist;
	 }

	public String getColumnWidth(int index) {
		return columnVisibilitylist.get(index) ? "15%" : "0";
	}
	 
	 public void onToggle(ToggleEvent e) {
	 	columnVisibilitylist.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
	 }
	
}


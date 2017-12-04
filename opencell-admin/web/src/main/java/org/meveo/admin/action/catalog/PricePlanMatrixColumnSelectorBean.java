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
package org.meveo.admin.action.catalog;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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


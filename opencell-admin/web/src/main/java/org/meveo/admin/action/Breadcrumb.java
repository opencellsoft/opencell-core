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
package org.meveo.admin.action;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named
@SessionScoped
public class Breadcrumb implements Serializable {

    private static final long serialVersionUID = -6053968861740813024L;

    // private List<Page> sessionCrumb;

    public void log() {
		// try {
		// if (sessionCrumb == null) {
		// sessionCrumb = new LinkedList<Page>();
		// }
		//
		// if (Pageflow.instance() != null) {
		// if (Pageflow.instance().getPage() != null) {
		// Page thePage = Pageflow.instance().getPage();
		// if (sessionCrumb.contains(thePage)) {
		// // rewind the conversation crumb to the page
		// sessionCrumb = sessionCrumb.subList(0, sessionCrumb.indexOf(thePage)
		// + 1);
		// } else {
		// sessionCrumb.add(thePage);
		// }
		// }
		// }
		// } catch (Throwable t) {
		// // Do nothing as this is just a "listener" for breadcrumbs
		// t.printStackTrace();
		// }
	}

	public void navigate() {
		// FacesContext context = FacesContext.getCurrentInstance();
		// Map map = context.getExternalContext().getRequestParameterMap();
		// String viewId = (String) map.get("viewId");
		// String pageName = (String) map.get("name");
		//
		// Pageflow.instance().reposition(pageName);
		//
		// Redirect redirect = Redirect.instance();
		// redirect.setViewId(viewId);
		// redirect.execute();
	}
}
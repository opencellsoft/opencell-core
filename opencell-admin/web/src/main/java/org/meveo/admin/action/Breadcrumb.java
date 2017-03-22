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
package org.meveo.admin.action;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

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
/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("breadcrumb")
@Scope(ScopeType.SESSION)
public class Breadcrumb {

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
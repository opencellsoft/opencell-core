/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.admin.filter;

import java.io.IOException;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.meveo.model.admin.User;

@Startup
@ApplicationScoped
/*TODO: javaee6 @Name("org.meveo.admin.filter.UserActionsLoggingFilter")
@BypassInterceptors
@Filter(around = "org.jboss.seam.web.ajax4jsfFilter")*/
public class UserActionsLoggingFilter /*extends AbstractFilter*/ {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        
        // TODO
//        HttpServletRequest httpReq = (HttpServletRequest) request;
//        User user = null;
//        if (httpReq.getSession().getAttribute("currentUser") != null) {
//            user = (User) httpReq.getSession().getAttribute("currentUser");
//        }
//        String objectId = "";
//        String edit = "";
//
//        Map<?, ?> parametersMap = httpReq.getParameterMap();
//        String uri = httpReq.getRequestURI();
//        if (parametersMap.containsKey("objectId")) {
//            objectId = ((String[]) parametersMap.get("objectId"))[0];
//        }
//        if (parametersMap.containsKey("edit")) {
//            edit = ((String[]) parametersMap.get("edit"))[0];
//        }
//        output(user, objectId, edit, uri);
        chain.doFilter(request, response);

    }

    public void output(User user, String objectId, String edit, String uri) {
        if (uri.endsWith("jsf") && (user != null)) {
            
            // TODO
        	/*Lifecycle.beginCall();
            UserServiceLocal userService = (UserServiceLocal) Component.getInstance("userService", true);

            if ((!objectId.equals("")) && (!edit.equals(""))) {
                if (edit.equals("true")) {
                    action = "edit";
                } else
                    action = "View object";
            } else if (user != null) {
                action = "View all list";
            }
            userService.saveActivity(user, objectId, action, uri);
            Lifecycle.endCall();*/
        }

    }

}

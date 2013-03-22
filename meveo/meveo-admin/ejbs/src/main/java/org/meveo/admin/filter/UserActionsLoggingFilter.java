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
package org.meveo.admin.filter;

import java.io.IOException;
import java.util.Map;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.lifecycle.Lifecycle;
import javax.inject.Scope;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
        String action = "";
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

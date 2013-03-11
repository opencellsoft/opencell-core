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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.web.AbstractFilter;

/**
 * @author Tyshan(tyshanchn@manaty.net)
 * @created 2011-1-6
 */

@Startup
@Scope(ScopeType.APPLICATION)
@Name("org.meveo.admin.filter.UTF8CharacterEncodingFilter")
@BypassInterceptors
@org.jboss.seam.annotations.web.Filter(around = "org.jboss.seam.web.ajax4jsfFilter")
public class UTF8CharacterEncodingFilter extends AbstractFilter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        request.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

}

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
package org.meveo.admin.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles cases when user refresh a stale page, bound to a session, that is when url contains CID parameter
 * 
 * @author Andrius Karpavicius
 *
 */
@WebFilter(filterName = "redirectToStaleSessionFilter", urlPatterns = { "/pages/*" })
public class RedirectToStaleSessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest reqHttp = (HttpServletRequest) req;

        String referer = reqHttp.getHeader("Referer");

        if ("GET".equals(reqHttp.getMethod()) && referer != null && referer.contains("/auth") && reqHttp.getParameter("cid") != null) {
            String url = reqHttp.getRequestURL().toString();
            String indexUrl = url.substring(0, url.indexOf(reqHttp.getContextPath())) + reqHttp.getContextPath() + "/index.html";

            ((HttpServletResponse) resp).sendRedirect(indexUrl);
            return;
        }

        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
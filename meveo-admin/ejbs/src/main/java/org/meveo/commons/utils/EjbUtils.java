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
package org.meveo.commons.utils;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class for remote ejb lookups.
 * 
 * @author Ignas Lelys
 * @created Jan 19, 2011
 *
 */
public class EjbUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(EjbUtils.class);

    private static final String LOCALHOST = "127.0.0.1";
    
    /**
     * Non instantiable class.
     */
    private EjbUtils() {
    }

    /**
     * Obtain interface of an object in JNDI
     * 
     * @param nameEJB
     *            Full JNDI path to an object
     * @param serverName
     *            Server address where to look for an object
     * @return Object instance
     * 
     */
    public static Object getInterface(String nameEJB) throws NamingException {
        InitialContext ctx = null;
        if (System.getenv("JBOSS_HOST") != null) {
            logger.info(String.format("JBOSS_HOST=", System.getenv("JBOSS_HOST")));
            ctx = getInitialContext(System.getenv("JBOSS_HOST"));
        } else {
            ctx = getInitialContext(LOCALHOST);
        }
        return ctx.lookup(nameEJB);
    }

    /**
     * Obtain remote interface of an object in JNDI
     * 
     * @param nameEJB
     *            Full JNDI path to an object
     * @param serverName
     *            Server address where to look for an object
     * @return Object instance
     * 
     */
    public static Object getRemoteInterface(String nameEJB, String serverName) throws NamingException {
        InitialContext ctx = (InitialContext) getInitialContext(serverName);
        return ctx.lookup(nameEJB);
    }

    private static InitialContext getInitialContext(String serverName) throws NamingException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        properties.put(Context.PROVIDER_URL, serverName);
        return new InitialContext(properties);
    }
    
    /**
     * Return a service
     * 
     * @param serviceInterfaceName A simple name of a service (NOT a full classname). E.g. WorkflowService
     * @return Service instance
     */
    public static Object getServiceInterface(String serviceInterfaceName) {
        try {
            InitialContext ic = new InitialContext();
            return ic.lookup("java:global/" + ParamBean.getInstance().getProperty("meveo.moduleName", "meveo") + "/" + serviceInterfaceName);
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(EjbUtils.class);
            log.error("Failed to obtain service interface for {} {}", serviceInterfaceName, e.getMessage());
        }
        return null;
    }
}
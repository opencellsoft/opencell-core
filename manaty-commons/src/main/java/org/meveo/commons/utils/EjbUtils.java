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

}
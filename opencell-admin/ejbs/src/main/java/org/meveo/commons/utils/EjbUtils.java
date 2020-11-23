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
package org.meveo.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Util class for remote ejb lookups.
 * 
 * @author Ignas Lelys
 *
 */
public class EjbUtils {

    private static final Logger logger = LoggerFactory.getLogger(EjbUtils.class);

    private static final String LOCALHOST = "127.0.0.1";

    private static final String JBOSSHOST = "JBOSS_HOST";

    /**
     * Non instantiable class.
     */
    private EjbUtils() {
    }

    /**
     * Obtain interface of an object in JNDI.
     * 
     * @param nameEJB Full JNDI path to an object
     * @return Object instance
     * @throws NamingException naming exception.
     * 
     */
    public static Object getInterface(String nameEJB) throws NamingException {
        InitialContext ctx = null;
        if (System.getProperty(JBOSSHOST) != null) {
            logger.info(String.format("JBOSS_HOST=", System.getProperty(JBOSSHOST)));
            ctx = getInitialContext(System.getProperty(JBOSSHOST));
        } else {
            ctx = getInitialContext(LOCALHOST);
        }
        return ctx.lookup(nameEJB);
    }

    /**
     * Obtain remote interface of an object in JNDI.
     * 
     * @param nameEJB Full JNDI path to an object
     * @param serverName Server address where to look for an object
     * @return Object instance
     * @throws NamingException naming exception.
     * 
     */
    public static Object getRemoteInterface(String nameEJB, String serverName) throws NamingException {
        InitialContext ctx = (InitialContext) getInitialContext(serverName);
        return ctx.lookup(nameEJB);
    }

    /**
     * @param serverName server name
     * @return initial context
     * @throws NamingException naming exception.
     */
    private static InitialContext getInitialContext(String serverName) throws NamingException {
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        properties.put(Context.PROVIDER_URL, serverName);
        return new InitialContext(properties);
    }

    /**
     * Return a service by a service interface name.
     * 
     * @param serviceInterfaceName A simple name of a service class (NOT a full classname). E.g. WorkflowService
     * @return Service instance
     */
    public static Object getServiceInterface(String serviceInterfaceName) {
        try {
            InitialContext ic = new InitialContext();
            return ic.lookup("java:global/" + ParamBean.getInstance().getProperty("opencell.moduleName", "opencell") + "/" + serviceInterfaceName);
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(EjbUtils.class);
            log.debug("Failed to obtain service interface for {} {}", serviceInterfaceName, e.getMessage());
        }
        return null;
    }

    /**
     * Return a persistence service for a given entity class.
     * 
     * @param entityClass Entity class
     * @return Persistence service
     */
    @SuppressWarnings("rawtypes")
    public static Object getServiceInterface(Class entityClass) {
        return getServiceInterface(entityClass.getSimpleName() + "Service");
    }

    public static String getCurrentClusterNode() {
        return System.getProperty("jboss.node.name");
    }

    public static boolean isRunningInClusterMode() {
        String nodeName = System.getProperty("jboss.node.name");
        return  nodeName!= null && nodeName.startsWith("opencell");
    }
}
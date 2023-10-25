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

package org.meveo.api.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.meveo.api.rest.filter.RESTCorsRequestFilter;
import org.meveo.api.rest.impl.BaseRs;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application config for public api rest paths, in order to get access to not protected resources. Required for some use cases needs , like Yousign webhook calbacks :
 * https://help.yousign.com/hc/fr/articles/360000856312-Notifier-mon-application-avec-les-webhooks.
 * 
 * @author Said Ramli
 **/
@ApplicationPath("/api/pub")
public class JaxRsPubActivator extends Application {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();

        Reflections reflections = new Reflections("org.meveo.api.pub.rest");
        Set<Class<? extends BaseRs>> allClasses = reflections.getSubTypesOf(BaseRs.class);

        Logger log = LoggerFactory.getLogger(getClass());
        log.debug("Documenting {} rest services for path /api/pub/", allClasses.size());

        resources.addAll(allClasses);

        resources.add(RESTCorsRequestFilter.class);
        // resources.add(RESTCorsResponseFilter.class);
        resources.add(JaxRsExceptionMapper.class);

        return resources;
    }

}

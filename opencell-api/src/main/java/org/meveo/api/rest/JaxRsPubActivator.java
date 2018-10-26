package org.meveo.api.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.meveo.api.rest.filter.RESTCorsRequestFilter;
import org.meveo.api.rest.filter.RESTCorsResponseFilter;
import org.meveo.api.rest.impl.BaseRs;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application config for public api rest paths, in order to get access to not protected resources.
 *  Required for some use cases needs , like Yousign webhook calbacks : https://help.yousign.com/hc/fr/articles/360000856312-Notifier-mon-application-avec-les-webhooks.
 * 
 * @author Said Ramli
 **/
@ApplicationPath("/api/pub")
public class JaxRsPubActivator extends Application {

    private Logger log = LoggerFactory.getLogger(JaxRsPubActivator.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();

        Reflections reflections = new Reflections("org.meveo.api.pub.rest");
        Set<Class<? extends BaseRs>> allClasses = reflections.getSubTypesOf(BaseRs.class);

        log.debug("Documenting {} rest services...", allClasses.size());

        resources.addAll(allClasses);

        resources.add(RESTCorsRequestFilter.class);
        resources.add(RESTCorsResponseFilter.class);
        resources.add(JaxRsExceptionMapper.class);

        return resources;
    }

}

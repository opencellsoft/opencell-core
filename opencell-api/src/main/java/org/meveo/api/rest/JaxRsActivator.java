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
 * @author Edward P. Legaspi
 **/
@ApplicationPath("/api/rest")
public class JaxRsActivator extends Application {

    private Logger log = LoggerFactory.getLogger(JaxRsActivator.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();

        Reflections reflections = new Reflections("org.meveo.api.rest");
        Set<Class<? extends BaseRs>> allClasses = reflections.getSubTypesOf(BaseRs.class);

        log.debug("Documenting {} rest services...", allClasses.size());

        resources.addAll(allClasses);

        resources.add(RESTCorsRequestFilter.class);
        resources.add(RESTCorsResponseFilter.class);
        resources.add(JaxRsExceptionMapper.class);
        resources.add(JacksonProvider.class);
        
        return resources;
    }

}

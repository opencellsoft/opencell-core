package org.meveo.apiv2;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.meveo.apiv2.exception.BadRequestExceptionMapper;
import org.meveo.apiv2.exception.NotFoundExceptionMapper;
import org.meveo.apiv2.exception.UnhandledExceptionMapper;
import org.meveo.apiv2.ordering.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.product.ProductResourceImpl;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/rest/v2")
public class OpencellRestful extends Application {
    private static final String API_LIST_DEFAULT_LIMIT_KEY = "api.list.defaultLimit";
    public static long API_LIST_DEFAULT_LIMIT;
    @Inject
    protected Logger log;
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @PostConstruct
    public void init() {
        API_LIST_DEFAULT_LIMIT = paramBeanFactory.getInstance().getPropertyAsInteger(API_LIST_DEFAULT_LIMIT_KEY, 100);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();
        resources.add(ProductResourceImpl.class);
        resources.add(OrderItemResourceImpl.class);
        resources.add(OrderResourceImpl.class);
        resources.add(GenericResourceImpl.class);
        resources.add(NotYetImplementedResource.class);


        resources.add(NotFoundExceptionMapper.class);
        resources.add(BadRequestExceptionMapper.class);
        resources.add(UnhandledExceptionMapper.class);
        log.debug("Documenting {} rest services...", resources.size());
        resources.add(OpenApiResource.class);
        log.debug("Opencell OpenAPI definition is accessible in /api/rest/v2/openapi.{type:json|yaml}");
        return resources;
    }

}
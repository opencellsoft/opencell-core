package org.meveo.apiv2.generic.security.interceptor;

import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.SecuredBusinessEntityConfig;
import org.meveo.api.security.config.SecuredBusinessEntityConfigFactory;
import org.meveo.apiv2.generic.security.config.JsonConfigFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * This is the secured business entities interceptor which extends {@link SecuredBusinessEntityMethodInterceptor},
 * and uses {@link SecuredBusinessEntityJsonConfigFactory} to use the Json based config
 * 
 * @author mounir Boukayoua
 * @since 10.X
 */
public class SecuredBusinessEntityCheckInterceptor extends SecuredBusinessEntityMethodInterceptor {

    /**
     * Inject an instance of {@link SecuredBusinessEntityJsonConfigFactory}
     */
    @Inject
    @JsonConfigFactory
    protected SecuredBusinessEntityConfigFactory securedBusinessEntityConfigFactory;

    /**
     * Override aroundInvoke() method to get a json based config represented by an instance
     * of {@link SecuredBusinessEntityJsonConfig} and then passe it to method checkForSecuredEntities()
     * @param context API method invocation context
     * @return API method result if the check is OK
     * @throws Exception an exception if check is KO
     */
    @AroundInvoke
    @Override
    public Object aroundInvoke(InvocationContext context) throws Exception {
        SecuredBusinessEntityConfig sbeConfig = this.securedBusinessEntityConfigFactory.get(context);
        return super.checkForSecuredEntities(context, sbeConfig);
    }
}

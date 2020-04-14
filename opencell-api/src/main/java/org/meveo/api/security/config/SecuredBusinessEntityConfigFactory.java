package org.meveo.api.security.config;

import javax.interceptor.InvocationContext;

public interface SecuredBusinessEntityConfigFactory {

    SecuredBusinessEntityConfig get(InvocationContext context);
}

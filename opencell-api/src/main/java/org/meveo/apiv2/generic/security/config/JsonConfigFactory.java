package org.meveo.apiv2.generic.security.config;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier to mark {@link SecuredBusinessEntityJsonConfigFactory}
 *
 * @author Mounir Boukayoua
 * @since 10.X
 */
@Qualifier
@Target({PARAMETER, FIELD, METHOD, TYPE})
@Retention(RUNTIME)
public @interface JsonConfigFactory {
}

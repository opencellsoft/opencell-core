package org.meveo.api.security.Interceptor;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.meveo.api.exception.AccessDeniedException;
import org.meveo.api.security.filter.SecureMethodResultFilter;
import org.meveo.api.security.filter.SecureMethodResultFilterFactory;
import org.meveo.api.security.parameter.SecureMethodParameter;
import org.meveo.api.security.parameter.SecureMethodParameterHandler;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This will handle the processing of {@link SecuredBusinessEntityMethod}
 * annotated methods.
 * 
 * @author Tony Alejandro
 *
 */
public class SecuredBusinessEntityMethodInterceptor implements Serializable {

	private static final long serialVersionUID = 4656634337151866255L;

	private static final Logger log = LoggerFactory.getLogger(SecuredBusinessEntityMethodInterceptor.class);

	@Inject
	private SecuredBusinessEntityService securedBusinessEntityService;

	@Inject
	private SecureMethodResultFilterFactory filterFactory;

	@Inject
	private SecureMethodParameterHandler parameterHandler;

	private ParamBean paramBean = ParamBean.getInstance();

	/**
	 * This is called before a method that makes use of the
	 * {@link SecuredBusinessEntityMethodInterceptor} is called. It contains
	 * logic on retrieving the attributes of the
	 * {@link SecuredBusinessEntityMethod} annotation placed on the method and
	 * then validate the parameters described in the
	 * {@link SecureMethodParameter} validation attributes and then filters the
	 * result using the {@link SecureMethodResultFilter} filter attribute.
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@AroundInvoke
	public Object checkForSecuredEntities(InvocationContext context) throws Exception {

		// check if secured entities should be saved.
		String secureSetting = paramBean.getProperty("secured.entities.enabled", "true");
		boolean secureEntitesEnabled = Boolean.parseBoolean(secureSetting);

		// if not, immediately return.
		if (!secureEntitesEnabled) {
			return context.proceed();
		}

		Class<?> objectClass = context.getMethod().getDeclaringClass();
		String objectName = objectClass.getSimpleName();
		String methodName = context.getMethod().getName();

		SecuredBusinessEntityMethod annotation = context.getMethod().getAnnotation(SecuredBusinessEntityMethod.class);
		if (annotation == null) {
			log.debug("Method {}.{} is not annotated with @SecuredBusinessEntityProperty.  No need to check for authorization.", objectName, methodName);
			return context.proceed();
		}

		SecureMethodParameter userParameter = annotation.user();
		Object[] values = context.getParameters();
		User user = parameterHandler.getParameterValue(userParameter, values, User.class);

		boolean hasRestrictions = user != null && user.getSecuredEntities() != null && !user.getSecuredEntities().isEmpty();

		if (!hasRestrictions) {
			log.debug("User does not have any restrictions.");
			return context.proceed();
		}

		log.debug("Checking method {}.{} for secured BusinessEntities", objectName, methodName);
		SecureMethodParameter[] parametersForValidation = annotation.validate();
		for (SecureMethodParameter parameter : parametersForValidation) {
			BusinessEntity entity = parameterHandler.getParameterValue(parameter, values, BusinessEntity.class);
			if (!securedBusinessEntityService.isEntityAllowed(entity, user, false)) {
				throw new AccessDeniedException("Access to entity details is not allowed.");
			}
		}

		log.debug("Allowing method {}.{} to be invoked.", objectName, methodName);
		Object result = context.proceed();

		SecureMethodResultFilter filter = filterFactory.getFilter(annotation.resultFilter());
		log.debug("Results will be filtered using {} filter.", filter);
		result = filter.filterResult(result);
		return result;

	}
}

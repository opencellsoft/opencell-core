package org.meveo.audit.logging.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.annotations.IgnoreAudit;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.audit.logging.dto.AnnotationAuditEvent;
import org.meveo.audit.logging.dto.AuditEvent;
import org.meveo.audit.logging.dto.MethodParameter;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountActionsEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditManagerService {

	@Inject
	private MetadataHandler metadataHandler;

	@Inject
	private AuditEventProcessor auditEventProcessor;

	private final static String ACTION = "action";

	private Class getEntityClass(Class clazz) {
		while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			clazz = clazz.getSuperclass();
		}
		Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

		if (o instanceof TypeVariable) {
			return (Class) ((TypeVariable) o).getBounds()[0];
		} else {
			return (Class) o;
		}
	}

	public void audit(Class<? extends Object> clazz, String method, Object[] paramValues) throws BusinessException {
		AuditEvent event = new AuditEvent();
		event.setEntity(clazz.getName());
		event.setAction(method);
		event = metadataHandler.addSignature(event);
		auditEventProcessor.process(event);
	}

	public void audit(Class<? extends Object> clazz, Method method, Object[] paramValues) throws BusinessException {
		audit(new AnnotationAuditEvent(clazz, method, paramValues));
		auditPaymentMethod(clazz, method, paramValues);
	}

	/**
	 * Add the payment method to the audit
	 * @param clazz a Class
	 * @param method a method where the action on payment method is executed.
	 * @param paramValues An array of payment methods
	 * @throws BusinessException
	 */
	private void auditPaymentMethod(Class<?> clazz, Method method, Object[] paramValues) throws BusinessException {
		String CustomerAccountServiceClassName = ReflectionUtils.getCleanClassName(CustomerAccountService.class.getSimpleName());
		if (CustomerAccountServiceClassName.equals(clazz.getSimpleName()) && (CustomerAccountActionsEnum.update.name().equals(method.getName()) || CustomerAccountActionsEnum.create.name().equals(method.getName())) && paramValues != null
				&& paramValues.length > 0) {
			Map<String, List<PaymentMethod>> auditedPaymentMethods = ((CustomerAccount) paramValues[0]).getAuditedMethodPayments();
			if (auditedPaymentMethods == null || auditedPaymentMethods.isEmpty()) {
				return;
			}
			for (String action : auditedPaymentMethods.keySet()) {
				AuditEvent event = new AuditEvent();
				event.setEntity(PaymentMethod.class.getName());
				event.setAction(action);
				event.addField(PaymentMethod.class.getName(), auditedPaymentMethods.get(action));
				event = metadataHandler.addSignature(event);
				auditEventProcessor.process(event);
			}

		}
	}

	public void audit(AnnotationAuditEvent event) throws BusinessException {
		AuditEvent auditEvent = transformToEvent(event);
		auditEvent = metadataHandler.addSignature(auditEvent);
		auditEventProcessor.process(auditEvent);
	}

	public AuditEvent transformToEvent(AnnotationAuditEvent annotationEvent) {
		AuditEvent event = new AuditEvent();
		event.setEntity(getEntityClass(annotationEvent.getClazz()).getName());
		event.setAction(annotationEvent.getMethod().getName());
		event.setFields(getParameterLines(annotationEvent.getMethod(), annotationEvent.getParamValues()));
		return event;
	}

	public AuditEvent transformToEventExplicitAnnotated(AnnotationAuditEvent annotationEvent) {
		AuditEvent event = new AuditEvent();
		event.setEntity(getEntityClass(annotationEvent.getClazz()).getName());

		if (annotationEvent.getClazz().isAnnotationPresent(MeveoAudit.class)
				&& !annotationEvent.getMethod().isAnnotationPresent(IgnoreAudit.class)) {
			MeveoAudit audit = annotationEvent.getClazz().getAnnotation(MeveoAudit.class);
			event.setFields(getParameterLines(annotationEvent.getMethod(), annotationEvent.getParamValues()));

			String annotationAction = audit.action();
			if (ACTION.equals(annotationAction)) {
				event.setAction(annotationEvent.getMethod().getName());

			} else {
				event.setAction(annotationAction);
			}

		} else if (!annotationEvent.getClazz().isAnnotationPresent(MeveoAudit.class)
				&& annotationEvent.getMethod().isAnnotationPresent(MeveoAudit.class)) {
			MeveoAudit audit = annotationEvent.getMethod().getAnnotation(MeveoAudit.class);
			event.setFields(getParameterLines(annotationEvent.getMethod(), annotationEvent.getParamValues()));

			String annotationAction = audit.action();
			if (ACTION.equals(annotationAction)) {
				event.setAction(annotationEvent.getMethod().getName());

			} else {
				event.setAction(annotationAction);
			}
		}

		return event;
	}

	public static List<MethodParameter> getParameterLines(Method method, Object[] objects) {
		Parameter[] parameters = method.getParameters();
		List<MethodParameter> methodParameters = new ArrayList<>();

		int i = 0;
		for (Parameter parameter : parameters) {
			// if (!parameter.isNamePresent()) {
			// // throw new IllegalArgumentException("Parameter names are not
			// // present!");
			// continue;
			// }
			final Object obj = objects[i++];

			MethodParameter mp = new MethodParameter(parameter.getName(), obj, parameter.getType().getName());
			methodParameters.add(mp);
		}

		return methodParameters;
	}

}

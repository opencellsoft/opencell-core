package org.meveo.audit.logging.extension;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.audit.logging.core.AuditContext;

/**
 * @author Edward P. Legaspi
 * 
 *         https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html
 **/
public class AuditSpiExtension implements Extension {

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();

		// check if the class is to be audited
		if (AuditContext.getInstance().getAuditConfiguration()
				.findByClassName(annotatedType.getJavaClass().getName()) != null) {

			Annotation auditAnnotation = new Annotation() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return MeveoAudit.class;
				}
			};

			AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(annotatedType,
					annotatedType.getAnnotations());
			wrapper.addAnnotation(auditAnnotation);

			processAnnotatedType.setAnnotatedType(wrapper);

		}

	}

}

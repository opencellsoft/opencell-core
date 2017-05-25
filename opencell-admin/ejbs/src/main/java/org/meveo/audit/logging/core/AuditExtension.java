package org.meveo.audit.logging.core;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Edward P. Legaspi
 **/
public class AuditExtension implements Extension {

	public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();

		if (annotatedType.getJavaClass().equals(OfferTemplateService.class)) {

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

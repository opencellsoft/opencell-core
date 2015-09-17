package org.meveo.model.crm;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PostPersist;

import org.meveo.model.event.RegisterCFEndPeriodEvent;

/**
 * @author Edward P. Legaspi
 **/
public class CustomFieldPeriodListener {

	@PostPersist
	public void postPersist(CustomFieldPeriod entity) {
		// fire event, and in that event register a timer
		if (entity.getCustomFieldInstance().getCalendar() != null) {
			RegisterCFEndPeriodEvent event = new RegisterCFEndPeriodEvent();
			event.setCustomFieldPeriod(entity);

			getBeanManager().fireEvent(event);
		}
	}

	public static BeanManager getBeanManager() {
		try {
			InitialContext initialContext = new InitialContext();
			return (BeanManager) initialContext.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			throw new IllegalStateException(e);
		}
	}
	
}

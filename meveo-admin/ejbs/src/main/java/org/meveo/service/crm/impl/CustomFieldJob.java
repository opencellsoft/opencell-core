package org.meveo.service.crm.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.event.CFEndPeriodEvent;
import org.meveo.model.crm.CustomFieldInstance;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Startup
@Singleton
public class CustomFieldJob {

	@Inject
	private Logger log;

	@Resource
	private TimerService timerService;

	@Inject
	private Event<CFEndPeriodEvent> cFEndPeriodEvent;

	@Timeout
	private void triggerEndPeriodEventExpired(Timer timer) {
		Object[] objs = (Object[]) timer.getInfo();
		try {
			CustomFieldInstance customFieldInstance = (CustomFieldInstance) objs[0];
			CFEndPeriodEvent event = new CFEndPeriodEvent();
			event.setCustomFieldInstance(customFieldInstance);

			cFEndPeriodEvent.fire(event);
		} catch (Exception e) {
			log.error("Failed executing end period event timer", e);
		}
	}

	public void triggerEndPeriodEvent(CustomFieldInstance cfi, Date expiration) {
		TimerConfig timerConfig = new TimerConfig();
		Object[] objs = { cfi };
		timerConfig.setInfo(objs);

		// used for testing
		// expiration = new Date();
		// expiration = DateUtils.addMinutes(expiration, 1);

		log.debug("creating timer for triggerEndPeriodEvent with expiration={}", expiration);

		timerService.createSingleActionTimer(expiration, timerConfig);
	}

}

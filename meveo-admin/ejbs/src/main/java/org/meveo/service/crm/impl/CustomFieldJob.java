package org.meveo.service.crm.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.meveo.event.CFEndPeriodEvent;
import org.meveo.model.event.RegisterCFEndPeriodEvent;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
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
		log.debug("triggerEndPeriodEventExpired={}", timer);
		Object[] objs = (Object[]) timer.getInfo();
		try {
			RegisterCFEndPeriodEvent registerCFEndPeriodEvent = (RegisterCFEndPeriodEvent) objs[0];
			CFEndPeriodEvent event = new CFEndPeriodEvent();
			event.setCustomFieldPeriod(registerCFEndPeriodEvent.getCustomFieldPeriod());

			cFEndPeriodEvent.fire(event);
		} catch (Exception e) {
			log.error("Failed executing end period event timer", e);
		}
	}

	public void triggerEndPeriodEvent(RegisterCFEndPeriodEvent event, Date expiration) {
		TimerConfig timerConfig = new TimerConfig();
		Object[] objs = { event };
		timerConfig.setInfo(objs);

		// used for testing
		// expiration = new Date();
		// expiration = DateUtils.addMinutes(expiration, 1);

		log.debug("creating timer for triggerEndPeriodEvent with expiration={}", expiration);

		timerService.createSingleActionTimer(expiration, timerConfig);
	}

	@Asynchronous
	public void observeEndPeriodEvent(@Observes RegisterCFEndPeriodEvent obj) {
		log.debug("observeEndPeriodEvent={}", obj);

		if (obj.getCustomFieldPeriod().getPeriodEndDate() == null) {
			CFEndPeriodEvent event = new CFEndPeriodEvent();
			event.setCustomFieldPeriod(obj.getCustomFieldPeriod());
			cFEndPeriodEvent.fire(event);
		} else {
			triggerEndPeriodEvent(obj, obj.getCustomFieldPeriod().getPeriodEndDate());
		}
	}

}

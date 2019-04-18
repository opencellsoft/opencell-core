package org.meveo.service.notification;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.IEvent;
import org.meveo.model.IEntity;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.model.notification.ScriptNotification;
import org.meveo.model.notification.WebHook;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterValueInsufficientException;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class DefaultNotificationService {

	private static Logger log = LoggerFactory.getLogger(DefaultNotificationService.class);

	@Inject
	private BeanManager manager;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	private NotificationHistoryService notificationHistoryService;

	@Inject
	private EmailNotifier emailNotifier;

	@Inject
	private WebHookNotifier webHookNotifier;

	@Inject
	private InstantMessagingNotifier imNotifier;

	@Inject
	private CounterInstanceService counterInstanceService;

	@Inject
	private JobTriggerLauncher jobTriggerLauncher;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	private boolean matchExpression(String expression, Object entityOrEvent) throws BusinessException {
		Boolean result = true;
		if (StringUtils.isBlank(expression)) {
			return result;
		}

		Map<Object, Object> userMap = new HashMap<>();
		userMap.put("event", entityOrEvent);

		Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
		try {
			result = (Boolean) res;

		} catch (Exception e) {
			throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
		}

		return result == null ? false : result;
	}

	private void executeScript(ScriptInstance scriptInstance, Object entityOrEvent, Map<String, String> params,
			Map<String, Object> context) throws BusinessException {
		log.debug("execute notification script: {}", scriptInstance.getCode());

		try {

			context.put("entityOrEvent", entityOrEvent);

			Map<Object, Object> userMap = new HashMap<>();
			userMap.put("event", entityOrEvent);
			userMap.put("manager", manager);

			for (Map.Entry<String, String> entry : params.entrySet()) {
				context.put(entry.getKey(),
						ValueExpressionWrapper.evaluateExpression(entry.getValue(), userMap, Object.class));
			}

			if (scriptInstance.getReuse()) {
				scriptInstanceService.executeCached(entityOrEvent, scriptInstance.getCode(), context);

			} else {
				scriptInstanceService.executeWInitAndFinalize(entityOrEvent, scriptInstance.getCode(), context);
			}

		} catch (Exception e) {
			log.error("failed script {} execution", scriptInstance.getCode(), e);
			if (e instanceof BusinessException) {
				throw e;
			} else {
				throw new BusinessException(e);
			}
		}
	}

	public void fireCdrNotification(Notification notif, Object cdr) throws BusinessException {
		log.debug("Fire Cdr Notification for notif {} and  cdr {}", notif, cdr);
		try {
			if (!StringUtils.isBlank(notif.getScriptInstance()) && matchExpression(notif.getElFilter(), cdr)) {
				executeScript(notif.getScriptInstance(), cdr, notif.getParams(), new HashMap<String, Object>());
			}
			
		} catch (BusinessException e1) {
			log.error("Error while firing notification {}: {} ", notif.getCode(), e1);
			throw e1;
		}

	}

	@Asynchronous
	public Future<Boolean> fireNotificationAsync(Notification notif, Object entityOrEvent) throws BusinessException {
		return new AsyncResult<>(fireNotification(notif, entityOrEvent));
	}

	public boolean fireNotification(Notification notif, Object entityOrEvent) throws BusinessException {

		if (notif == null) {
			return false;
		}

		IEntity entity = null;
		if (entityOrEvent instanceof IEntity) {
			entity = (IEntity) entityOrEvent;

		} else if (entityOrEvent instanceof IEvent) {
			entity = ((IEvent) entityOrEvent).getEntity();
		}

		log.debug("Fire Notification for notif with {} and entity with id={}", notif, entity.getId());
		try {
			if (!matchExpression(notif.getElFilter(), entityOrEvent)) {
				log.debug("Expression {} does not match", notif.getElFilter());
				return false;
			}

			boolean sendNotify = true;
			// Check if the counter associated to notification was not exhausted yet
			if (notif.getCounterInstance() != null) {
				try {
					counterInstanceService.deduceCounterValue(notif.getCounterInstance(), new Date(),
							notif.getAuditable().getCreated(), new BigDecimal(1));

				} catch (CounterValueInsufficientException ex) {
					sendNotify = false;
				}
			}

			if (!sendNotify) {
				return false;
			}

			Map<String, Object> context = new HashMap<>();
			// Rethink notif and script - maybe create pre and post script
			if (!(notif instanceof WebHook) && notif.getScriptInstance() != null) {
				executeScript(notif.getScriptInstance(), entityOrEvent, notif.getParams(), context);
			}

			// Execute notification

			// ONLY ScriptNotifications will produce notification history in synchronous
			// mode. Other type notifications will produce notification history in
			// asynchronous mode and
			// thus
			// will not be related to inbound request.
			if (notif instanceof ScriptNotification) {
				NotificationHistory histo = notificationHistoryService.create(notif, entityOrEvent,
						(String) context.get(Script.RESULT_VALUE), NotificationHistoryStatusEnum.SENT);

				if (notif.getEventTypeFilter() == NotificationEventTypeEnum.INBOUND_REQ && histo != null) {
					histo.setInboundRequest((InboundRequest) entityOrEvent);
					((InboundRequest) entityOrEvent).add(histo);
				}

			} else if (notif instanceof EmailNotification) {
				MeveoUser lastCurrentUser = currentUser.unProxy();
				emailNotifier.sendEmail((EmailNotification) notif, entityOrEvent, context, lastCurrentUser);

			} else if (notif instanceof WebHook) {
				MeveoUser lastCurrentUser = currentUser.unProxy();
				webHookNotifier.sendRequest((WebHook) notif, entityOrEvent, context, lastCurrentUser);

			} else if (notif instanceof InstantMessagingNotification) {
				MeveoUser lastCurrentUser = currentUser.unProxy();
				imNotifier.sendInstantMessage((InstantMessagingNotification) notif, entityOrEvent, lastCurrentUser);

			} else if (notif instanceof JobTrigger) {
				MeveoUser lastCurrentUser = currentUser.unProxy();
				jobTriggerLauncher.launch((JobTrigger) notif, entityOrEvent, lastCurrentUser);
			}

		} catch (Exception e1) {
			log.error("Error while firing notification {} ", notif.getCode(), e1);
			try {
				NotificationHistory notificationHistory = notificationHistoryService.create(notif, entityOrEvent,
						e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
				if (entityOrEvent instanceof InboundRequest) {
					((InboundRequest) entityOrEvent).add(notificationHistory);
				}

			} catch (Exception e2) {
				log.error("Failed to create notification history", e2);
			}

			if (!(e1 instanceof BusinessException)) {
				throw new BusinessException(e1);

			} else {
				throw (BusinessException) e1;
			}
		}

		return true;
	}
}

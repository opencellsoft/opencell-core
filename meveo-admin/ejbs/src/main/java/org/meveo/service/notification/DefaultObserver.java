package org.meveo.service.notification;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.monitoring.BusinessExceptionEvent;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.event.qualifier.LoggedIn;
import org.meveo.event.qualifier.LowBalance;
import org.meveo.event.qualifier.Processed;
import org.meveo.event.qualifier.Rejected;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.event.qualifier.Removed;
import org.meveo.event.qualifier.Terminated;
import org.meveo.event.qualifier.Updated;
import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;
import org.meveo.model.IProvider;
import org.meveo.model.admin.User;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.script.JavaCompilerManager;
import org.meveo.script.ScriptInterface;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterValueInsufficientException;
import org.slf4j.Logger;

@Singleton
@Startup
public class DefaultObserver {

    @Inject
    private Logger log;

    @Inject
    private BeanManager manager;

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
    private NotificationCacheContainerProvider notificationCacheContainerProvider;
    
    @Inject
    private RemoteInstanceNotifier remoteInstanceNotifier;
    
    @Inject
    private JavaCompilerManager javaCompilerManager;
    
    @Inject
    private JobTriggerLauncher jobTriggerLauncher;

    private boolean matchExpression(String expression, Object o) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("event", o);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    private String executeAction(String expression, Object o) throws BusinessException {
        log.debug("execute notification action: {}", expression);
        if (StringUtils.isBlank(expression)) {
            return "";
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("event", o);
        userMap.put("manager", manager);
        return (String) ValueExpressionWrapper.evaluateExpression(expression, userMap, String.class);
    }
    
    
    private void executeScript(ScriptInstance scriptInstance, Object o) throws BusinessException {
        log.debug("execute notification script: {}", scriptInstance.getScript());
        ScriptInterface scriptInterface = javaCompilerManager.getAllScriptInterfaces().get(scriptInstance.getCode());
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("event", o);
    	scriptInterface.execute(userMap);
    }    

    private void fireNotification(Notification notif, IEntity e) {
        log.debug("Fire Notification for notif with {} and entity with id={}", notif, e.getId());
        try {
            if (!matchExpression(notif.getElFilter(), e)) {
                return;
            }

            // we first perform the EL actions
            if (!(notif instanceof WebHook)) {
                executeAction(notif.getElAction(), e);
            }
            
            if(notif.getScriptInstance() != null){
            	executeScript(notif.getScriptInstance(), e);
            }

            boolean sendNotify = true;
            // Check if the counter associated to notification was not exhausted
            // yet
            if (notif.getCounterInstance() != null) {
                try {
                    counterInstanceService.deduceCounterValue(notif.getCounterInstance(), new Date(), notif.getAuditable().getCreated(), new BigDecimal(1), notif.getAuditable()
                        .getCreator());
                } catch (CounterValueInsufficientException ex) {
                    sendNotify = false;
                }
            }

            if (!sendNotify) {
                return;
            }
            // then the notification itself
            if (notif instanceof EmailNotification) {
                emailNotifier.sendEmail((EmailNotification) notif, e);
            } else if (notif instanceof WebHook) {
                webHookNotifier.sendRequest((WebHook) notif, e);
            } else if (notif instanceof InstantMessagingNotification) {
                imNotifier.sendInstantMessage((InstantMessagingNotification) notif, e);
            } else if (notif.getEventTypeFilter() != NotificationEventTypeEnum.INBOUND_REQ){
                notificationHistoryService.create(notif, e, "", NotificationHistoryStatusEnum.SENT);
            }
            if (notif.getEventTypeFilter() == NotificationEventTypeEnum.INBOUND_REQ) {
                NotificationHistory histo = notificationHistoryService.create(notif, e, "", NotificationHistoryStatusEnum.SENT);
                ((InboundRequest) e).add(histo);
            }
            if(notif instanceof JobTrigger){
            	jobTriggerLauncher.launch((JobTrigger) notif, e);
            }

        } catch (BusinessException e1) {
            log.error("Error while firing notification {} for provider {}: {} ", notif.getCode(), notif.getProvider().getCode(), e1);
            try {
                NotificationHistory notificationHistory = notificationHistoryService.create(notif, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
                if (e instanceof InboundRequest) {
                    ((InboundRequest) e).add(notificationHistory);
                }
            } catch (BusinessException e2) {
            	log.error("Failed to firing notification",e);
            }
        }
    }

    private void fireCdrNotification(Notification notif, IProvider cdr) {
        log.debug("Fire Cdr Notification for notif {} and  cdr {}", notif, cdr);
        try {
            if (!StringUtils.isBlank(notif.getElAction()) && matchExpression(notif.getElFilter(), cdr)) {
                executeAction(notif.getElAction(), cdr);
            }
        } catch (BusinessException e1) {
            log.error("Error while firing notification {} for provider {}: {} ", notif.getCode(), notif.getProvider().getCode(), e1);
        }

    }

    private void checkEvent(NotificationEventTypeEnum type, BaseEntity entity) {

        for (Notification notif : notificationCacheContainerProvider.getApplicableNotifications(type, entity)) {
            fireNotification(notif, entity);
        }
    }

    public void entityCreated(@Observes @Created BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} created", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.CREATED, e);
    }

    public void entityUpdated(@Observes @Updated BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} updated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.UPDATED, e);
    }

    public void entityRemoved(@Observes @Removed BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} removed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REMOVED, e);
    }

    public void entityDisabled(@Observes @Disabled BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} disabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.DISABLED, e);
    }

    public void entityEnabled(@Observes @Enabled BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} enabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.ENABLED, e);
    }

    public void entityTerminated(@Observes @Terminated BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} terminated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.TERMINATED, e);
    }

    public void entityProcessed(@Observes @Processed BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} processed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.PROCESSED, e);
    }

    public void entityRejected(@Observes @Rejected BaseEntity e) {
        log.debug("Defaut observer : Entity {} with id {} rejected", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REJECTED, e);
    }

    public void cdrRejected(@Observes @RejectedCDR IProvider cdr) {
        log.debug("Defaut observer : cdr {} rejected", cdr);
        for (Notification notif : notificationCacheContainerProvider.getApplicableNotifications(NotificationEventTypeEnum.REJECTED_CDR, cdr)) {
            fireCdrNotification(notif, cdr);
        }
    }

    public void loggedIn(@Observes @LoggedIn User e) {
        log.debug("Defaut observer : logged in class={} ", e.getClass().getName());
        checkEvent(NotificationEventTypeEnum.LOGGED_IN, e);
    }

    public void inboundRequest(@Observes @InboundRequestReceived InboundRequest e) {
        log.debug("Defaut observer : inbound request {} ", e.getCode());
        checkEvent(NotificationEventTypeEnum.INBOUND_REQ, e);
    }
    
    
   public void LowBalance(@Observes @LowBalance WalletInstance e){
       log.debug("Defaut observer : low balance on {} ", e.getCode());
       checkEvent(NotificationEventTypeEnum.LOW_BALANCE, e);
       
   }
   
   public void businesException(@Observes  BusinessExceptionEvent bee){
       log.info("Defaut observer : BusinessExceptionEvent {} ", bee);
       StringWriter errors = new StringWriter();
       bee.getBusinessException().printStackTrace(new PrintWriter(errors));
       String input = "{"+
				"	  #meveoInstanceCode#: #myMeveo#,"+
				"	  #subject#: #"+bee.getBusinessException().getMessage()+"#,"+
				"	  #body#: #"+errors.toString()+"#,"+
				"	  #additionnalInfo1#: #"+LogExtractionService.getLogs(new Date(System.currentTimeMillis()-Integer.parseInt(ParamBean.getInstance().
						getProperty("meveo.notifier.log.timeBefore", "5000"))) , new Date())+"#,"+
				"	  #additionnalInfo2#: ##,"+
				"	  #additionnalInfo3#: ##,"+
				"	  #additionnalInfo4#: ##"+
				"}";
       log.info("Defaut observer : input {} ", input.replaceAll("#", "\""));
       //TODO send a jsonObject
       remoteInstanceNotifier.invoke(input.replaceAll("\"","'").replaceAll("#", "\""),ParamBean.getInstance().getProperty("inboundCommunication.url", "http://version.meveo.info/meveo-moni/api/rest/inboundCommunication"));
       
		//TODO handel reponse
		//if pertinent, if need logs
      
   }
}
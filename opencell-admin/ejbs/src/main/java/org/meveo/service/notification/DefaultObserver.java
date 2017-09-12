package org.meveo.service.notification;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.ftp.event.FileDelete;
import org.meveo.admin.ftp.event.FileDownload;
import org.meveo.admin.ftp.event.FileRename;
import org.meveo.admin.ftp.event.FileUpload;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.CFEndPeriodEvent;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.event.IEvent;
import org.meveo.event.communication.InboundCommunicationEvent;
import org.meveo.event.logging.LoggedEvent;
import org.meveo.event.monitoring.BusinessExceptionEvent;
import org.meveo.event.qualifier.Created;
import org.meveo.event.qualifier.Disabled;
import org.meveo.event.qualifier.Enabled;
import org.meveo.event.qualifier.EndOfTerm;
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
import org.meveo.model.admin.User;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.mediation.MeveoFtpFile;
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
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.CounterValueInsufficientException;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Singleton
@Startup
@LoggedEvent
@Lock(LockType.READ)
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

    // @Inject
    // private RemoteInstanceNotifier remoteInstanceNotifier;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private JobTriggerLauncher jobTriggerLauncher;

    private boolean matchExpression(String expression, Object entityOrEvent) throws BusinessException {
        Boolean result = true;
        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("event", entityOrEvent);

        Object res = ValueExpressionWrapper.evaluateExpression(expression, userMap, Boolean.class);
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }

    private void executeScript(ScriptInstance scriptInstance, Object entityOrEvent, Map<String, String> params, Map<String, Object> context) throws BusinessException {
        log.debug("execute notification script: {}", scriptInstance.getCode());

        try {
            ScriptInterface scriptInterface = scriptInstanceService.getScriptInstance(scriptInstance.getCode());
            Map<Object, Object> userMap = new HashMap<>();
            userMap.put("event", entityOrEvent);
            userMap.put("manager", manager);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                context.put(entry.getKey(), ValueExpressionWrapper.evaluateExpression(entry.getValue(), userMap, Object.class));
            }
            scriptInterface.init(context);
            scriptInterface.execute(context);
            scriptInterface.finalize(context);
        } catch (Exception e) {
            log.error("failed script execution", e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new BusinessException(e);
            }
        }
    }

    private boolean fireNotification(Notification notif, Object entityOrEvent) throws BusinessException {
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
                    CounterInstance counterInstance = counterInstanceService.refreshOrRetrieve(notif.getCounterInstance());
                    counterInstanceService.deduceCounterValue(counterInstance, new Date(), notif.getAuditable().getCreated(), new BigDecimal(1));
                } catch (CounterValueInsufficientException ex) {
                    sendNotify = false;
                }
            }

            if (!sendNotify) {
                return false;
            }

            Map<String, Object> context = new HashMap<String, Object>();
            // Rethink notif and script - maybe create pre and post script
            if (!(notif instanceof WebHook)) {
                if (notif.getScriptInstance() != null) {
                    executeScript(notif.getScriptInstance(), entityOrEvent, notif.getParams(), context);
                }
            }

            // Execute notification

            // ONLY ScriptNotifications will produce notification history in synchronous mode. Other type notifications will produce notification history in asynchronous mode and
            // thus
            // will not be related to inbound request.
            if (notif instanceof ScriptNotification) {
                NotificationHistory histo = notificationHistoryService.create(notif, entityOrEvent, (String)context.get(Script.RESULT_VALUE), NotificationHistoryStatusEnum.SENT);

                if (notif.getEventTypeFilter() == NotificationEventTypeEnum.INBOUND_REQ && histo != null) {
                    ((InboundRequest) entityOrEvent).add(histo);
                }

            } else if (notif instanceof EmailNotification) {
                emailNotifier.sendEmail((EmailNotification) notif, entityOrEvent, context);

            } else if (notif instanceof WebHook) {
                webHookNotifier.sendRequest((WebHook) notif, entityOrEvent, context);

            } else if (notif instanceof InstantMessagingNotification) {
                imNotifier.sendInstantMessage((InstantMessagingNotification) notif, entityOrEvent);

            } else if (notif instanceof JobTrigger) {
                jobTriggerLauncher.launch((JobTrigger) notif, entityOrEvent);
            }

        } catch (Exception e1) {
            log.error("Error while firing notification {} ", notif.getCode(), e1);
            try {
                NotificationHistory notificationHistory = notificationHistoryService.create(notif, entityOrEvent, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
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

    private void fireCdrNotification(Notification notif, Object cdr) throws BusinessException {
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

    /**
     * Check and fire all matched notifications
     * 
     * @param type Notification type
     * @param entityOrEvent Entity or event triggered
     * @return True if at least one notification has been triggered
     * @throws BusinessException
     */
    private boolean checkEvent(NotificationEventTypeEnum type, Object entityOrEvent) throws BusinessException {
        boolean result = false;
        for (Notification notif : notificationCacheContainerProvider.getApplicableNotifications(type, entityOrEvent)) {
            result = fireNotification(notif, entityOrEvent) || result;
        }
        return result;
    }

    public void entityCreated(@Observes @Created BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} created", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.CREATED, e);
    }

    public void entityUpdated(@Observes @Updated BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} updated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.UPDATED, e);
    }

    public void entityRemoved(@Observes @Removed BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} removed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REMOVED, e);
    }

    public void entityDisabled(@Observes @Disabled BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} disabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.DISABLED, e);
    }

    public void entityEnabled(@Observes @Enabled BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} enabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.ENABLED, e);
    }

    public void entityTerminated(@Observes @Terminated BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} terminated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.TERMINATED, e);
    }

    public void entityProcessed(@Observes @Processed BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} processed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.PROCESSED, e);
    }

    public void entityRejected(@Observes @Rejected BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} rejected", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REJECTED, e);
    }
    
    public void entityEndOfTerm(@Observes @EndOfTerm BaseEntity e) throws BusinessException {
        log.debug("Defaut observer : Entity {} with id {} end of term", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.END_OF_TERM, e);
    }

    public void cdrRejected(@Observes @RejectedCDR Object cdr) throws BusinessException {
        log.debug("Defaut observer : cdr {} rejected", cdr);
        for (Notification notif : notificationCacheContainerProvider.getApplicableNotifications(NotificationEventTypeEnum.REJECTED_CDR, cdr)) {
            fireCdrNotification(notif, cdr);
        }
    }

    public void loggedIn(@Observes @LoggedIn User e) throws BusinessException {
        log.debug("Defaut observer : logged in class={} ", e.getClass().getName());
        checkEvent(NotificationEventTypeEnum.LOGGED_IN, e);
    }

    @MeveoAudit
    public void inboundRequest(@Observes @InboundRequestReceived InboundRequest e) throws BusinessException {
        log.debug("Defaut observer : inbound request {} ", e.getCode());
        boolean fired = checkEvent(NotificationEventTypeEnum.INBOUND_REQ, e);
        e.getHeaders().put("fired", fired ? "true" : "false");
    }

    @MeveoAudit
    public void LowBalance(@Observes @LowBalance WalletInstance e) throws BusinessException {
        log.debug("Defaut observer : low balance on {} ", e.getCode());
        checkEvent(NotificationEventTypeEnum.LOW_BALANCE, e);

    }

    public void businesException(@Observes BusinessExceptionEvent bee) {
        log.debug("BusinessExceptionEvent handler inactivated {}",
            bee);/*
                  * log.debug("Defaut observer : BusinessExceptionEvent {} ", bee); StringWriter errors = new StringWriter(); bee.getException().printStackTrace(new
                  * PrintWriter(errors)); String meveoInstanceCode = ParamBean.getInstance().getProperty("monitoring.instanceCode", ""); int bodyMaxLegthByte =
                  * Integer.parseInt(ParamBean.getInstance().getProperty("meveo.notifier.stackTrace.lengthInBytes", "9999")); String stackTrace = errors.toString(); String input =
                  * "{" + "	  #meveoInstanceCode#: #" + meveoInstanceCode + "#," + "	  #subject#: #" + bee.getException().getMessage() + "#," + "	  #body#: #" +
                  * StringUtils.truncate(stackTrace, bodyMaxLegthByte, true) + "#," + "	  #additionnalInfo1#: #" + LogExtractionService.getLogs( new Date(System.currentTimeMillis()
                  * - Integer.parseInt(ParamBean.getInstance().getProperty("meveo.notifier.log.timeBefore_ms", "5000"))), new Date()) + "#," + "	  #additionnalInfo2#: ##," +
                  * "	  #additionnalInfo3#: ##," + "	  #additionnalInfo4#: ##" + "}"; log.trace("Defaut observer : input {} ", input.replaceAll("#", "\""));
                  * remoteInstanceNotifier.invoke(input.replaceAll("\"", "'").replaceAll("#", "\"").replaceAll("\\[", "(").replaceAll("\\]", ")"),
                  * ParamBean.getInstance().getProperty("inboundCommunication.url", "http://version.meveo.info/meveo-moni/api/rest/inboundCommunication"));
                  * 
                  */

    }

    public void customFieldEndPeriodEvent(@Observes CFEndPeriodEvent event) {
        log.debug("DefaultObserver.customFieldEndPeriodEvent : {}", event);
    }

    public void knownMeveoInstance(@Observes InboundCommunicationEvent event) {
        log.debug("DefaultObserver.knownMeveoInstance" + event);
    }

    public void ftpFileUpload(@Observes @FileUpload MeveoFtpFile importedFile) throws BusinessException {
        log.debug("observe a file upload event ");
        checkEvent(NotificationEventTypeEnum.FILE_UPLOAD, importedFile);
    }

    public void ftpFileDownload(@Observes @FileDownload MeveoFtpFile importedFile) throws BusinessException {
        log.debug("observe a file download event ");
        checkEvent(NotificationEventTypeEnum.FILE_DOWNLOAD, importedFile);
    }

    public void ftpFileDelete(@Observes @FileDelete MeveoFtpFile importedFile) throws BusinessException {
        log.debug("observe a file delete event ");
        checkEvent(NotificationEventTypeEnum.FILE_DELETE, importedFile);
    }

    public void ftpFileRename(@Observes @FileRename MeveoFtpFile importedFile) throws BusinessException {
        log.debug("observe a file rename event ");
        checkEvent(NotificationEventTypeEnum.FILE_RENAME, importedFile);
    }

    public void counterUpdated(@Observes CounterPeriodEvent event) throws BusinessException {
        log.debug("DefaultObserver.counterUpdated " + event);
        checkEvent(NotificationEventTypeEnum.COUNTER_DEDUCED, event);
    }

}

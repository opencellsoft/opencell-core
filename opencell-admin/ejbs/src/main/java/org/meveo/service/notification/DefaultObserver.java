/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.notification;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.ftp.event.FileDelete;
import org.meveo.admin.ftp.event.FileDownload;
import org.meveo.admin.ftp.event.FileRename;
import org.meveo.admin.ftp.event.FileUpload;
import org.meveo.audit.AuditableFieldEvent;
import org.meveo.audit.logging.annotations.MeveoAudit;
import org.meveo.event.CFEndPeriodEvent;
import org.meveo.event.CounterPeriodEvent;
import org.meveo.event.communication.InboundCommunicationEvent;
import org.meveo.event.logging.LoggedEvent;
import org.meveo.event.monitoring.BusinessExceptionEvent;
import org.meveo.event.qualifier.*;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomTableEvent;
import org.meveo.model.admin.User;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditableFieldHistory;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.mediation.MeveoFtpFile;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles events associated with CDRUD operations on entities
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Singleton
@Startup
@LoggedEvent
@Lock(LockType.READ)
public class DefaultObserver {

    private static Logger log = LoggerFactory.getLogger(DefaultObserver.class);

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private GenericNotificationService genericNotificationService;

    // @Inject
    // private RemoteInstanceNotifier remoteInstanceNotifier;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private DefaultNotificationService defaultNotificationService;

    /**
     * Check and fire all matched notifications
     * 
     * @param type Notification type
     * @param entityOrEvent Entity or event triggered
     * @return True if at least one notification has been triggered
     * @throws BusinessException Business exception
     */
    private boolean checkEvent(NotificationEventTypeEnum type, Object entityOrEvent) throws BusinessException {
        boolean result = false;

        for (Notification notif : genericNotificationService.getApplicableNotifications(type, entityOrEvent)) {
            if (notif.isRunAsync()) {
                defaultNotificationService.fireNotificationAsync(notif, entityOrEvent);
                result = true;

            } else {
                result = defaultNotificationService.fireNotification(notif, entityOrEvent) || result;
            }

        }

        return result;
    }

    public void entityInstantiateWF(@Observes @InstantiateWF BusinessEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} instantiateWF", e.getClass().getName(), e.getId());

        List<GenericWorkflow> genericWorkflows = genericWorkflowService.findByBusinessEntity(e);
        for (GenericWorkflow genericWorkflow : genericWorkflows) {
            workflowInstanceService.create(e, genericWorkflow);
        }
    }

    public void customEntityChange(@Observes CustomTableEvent e) throws BusinessException {
        log.debug("Defaut observer: CustomEntity {} with id {} created", e.getClass().getName());
        checkEvent(e.getType(), e);
    }
    
    public void createUserChange(@Observes User e) throws BusinessException {
        log.debug("Defaut observer: UserEvent with id {} created", e);
        checkEvent(NotificationEventTypeEnum.CREATED, e);
    }

    public void entityCreated(@Observes @Created BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} created", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.CREATED, e);
    }

    public void entityUpdated(@Observes @Updated BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} updated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.UPDATED, e);
    }

    public void entityRemoved(@Observes @Removed BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} removed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REMOVED, e);
    }

    public void entityDisabled(@Observes @Disabled BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} disabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.DISABLED, e);
    }

    public void entityEnabled(@Observes @Enabled BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} enabled", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.ENABLED, e);
    }

    public void entityTerminated(@Observes @Terminated BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} terminated", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.TERMINATED, e);
    }

    public void entityProcessed(@Observes @Processed BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} processed", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.PROCESSED, e);
    }

    public void entityRejected(@Observes @Rejected BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} rejected", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.REJECTED, e);
    }

    public void entityEndOfTerm(@Observes @EndOfTerm BaseEntity e) throws BusinessException {
        log.debug("Defaut observer: Entity {} with id {} end of term", e.getClass().getName(), e.getId());
        checkEvent(NotificationEventTypeEnum.END_OF_TERM, e);
    }

    public void cdrRejected(@Observes @RejectedCDR Object cdr) throws BusinessException {
        log.debug("Defaut observer: cdr {} rejected", cdr);
        for (Notification notif : genericNotificationService.getApplicableNotifications(NotificationEventTypeEnum.REJECTED_CDR, cdr)) {
            defaultNotificationService.fireCdrNotification(notif, cdr);
        }
    }

    public void loggedIn(@Observes @LoggedIn User e) throws BusinessException {
        log.debug("Defaut observer: logged in class={} ", e.getClass().getName());
        checkEvent(NotificationEventTypeEnum.LOGGED_IN, e);
    }

    @MeveoAudit
    public void inboundRequest(@Observes @InboundRequestReceived InboundRequest e) throws BusinessException {
        log.debug("Defaut observer: inbound request {} ", e.getCode());
        boolean fired = checkEvent(NotificationEventTypeEnum.INBOUND_REQ, e);
        e.getHeaders().put("fired", fired ? "true" : "false");
    }

    @MeveoAudit
    public void LowBalance(@Observes @LowBalance WalletInstance e) throws BusinessException {
        log.debug("Defaut observer: low balance on {} ", e.getCode());
        checkEvent(NotificationEventTypeEnum.LOW_BALANCE, e);

    }

    public void businesException(@Observes BusinessExceptionEvent bee) {
        log.debug("BusinessExceptionEvent handler inactivated {}",
            bee);/*
                  * log.debug("Defaut observer: BusinessExceptionEvent {} ", bee); StringWriter errors = new StringWriter(); bee.getException().printStackTrace(new
                  * PrintWriter(errors)); String meveoInstanceCode = ParamBean.getInstance().getProperty("monitoring.instanceCode", ""); int bodyMaxLegthByte =
                  * Integer.parseInt(ParamBean.getInstance().getProperty("meveo.notifier.stackTrace.lengthInBytes", "9999")); String stackTrace = errors.toString(); String input =
                  * "{" + "	  #meveoInstanceCode#: #" + meveoInstanceCode + "#," + "	  #subject#: #" + bee.getException().getMessage() + "#," + "	  #body#: #" +
                  * StringUtils.truncate(stackTrace, bodyMaxLegthByte, true) + "#," + "	  #additionnalInfo1#: #" + LogExtractionService.getLogs( new Date(System.currentTimeMillis()
                  * - Integer.parseInt(ParamBean.getInstance().getProperty("meveo.notifier.log.timeBefore_ms", "5000"))), new Date()) + "#," + "	  #additionnalInfo2#: ##," +
                  * "	  #additionnalInfo3#: ##," + "	  #additionnalInfo4#: ##" + "}"; log.trace("Defaut observer: input {} ", input.replaceAll("#", "\""));
                  * remoteInstanceNotifier.invoke(input.replaceAll("\"", "'").replaceAll("#", "\"").replaceAll("\\[", "(").replaceAll("\\]", ")"),
                  * ParamBean.getInstance().getProperty("inboundCommunication.url", "http://version.meveo.info/meveo-moni/api/rest/inboundCommunication"));
                  * 
                  */

    }

    public void customFieldEndPeriodEvent(@Observes CFEndPeriodEvent event) {
        log.debug("Defaut observer: custom field period expired : {}", event);
    }

    public void knownMeveoInstance(@Observes InboundCommunicationEvent event) {
        log.debug("DefaultObserver.knownMeveoInstance" + event);
    }

    public void ftpFileUpload(@Observes @FileUpload MeveoFtpFile importedFile) throws BusinessException {
        log.debug("Defaut observer:  a file upload event ");
        checkEvent(NotificationEventTypeEnum.FILE_UPLOAD, importedFile);
    }

    public void ftpFileDownload(@Observes @FileDownload MeveoFtpFile importedFile) throws BusinessException {
        log.debug("Defaut observer: a file download event ");
        checkEvent(NotificationEventTypeEnum.FILE_DOWNLOAD, importedFile);
    }

    public void ftpFileDelete(@Observes @FileDelete MeveoFtpFile importedFile) throws BusinessException {
        log.debug("Defaut observer: a file delete event ");
        checkEvent(NotificationEventTypeEnum.FILE_DELETE, importedFile);
    }

    public void ftpFileRename(@Observes @FileRename MeveoFtpFile importedFile) throws BusinessException {
        log.debug("Defaut observer: a file rename event ");
        checkEvent(NotificationEventTypeEnum.FILE_RENAME, importedFile);
    }

    public void counterUpdated(@Observes CounterPeriodEvent event) throws BusinessException {
        log.debug("Defaut observer: Counter period deduced " + event);
        checkEvent(NotificationEventTypeEnum.COUNTER_DEDUCED, event);
    }

    /**
     * Handle Invoice PDF generated event
     * 
     * @param invoice Invoice
     * @throws BusinessException General business exception
     */
    public void pdfGenerated(@Observes @PDFGenerated Invoice invoice) throws BusinessException {
        log.debug("Defaut observer: PDF generated for invoice {} ", invoice.getId());
        checkEvent(NotificationEventTypeEnum.PDF_GENERATED, invoice);
    }

    /**
     * Handle Invoice XML generated event
     * 
     * @param invoice Invoice
     * @throws BusinessException General business exception
     */
    public void xmlGenerated(@Observes @XMLGenerated Invoice invoice) throws BusinessException {
        log.debug("Defaut observer: XML generated for invoice {} ", invoice.getId());
        checkEvent(NotificationEventTypeEnum.XML_GENERATED, invoice);
    }

    /**
     * Handle Invoice number assigned event
     *
     * @param invoice Invoice
     * @throws BusinessException General business exception
     */
    public void invoiceNumberAssigned(@Observes @InvoiceNumberAssigned Invoice invoice) throws BusinessException {
        log.debug("Defaut observer: Subscription version created, id ", invoice.getId());
        checkEvent(NotificationEventTypeEnum.INVOICE_NUMBER_ASSIGNED, invoice);
    }

    /**
     * Handle Subscription version
     *
     * @param Subscription subscription
     * @throws BusinessException General business exception
     */
    public void versionCreated(@Observes @VersionCreated Subscription subscription) throws BusinessException {
        log.debug("Defaut observer: Subscription version created, id: ", subscription.getId());
        checkEvent(NotificationEventTypeEnum.VERSION_CREATED, subscription);
    }

    /**
     * Handle Subscription version
     *
     * @param Subscription subscription
     * @throws BusinessException General business exception
     */
    public void versionRemoved(@Observes @VersionRemoved Subscription subscription) throws BusinessException {
        log.debug("Defaut observer: Subscription version removed, id: ", subscription.getId());
        checkEvent(NotificationEventTypeEnum.VERSION_REMOVED, subscription);
    }

    private void fieldUpdated(BaseEntity entity, AuditableFieldEvent field, NotificationEventTypeEnum notificationType) throws BusinessException {
        if (entity != null) {
            checkEvent(notificationType, field);
        }
    }

    private void fieldUpdated(AuditableEntity entity, AuditableFieldHistory fieldHistory) throws BusinessException {
        AuditableFieldEvent field = new AuditableFieldEvent();
        try {
            BeanUtils.copyProperties(field, fieldHistory);
            field.setEntity(entity);
            // In the case of a status field, we fire an status event.
            if (fieldHistory.getAuditType() == AuditChangeTypeEnum.STATUS) {
                fieldUpdated(entity, field, NotificationEventTypeEnum.STATUS_UPDATED);
            }
            // In the case of a renewal field, we fire an renewal event.
            if (fieldHistory.getAuditType() == AuditChangeTypeEnum.RENEWAL) {
                fieldUpdated(entity, field, NotificationEventTypeEnum.RENEWAL_UPDATED);
            }
            fieldHistory.setNotified(true);
        } catch (IllegalAccessException | InvocationTargetException e) {
            fieldHistory.setNotified(false);
            log.error("Failed to fire field updated notification");
            throw new BusinessException(e.getMessage());
        } catch (BusinessException e) {
            fieldHistory.setNotified(false);
            log.error("Failed to fire field updated notification");
            throw e;
        }
    }

    public void fieldsUpdated(@Observes Set<BaseEntity> event) throws BusinessException {
        if (event != null && !event.isEmpty()) {
            for (BaseEntity baseEntity : event) {
                AuditableEntity entity = (AuditableEntity) baseEntity;
                Set<AuditableFieldHistory> auditableFields = entity.getAuditableFields();
                if (!entity.isNotified() && auditableFields != null && !auditableFields.isEmpty()) {
                    for (AuditableFieldHistory fieldHistory : auditableFields) {
                        // Check if the field is notifiable and is not yet notified
                        if (fieldHistory.isNotfiable() && !fieldHistory.isNotified()) {
                            fieldUpdated(entity, fieldHistory);
                        }
                    }
                    entity.setNotified(true);
                }

            }
        }
    }

}

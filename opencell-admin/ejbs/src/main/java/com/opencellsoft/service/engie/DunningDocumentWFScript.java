package com.opencellsoft.service.engie;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.dunning.DunningDocumentStatusEnum;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.generic.wf.WorkflowInstanceHistoryService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.meveo.service.payments.impl.DunningDocumentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * WF Script for entity dunning doc
 *
 * @author mboukayoua
 */
public class DunningDocumentWFScript extends Script {

    /**
     * script constantes
     */
    private static final String WF_INS = "WF_INS";
    private static final String DUNNING_R1_EMAIL_FROM = "DUNNING_R1_EMAIL_FROM";
    private static final String DUNNING_R1_EMAIL_SUBJECT = "DUNNING_R1_EMAIL_SUBJECT";
    private static final String DUNNING_R1_EMAIL_BODY_TPL = "DUNNING_R1_EMAIL_BODY_TPL";

    private static final String DUNNING_R2_DELAY = "DUNNING_R2_DELAY";
    private static final String DUNNING_R2_EMAIL_FROM = "DUNNING_R2_EMAIL_FROM";
    private static final String DUNNING_R2_EMAIL_SUBJECT = "DUNNING_R2_EMAIL_SUBJECT";
    private static final String DUNNING_R2_EMAIL_BODY_TPL = "DUNNING_R2_EMAIL_BODY_TPL";

    private static final String DUNNING_R3_DELAY = "DUNNING_R3_DELAY";
    private static final String DUNNING_R4_DELAY = "DUNNING_R4_DELAY";

    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    /**
     * WF Instance service
     */
    private WorkflowInstanceService workflowInstanceService = (WorkflowInstanceService) getServiceInterface("WorkflowInstanceService");
    /**
     *
     */
    private WorkflowInstanceHistoryService wfiHistoryService = (WorkflowInstanceHistoryService) getServiceInterface("WorkflowInstanceHistoryService");
    /**
     * WF Instance service
     */
    private DunningDocumentService dunningDocumentService = (DunningDocumentService) getServiceInterface("DunningDocumentService");

    private RecordedInvoiceService recordedInvoiceService = (RecordedInvoiceService) getServiceInterface("RecordedInvoiceService");
    /**
     * Provider
     */
    private final Provider provider = ((ProviderService) getServiceInterface("ProviderService")).getProvider();
    /**
     * Bean params
     */
    private ParamBean paramBean = ParamBean.getInstance();

    /**
     * script's execute methode
     * @param methodContext context
     * @throws BusinessException Business Exception
     */
    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        WorkflowInstance workflowInstance = (WorkflowInstance) methodContext.get(WF_INS);

        String code = workflowInstance.getEntityInstanceCode();
        WFStatus currentStatus = workflowInstance.getWfStatus();
        DunningDocument dunningDocument = dunningDocumentService.findByCode(code);
        try {
            if (DunningDocumentStatusEnum.R1.name().equals(currentStatus.getCode())) {
                sendDunningEmailToCustomer(dunningDocument);
                workflowInstanceService.changeStatus(workflowInstance, DunningDocumentStatusEnum.R2.name());
                createWFInstanceHistory(workflowInstance, currentStatus, DunningDocumentStatusEnum.R2.getDescription());
            }
            if (DunningDocumentStatusEnum.R2.name().equals(currentStatus.getCode())
                    && isDelayFromLastEventIsPassed(workflowInstance, DUNNING_R2_DELAY)) {

                notifyInternalAgentByEmail(dunningDocument);
                workflowInstanceService.changeStatus(workflowInstance, DunningDocumentStatusEnum.R3.name());
                createWFInstanceHistory(workflowInstance, currentStatus, DunningDocumentStatusEnum.R3.getDescription());
            }
            if (DunningDocumentStatusEnum.R3.name().equals(currentStatus.getCode())
                    && isDelayFromLastEventIsPassed(workflowInstance, DUNNING_R3_DELAY)) {
                //suspend souscription and mark due invoices as diputed
                subscriptionService.subscriptionSuspension(dunningDocument.getSubscription(), new Date());
                for (RecordedInvoice recordedInvoice : dunningDocument.getDueInvoices()){
                    recordedInvoiceService.addLitigation(recordedInvoice);
                }
                exportEfficoData(dunningDocument);
                workflowInstanceService.changeStatus(workflowInstance, DunningDocumentStatusEnum.R4.name());
                createWFInstanceHistory(workflowInstance, currentStatus, DunningDocumentStatusEnum.R4.getDescription());
            }
            /*if (DunningDocumentStatusEnum.R4.name().equals(currentStatus.getCode())
                    && isDelayFromLastEventIsPassed(workflowInstance, DUNNING_R4_DELAY)){
            }*/
        }catch (DunningDocumentWFException e){
            throw new BusinessException(e);
        }
    }

    /**
     * Export Effico data
     * @param dunningDocument dunning Document
     */
    private void exportEfficoData(DunningDocument dunningDocument) {
        //todo
    }

    private boolean isDelayFromLastEventIsPassed(WorkflowInstance workflowInstance, String delayCfCode) throws DunningDocumentWFException {
        WorkflowInstanceHistory lastWFHistory = wfiHistoryService.getLastWFHistory(workflowInstance);
        if(lastWFHistory == null){
            throw new DunningDocumentWFException("no last event is found for dunningDoc but its status is great then R1");
        }
        LocalDate dateLastHistory = lastWFHistory.getActionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int sincelastEvent = Period.between(dateLastHistory, LocalDate.now()).getDays();
        Integer delay = (Integer) provider.getCfValue(delayCfCode);
        if(delay == null){
            throw new DunningDocumentWFException("CF " + delayCfCode + " not defined.");
        }
        return delay < sincelastEvent;
    }

    private void notifyInternalAgentByEmail(DunningDocument dunningDocument) throws DunningDocumentWFException {
        Session session = initSmtpSession();
        // Construct and send the email
        Customer customer = dunningDocument.getCustomerAccount().getCustomer();
        String emailTo = customer.getContactInformation().getEmail();
        String emailFrom = (String) provider.getCfValue(DUNNING_R2_EMAIL_FROM);
        String emailSubj = (String) provider.getCfValue(DUNNING_R2_EMAIL_SUBJECT);
        String emailTpl = (String) provider.getCfValue(DUNNING_R2_EMAIL_BODY_TPL);

        try {
            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setFrom(new InternetAddress(emailFrom, "Internal billing system"));
            message.setSubject(emailSubj);

            Map<String, Object> velocityParams = new HashMap<>();
            velocityParams.put("CUSTOMER", customer);
            velocityParams.put("DUE_INVOICES", dunningDocument.getDueInvoices());
            String emailMsg = VelocityHelper.fillTemplate(emailTpl, velocityParams);
            message.setContent(emailMsg, "text/html");

            Transport.send(message);
        } catch (Exception e) {
            throw new DunningDocumentWFException("error on sending R1 email to client", e);
        }
    }

    private void createWFInstanceHistory(WorkflowInstance workflowInstance, WFStatus statusFrom, String event) throws BusinessException {
        WorkflowInstanceHistory wfiHistory =  new WorkflowInstanceHistory();
        wfiHistory.setWorkflowInstance(workflowInstance);
        wfiHistory.setActionDate(new Date());
        wfiHistory.setEvent(event);
        wfiHistory.setWfStatusFrom(statusFrom);
        wfiHistory.setWfStatusTo(workflowInstance.getWfStatus());
        wfiHistoryService.create(wfiHistory);
    }

    /**
     * send email with due invoices to client for dunning step R1
     * @param dunningDocument
     * @throws DunningDocumentWFException
     */
    private void sendDunningEmailToCustomer(DunningDocument dunningDocument) throws DunningDocumentWFException {
        Session session = initSmtpSession();
        // Construct and send the email
        Customer customer = dunningDocument.getCustomerAccount().getCustomer();
        String emailTo = customer.getContactInformation().getEmail();
        String emailFrom = (String) provider.getCfValue(DUNNING_R1_EMAIL_FROM);
        String emailSubj = (String) provider.getCfValue(DUNNING_R1_EMAIL_SUBJECT);
        String emailTpl = (String) provider.getCfValue(DUNNING_R1_EMAIL_BODY_TPL);

        try {
            Message message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setFrom(new InternetAddress(emailFrom, "Service client Engie"));
            message.setSubject(emailSubj);

            Map<String, Object> velocityParams = new HashMap<>();
            velocityParams.put("CUSTOMER", customer);
            velocityParams.put("DUE_INVOICES", dunningDocument.getDueInvoices());
            String emailMsg = VelocityHelper.fillTemplate(emailTpl, velocityParams);
            message.setContent(emailMsg, "text/html");

            Transport.send(message);
        } catch (Exception e) {
            throw new DunningDocumentWFException("error on sending R1 email to client", e);
        }
    }

    private Session initSmtpSession() {
        // init SMTP session.
        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.host", paramBean.getProperty("mail.smtp.host", null));
        props.put("mail.smtp.port", paramBean.getProperty("mail.smtp.port", "25"));
        props.put("mail.smtp.auth", true);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = paramBean.getProperty("mail.smtp.username", null);
                String password = paramBean.getProperty("mail.smtp.password", null);
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /**
     * helper class for velocity
     */
    private static class VelocityHelper {
        /** velocity engine single instance*/
        private static VelocityEngine engine;

        /**
         * fill velocity template
         * @param emailTpl email template
         * @param velocityParams velocity Params
         * @return
         * @throws DunningDocumentWFException
         */
        public static String fillTemplate(String emailTpl, Map<String, Object> velocityParams) throws DunningDocumentWFException {
            StringWriter stringWriter = new StringWriter();
            try {
                VelocityEngine velocityEngine = getVelocityEngine();

                VelocityContext velocityContext = new VelocityContext();
                for (Map.Entry<String, Object> param : velocityParams.entrySet()){
                    velocityContext.put(param.getKey(), param.getValue());
                }
                velocityEngine.evaluate(velocityContext, stringWriter, "DunningR1EmailTemplate", emailTpl);
            } catch (Exception e) {
                throw new DunningDocumentWFException("error on filling email msg from template", e);
            }
            return stringWriter.getBuffer().toString();
        }

        /**
         * init if needed and get velocity engine
         * @return
         */
        private static VelocityEngine getVelocityEngine() {
            if (engine == null) {
                engine = new VelocityEngine();
                engine.init();
            }
            return engine;
        }
    }

    /**
     * DunningDocument Workflow exception
     */
    public static class DunningDocumentWFException extends Exception {
        /**
         * constructor with excption msg and cause
         * @param message exception msg
         * @param cause exception cause
         */
        DunningDocumentWFException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * constructor with excption msg
         * @param message exception msg
         */
        DunningDocumentWFException(String message) {
            super(message);
        }
    }
}

package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionTypeEnum.SCRIPT;
import static org.meveo.model.payments.ActionTypeEnum.SEND_NOTIFICATION;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.*;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelInstanceService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.util.ApplicationProvider;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;

@Stateless
public class TriggerCollectionPlanLevelsJobBean extends IteratorBasedJobBean<Long> {

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private DunningCollectionPlanService collectionPlanService;

    @Inject
    private DunningCollectionPlanStatusService collectionPlanStatusService;

    @Inject
    private DunningLevelInstanceService levelInstanceService;

    @Inject
    private EmailSender emailSender;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Override
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess,
                this::process, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     *
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of active collection to process
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return of(new SynchronizedIterator<>(collectionPlanService.getActiveCollectionPlansIds()));
    }

    /**
     * Process collection plans
     *
     * @param collectionPlanId   Collection plan id to process
     * @param jobExecutionResult Job execution result
     */
    private void process(Long collectionPlanId, JobExecutionResultImpl jobExecutionResult) {
        DunningCollectionPlan collectionPlan = collectionPlanService.findById(collectionPlanId);
        Date dateToCompare;
        Date today = new Date();
        int index = 0;
        int nextLevel = 0;
        String lastAction = "";
        String nextAction = "";
        boolean updateCollectionPlan = false;
        for (DunningLevelInstance levelInstance : collectionPlan.getDunningLevelInstances()) {
            dateToCompare = DateUtils.addDaysToDate(collectionPlan.getStartDate(),
                    collectionPlan.getPauseDuration() + levelInstance.getDaysOverdue());
            if (levelInstance.getLevelStatus() != DunningLevelInstanceStatusEnum.DONE
                    && !collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)
                    && today.before(dateToCompare)) {
                nextLevel = index + 1;
                int countAutoActions = 0;
                for (int i = 0; i < levelInstance.getActions().size(); i++) {
                    if (levelInstance.getActions().get(i).getActionMode().equals(ActionModeEnum.AUTOMATIC)) {
                        if (levelInstance.getActions().get(i).getActionType().equals(SCRIPT)) {
                            if (levelInstance.getActions().get(i).getDunningAction() != null) {
                                scriptInstanceService.execute(levelInstance.getActions().get(i).getDunningAction().getScriptInstance().getCode(), new HashMap<>());
                            }
                        }
                        if (levelInstance.getActions().get(i).getActionType().equals(SEND_NOTIFICATION)) {
                            if (levelInstance.getActions().get(i).getDunningAction().getActionChannel().equals(EMAIL)
                                    || levelInstance.getActions().get(i).getDunningAction().getActionChannel().equals(LETTER)) {
                                sendReminderEmail(levelInstance.getActions().get(i).getDunningAction().getActionNotificationTemplate(),
                                        collectionPlan.getRelatedInvoice(), collectionPlan.getLastActionDate());
                            }
                        }
                        levelInstance.getActions().get(i).setActionStatus(DunningActionInstanceStatusEnum.DONE);
                        countAutoActions++;
                        lastAction = levelInstance.getActions().get(i).getCode();
                        if (i + 1 < levelInstance.getActions().size()) {
                            nextAction = levelInstance.getActions().get(i + 1).getCode();
                        }
                    }
                }
                levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
                collectionPlan.setLastActionDate(new Date());
                collectionPlan.setLastAction(lastAction);
                collectionPlan.setNextAction(nextAction);
                updateCollectionPlan = true;
                if (nextLevel < collectionPlan.getDunningLevelInstances().size()) {
                    collectionPlan.setCurrentDunningLevelSequence(collectionPlan.getDunningLevelInstances().get(nextLevel).getSequence());
                }
                if (dateToCompare.before(today)) {
                    collectionPlan.setStatus(collectionPlanStatusService.findByStatus(FAILED));
                }
                if (countAutoActions == levelInstance.getActions().size()
                        || collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)) {
                    collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
                }
                if (countAutoActions > 0 && countAutoActions < levelInstance.getActions().size()) {
                    levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
                }
            }
            levelInstanceService.update(levelInstance);
            index++;
        }
        if (updateCollectionPlan) {
            collectionPlanService.update(collectionPlan);
        }

    }

    private void sendReminderEmail(EmailTemplate emailTemplate, Invoice invoice, Date lastActionDate) {
        Map<Object, Object> params = new HashMap<>();
        BillingAccount billingAccount =
                billingAccountService.findById(invoice.getBillingAccount().getId(), asList("customerAccount"));
        params.put("Company.Name", billingAccount.getDescription());
        params.put("Compagny.adress", billingAccount.getAddress().getAddress1());
        params.put("Company.postalcode", billingAccount.getAddress().getZipCode());
        params.put("billingAccount.address.city", billingAccount.getAddress().getCity());
        params.put("Company.phone", billingAccount.getContactInformation().getPhone());

        CustomerAccount customerAccount = customerAccountService.findById(billingAccount.getCustomerAccount().getId());
        params.put("Title.client", ofNullable(customerAccount.getLegalEntityType()).map(Title::getCode).orElse(""));
        params.put("Company.client.adress", customerAccount.getAddress().getAddress1());
        params.put("Company.client.postalcode", customerAccount.getAddress().getZipCode());
        params.put("Company.client.city", customerAccount.getAddress().getCity());
        params.put("Contact.client", customerAccount.getDescription());
        params.put("Company.client.name", customerAccount.getName().getFirstName());

        params.put("invoice.invoiceNumber", invoice.getInvoiceNumber());
        params.put("invoice.dueDate", invoice.getDueDate());
        params.put("invoice.total", invoice.getAmountWithTax());
        params.put("day.date", new Date());
        params.put("Last.action.date", lastActionDate);

        emailTemplate = emailTemplateService.findById(emailTemplate.getId());
        String subject = evaluateExpression(emailTemplate.getSubject(), params, String.class);
        String content = evaluateExpression(emailTemplate.getTextContent(), params, String.class);
        String contentHtml = evaluateExpression(emailTemplate.getHtmlContent(), params, String.class);
        emailSender.send(appProvider.getEmail(), asList(appProvider.getEmail()),
                asList(billingAccount.getContactInformation().getEmail()), null, null,
                subject, content, contentHtml, null, null, false);
    }
}
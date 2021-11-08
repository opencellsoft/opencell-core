package org.meveo.apiv2.dunning.service;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.meveo.model.dunning.DunningInvoiceStatusContextEnum.ACTIVE_DUNNING;
import static org.meveo.model.dunning.DunningInvoiceStatusContextEnum.FAILED_DUNNING;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.hibernate.Hibernate;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningInvoiceStatusService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;

public class DunningPolicyApiService implements ApiService<DunningPolicy> {

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Inject
    private DunningLevelService dunningLevelService;

    @Inject
    private DunningInvoiceStatusService invoiceDunningStatusesService;

    @Inject
    private DunningCollectionPlanStatusService collectionPlanStatusService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    private final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private List<String> fetchFields = asList("minBalanceTriggerCurrency");

    @Override
    public List<DunningPolicy> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<DunningPolicy> findById(Long id) {
        DunningPolicy dunningPolicy = dunningPolicyService.findById(id, fetchFields);
        if(dunningPolicy == null) {
            throw new NotFoundException("Dunning policy with id  " + id + " does not exits");
        }
        return of(dunningPolicyService.findById(id, fetchFields));
    }

    @Override
    public DunningPolicy create(DunningPolicy dunningPolicy) {
        dunningPolicyService.create(dunningPolicy);
        trackOperation("create", new Date(), null, dunningPolicy.getPolicyName());
        return findByCode(dunningPolicy.getPolicyName()).get();
    }

    @Override
    public Optional<DunningPolicy> update(Long id, DunningPolicy dunningPolicy) {
        int countReminderLevels = 0;
        int countEndOfDunningLevel = 0;
        int totalDunningLevels = 0;
        int highestSequence = (Hibernate.isInitialized(dunningPolicy.getDunningLevels())
                && dunningPolicy.getDunningLevels() != null && !dunningPolicy.getDunningLevels().isEmpty())
                ? dunningPolicy.getDunningLevels().get(0).getSequence() : 0;
        if (Hibernate.isInitialized(dunningPolicy.getDunningLevels()) && dunningPolicy.getDunningLevels() != null) {
            for (DunningPolicyLevel policyLevel : dunningPolicy.getDunningLevels()) {
                refreshPolicyLevel(policyLevel);
                if (policyLevel.getDunningLevel().isReminder()) {
                    countReminderLevels++;
                } else {
                    totalDunningLevels++;
                }
                if (policyLevel.getDunningLevel().isEndOfDunningLevel()) {
                    if (!policyLevel.getCollectionPlanStatus().getContext().equals("Failed Dunning")
                            && !policyLevel.getInvoiceDunningStatuses().getContext().equals(FAILED_DUNNING)) {
                        throw new BadRequestException("Dunning level creation fails");
                    }
                    if (policyLevel.getSequence() < highestSequence) {
                        throw new BadRequestException("End of dunning level sequence must be high");
                    }
                    highestSequence = policyLevel.getSequence();
                    countEndOfDunningLevel++;
                }
                validateActiveDunning(policyLevel);
            }
            if (countReminderLevels == 0) {
                throw new BadRequestException("can not remove reminder level");
            }
            validateLevelsNumber(countReminderLevels, countEndOfDunningLevel, totalDunningLevels);
            dunningPolicy.setTotalDunningLevels(totalDunningLevels);
        }
        DunningPolicy updatedDunningPolicy = dunningPolicyService.update(dunningPolicy);
        return of(updatedDunningPolicy);
    }

    public DunningPolicyLevel refreshPolicyLevel(DunningPolicyLevel policyLevel) {
        DunningLevel dunningLevel = dunningLevelService.refreshOrRetrieve(policyLevel.getDunningLevel());
        DunningInvoiceStatus invoiceDunningStatuses =
                invoiceDunningStatusesService.refreshOrRetrieve(policyLevel.getInvoiceDunningStatuses());
        DunningCollectionPlanStatus collectionPlanStatus =
                collectionPlanStatusService.refreshOrRetrieve(policyLevel.getCollectionPlanStatus());
        if (dunningLevel == null) {
            throw new BadRequestException("Policy level creation fails dunning level does not exists");
        }
        if (invoiceDunningStatuses == null) {
            throw new BadRequestException("Policy level creation fails invoice dunning statuses does not exists");
        }
        if (collectionPlanStatus == null) {
            throw new BadRequestException("Policy level creation fails collection plan status does not exists");
        }
        policyLevel.setDunningLevel(dunningLevel);
        policyLevel.setInvoiceDunningStatuses(invoiceDunningStatuses);
        policyLevel.setCollectionPlanStatus(collectionPlanStatus);
        return policyLevel;
    }

    public void validateActiveDunning(DunningPolicyLevel policyLevel) {
        if (!policyLevel.getDunningLevel().isEndOfDunningLevel() && !policyLevel.getDunningLevel().isReminder()) {
            if (!policyLevel.getCollectionPlanStatus().getContext().equals("Active")
                    && !policyLevel.getInvoiceDunningStatuses().getContext().equals(ACTIVE_DUNNING)) {
                throw new BadRequestException("Dunning level creation fails");
            }
        }
    }

    public void validateLevelsNumber(int countReminderLevels, int countEndOfDunningLevel, int totalDunningLevels) {
        if (countReminderLevels > 1) {
            throw new BadRequestException("There is already a Reminder level for this policy, remove the existing level to select a new one.");
        }
        if (countEndOfDunningLevel > 1) {
            throw new BadRequestException("A policy can have only 1 level with isEndOfDunningLevel = TRUE");
        }
        if (totalDunningLevels == 0) {
            throw new BadRequestException("Policy should have at least one dunning level other the reminder level");
        }
    }

    @Override
    public Optional<DunningPolicy> patch(Long id, DunningPolicy dunningPolicy) {
        return empty();
    }

    @Override
    public Optional<DunningPolicy> delete(Long id) {
        DunningPolicy dunningPolicy = dunningPolicyService.findById(id);
        if(dunningPolicy != null) {
            dunningPolicyService.remove(id);
            trackOperation("delete", new Date(), null, dunningPolicy.getPolicyName());
            return of(dunningPolicy);
        } else {
            return empty();
        }
    }

    @Override
    public Optional<DunningPolicy> findByCode(String code) {
        try {
            DunningPolicy dunningPolicy = dunningPolicyService.findByName(code);
            return of(dunningPolicy);
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    public Optional<DunningPolicy> findByName(String policyName) {
        try {
            DunningPolicy dunningPolicy = dunningPolicyService.findByName(policyName);
            dunningPolicy.setDunningLevels(dunningPolicyLevelService.findByPolicyID(dunningPolicy.getId()));
            return of(dunningPolicy);
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    public Optional<DunningPolicy> updateTotalLevels(DunningPolicy dunningPolicy) {
        return of(dunningPolicyService.update(dunningPolicy));
    }

    public AuditLog trackOperation(String operationType, Date operationDate, String updatedField, String dunningPolicyCode) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntity(DunningPolicy.class.getSimpleName());
        auditLog.setCreated(operationDate);
        auditLog.setActor(currentUser.getUserName());
        auditLog.setAction(operationType);
        StringBuilder parameters = new StringBuilder();
        parameters.append("user ")
                .append(currentUser.getUserName())
                .append(" apply ")
                .append(operationType)
                .append(" on ")
                .append(formatter.format(operationDate))
                .append(" to the dunning policy ")
                .append(dunningPolicyCode);
        auditLog.setParameters(parameters.toString());
        auditLog.setOrigin(dunningPolicyCode);
        auditLogService.create(auditLog);
        return auditLog;
    }

    public Optional<DunningPolicy> archiveDunningPolicy(DunningPolicy dunningPolicy) {
        dunningPolicy.setActivePolicy(FALSE);
        trackOperation("archive", new Date(), "isActive", dunningPolicy.getPolicyName());
        return of(dunningPolicyService.update(dunningPolicy));
    }
}
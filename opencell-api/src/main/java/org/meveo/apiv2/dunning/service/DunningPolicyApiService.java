package org.meveo.apiv2.dunning.service;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.*;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.dunning.impl.DunningPolicyRuleLineMapper;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.admin.Currency;
import org.meveo.model.dunning.*;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.*;

public class DunningPolicyApiService implements ApiService<DunningPolicy> {

    @Inject
    private GlobalSettingsVerifier globalSettingsVerifier;

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Inject
    private DunningLevelService dunningLevelService;

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    private DunningPolicyRuleService dunningPolicyRuleService;

    @Inject
    private DunningPolicyRuleLineService dunningPolicyRuleLineService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    protected ResourceBundle resourceMessages;

    @Inject
    DunningSettingsService dunningSettingsService;
    
    private List<String> fetchFields = asList("minBalanceTriggerCurrency");

    private DunningPolicyRuleLineMapper policyRuleLineMapper = new DunningPolicyRuleLineMapper();

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
        globalSettingsVerifier.checkActivateDunning();
        try {
            if(dunningPolicy.getMinBalanceTriggerCurrency() != null) {
                Currency currency = currencyService.findByCode(dunningPolicy.getMinBalanceTriggerCurrency().getCurrencyCode());
                if(currency == null) {
                    throw new NotFoundException("Currency with code " + dunningPolicy.getMinBalanceTriggerCurrency().getCurrencyCode() + " not found");
                }
                dunningPolicy.setMinBalanceTriggerCurrency(currencyService.findByCode(dunningPolicy.getMinBalanceTriggerCurrency().getCurrencyCode()));
            }
            if(dunningPolicy.getPolicyPriority() != null && dunningPolicyService.checkIfSamePriorityExists(dunningPolicy.getPolicyPriority())) {
                throw new BusinessApiException("Policy with priority " + dunningPolicy.getPolicyPriority() + " already exists");
            }
            DunningSettings dunningSettings = dunningSettingsService.findLastOne();

            if(dunningSettings != null) {
                dunningPolicy.setType(dunningSettings.getDunningMode());
            }
            dunningPolicyService.create(dunningPolicy);
            auditLogService.trackOperation("create", new Date(), dunningPolicy, dunningPolicy.getPolicyName());
            return findByCode(dunningPolicy.getPolicyName()).get();
        } catch (Exception exception) {
            checkNameConstraint(exception);
            throw new BusinessException(exception.getMessage());
        }
    }

    @Override
    public Optional<DunningPolicy> update(Long id, DunningPolicy dunningPolicy) {
        globalSettingsVerifier.checkActivateDunning();
        try {
            int countReminderLevels = 0;
            int countEndOfDunningLevel = 0;
            int totalDunningLevels = 0;
            List<DunningPolicyLevel> dunningPolicyLevels = new ArrayList<>();
            int endOfLevelDayOverDue = -1;
            if (Hibernate.isInitialized(dunningPolicy.getDunningLevels()) && dunningPolicy.getDunningLevels() != null) {
                for (DunningPolicyLevel policyLevel : dunningPolicy.getDunningLevels()) {
                    refreshPolicyLevel(policyLevel);
                    if (policyLevel.getDunningLevel().isReminder()) {
                        countReminderLevels++;
                    }
                    totalDunningLevels++;
                    if (policyLevel.getDunningLevel().isEndOfDunningLevel()) {
                        endOfLevelDayOverDue = policyLevel.getDunningLevel().getDaysOverdue();
                        countEndOfDunningLevel++;
                    }
                    dunningPolicyLevels.add(policyLevel);
                }
                
                validateLevelsNumber(countReminderLevels, countEndOfDunningLevel, totalDunningLevels);
                dunningPolicy.setTotalDunningLevels(totalDunningLevels);
                validateLevels(dunningPolicyLevels, endOfLevelDayOverDue);
            }
            dunningPolicyService.updatePolicyWithLevel(dunningPolicy, dunningPolicyLevels);
            return of(dunningPolicy);
        } catch (Exception exception) {
            checkNameConstraint(exception);
            throw new BusinessException(exception.getMessage());
        }
    }

    private void checkNameConstraint(Exception exception) {
        Throwable throwable = exception.getCause();
        while (throwable != null) {
            if (throwable instanceof ConstraintViolationException) {
                throw new BusinessException("Dunning policy name is already exists");
            }
            throwable = throwable.getCause();
        }
    }

    public DunningPolicyLevel refreshPolicyLevel(DunningPolicyLevel policyLevel) {
        DunningLevel dunningLevel = dunningLevelService.refreshOrRetrieve(policyLevel.getDunningLevel());
        if (dunningLevel == null) {
            throw new BadRequestException("Policy level creation fails : dunning level does not exists");
        }
        policyLevel.setDunningLevel(dunningLevel);
        return policyLevel;
    }

    public void validateLevelsNumber(int countReminderLevels, int countEndOfDunningLevel, int totalDunningLevels) {
        if (countReminderLevels > 1) {
            throw new BadRequestException("There is already a reminder level for this policy, remove the existing level to select a new one");
        }
        if (countEndOfDunningLevel > 1) {
            throw new BadRequestException("There is already an end of dunning level for this policy");
        }
        if (totalDunningLevels  < 1) {
            throw new BadRequestException("Dunning policy should have at least one dunning level other the reminder level");
        }
        if (countEndOfDunningLevel == 0) {
            throw new BadRequestException("Dunning policy should have an end of dunning level");
        }
    }

    public void validateLevels(List<DunningPolicyLevel> dunningPolicyLevels, int endOfLevelDayOverDue) {
        for (DunningPolicyLevel policyLevel : dunningPolicyLevels) {
            if(!policyLevel.getDunningLevel().isEndOfDunningLevel()
                    && policyLevel.getDunningLevel().getDaysOverdue() > endOfLevelDayOverDue) {
                throw new BadRequestException("End of dunning level must have the higher days overdue");
            }
        }
    }

    @Override
    public Optional<DunningPolicy> patch(Long id, DunningPolicy dunningPolicy) {
        return empty();
    }

    @Override
    public Optional<DunningPolicy> delete(Long id) {
        globalSettingsVerifier.checkActivateDunning();
        DunningPolicy dunningPolicy = dunningPolicyService.findById(id);
        if(dunningPolicy != null) {
        	try {
                dunningPolicyService.remove(id);
                auditLogService.trackOperation("delete", new Date(), dunningPolicy, dunningPolicy.getPolicyName());
                return of(dunningPolicy);
        	} catch (Exception exception) {
                Throwable throwable = exception.getCause();
                while (throwable != null) {
                    if (throwable instanceof ConstraintViolationException) {
                        throw new BusinessException("The dunning policy with id "+ id + " is referenced");
                    }
                    throwable = throwable.getCause();
                }
                throw new BusinessException(exception.getMessage());
            }
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

    public Optional<DunningPolicy> archiveDunningPolicy(DunningPolicy dunningPolicy) {
        globalSettingsVerifier.checkActivateDunning();
        dunningPolicy.setIsActivePolicy(FALSE);
        dunningPolicy.setDisabled(true);
        auditLogService.trackOperation("archive", new Date(), dunningPolicy, dunningPolicy.getPolicyName(), Arrays.asList("disabled"));
        return of(dunningPolicyService.update(dunningPolicy));
    }

    public Optional<DunningPolicyRule> removePolicyRule(Long id) {
        globalSettingsVerifier.checkActivateDunning();
        DunningPolicyRule dunningPolicyRule = dunningPolicyRuleService.findById(id);
        if(dunningPolicyRule == null) {
            return empty();
        }
        try {
            dunningPolicyRuleService.remove(dunningPolicyRule);
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
        return of(dunningPolicyRule);
    }

    public Optional<Long> removePolicyRuleWithPolicyId(Long policyId) {
        try {
            List<DunningPolicyRule> dunningPolicyRules = dunningPolicyRuleService.findByDunningPolicy(policyId);
            for (DunningPolicyRule policyRule : dunningPolicyRules) {
                dunningPolicyRuleService.remove(policyRule);
            }
            return of(policyId);
        } catch (Exception exception) {
            throw new BusinessException(exception.getMessage());
        }
    }

    public Optional<DunningPolicyRule> addPolicyRule(DunningPolicyRule dunningPolicyRule,
                                                     List<org.meveo.apiv2.dunning.DunningPolicyRuleLine> policyRuleLines) {
        globalSettingsVerifier.checkActivateDunning();
        dunningPolicyRuleService.create(dunningPolicyRule);
        for (org.meveo.apiv2.dunning.DunningPolicyRuleLine line : policyRuleLines) {
            org.meveo.model.dunning.DunningPolicyRuleLine dunningPolicyRuleLine =
                    policyRuleLineMapper.toEntity(line);
            dunningPolicyRuleLine.setDunningPolicyRule(dunningPolicyRule);
            dunningPolicyRuleLineService.create(dunningPolicyRuleLine);
        }
        return of(dunningPolicyRule);
    }
    
    public List<DunningPolicyRule> getPolicyRuleWithPolicyId(Long policyId) {
        return dunningPolicyRuleService.findByDunningPolicy(policyId);
    }
    
    public List<DunningPolicyRuleLine> getPolicyRuleLineWithPolicyId(Long policyId) {
        return dunningPolicyRuleLineService.findByDunningPolicy(policyId);
    }
    
    public List<DunningPolicyRuleLine> getPolicyRuleLineWithDunningPolicyRuled(Long dunningPolicyRule) {
        return dunningPolicyRuleLineService.findByDunningPolicyRule(dunningPolicyRule);
    }
}
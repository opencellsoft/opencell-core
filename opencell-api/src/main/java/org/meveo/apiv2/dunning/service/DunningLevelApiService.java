package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.collection.spi.PersistentCollection;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.dunning.*;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningActionService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class DunningLevelApiService implements ApiService<DunningLevel> {

    @Inject
    private GlobalSettingsVerifier globalSettingsVerifier;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private DunningLevelService dunningLevelService;

    @Inject
    private DunningActionService dunningActionService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    private DunningSettingsService dunningSettingsService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Override
    public Optional<DunningLevel> findById(Long id) {
        return Optional.ofNullable(dunningLevelService.findById(id, Arrays.asList("minBalanceCurrency", "chargeCurrency", "dunningActions")));
    }

    @Override
    public Optional<DunningLevel> findByCode(String code) {
        DunningLevel dunningLevel = dunningLevelService.findByCode(code, Arrays.asList("minBalanceCurrency", "chargeCurrency", "dunningActions"));
        if (dunningLevel == null) {
            throw new EntityDoesNotExistsException(DunningLevel.class, code);
        }
        return Optional.of(dunningLevel);
    }

    @Override
    public Optional<DunningLevel> delete(Long id) {
        globalSettingsVerifier.checkActivateDunning();
        DunningLevel dunningLevel = findById(id).orElseThrow(() -> new EntityDoesNotExistsException(DunningLevel.class, id));
        dunningLevelService.remove(dunningLevel);
        String origine = (dunningLevel!=null) ? dunningLevel.getCode() : "";
        auditLogService.trackOperation("DELETE", new Date(), dunningLevel, origine);
        return Optional.ofNullable(dunningLevel);
    }

    @Override
    public DunningLevel create(DunningLevel newDunningLevel) {
        globalSettingsVerifier.checkActivateDunning();
        if (dunningLevelService.findByCode(newDunningLevel.getCode()) != null) {
            throw new EntityAlreadyExistsException(DunningLevel.class, newDunningLevel.getCode());
        }

        DunningSettings dunningSettings = dunningSettingsService.findLastOne();

        if(dunningSettings != null) {
            newDunningLevel.setType(dunningSettings.getDunningMode());
        }

        newDunningLevel.setSoftDecline(Boolean.FALSE);

        if(newDunningLevel.getType().equals(DunningModeEnum.CUSTOMER_LEVEL)) {
            newDunningLevel.setMinBalanceCurrency(null);
        }


        setDefaultValues(newDunningLevel);
        validateParameters(newDunningLevel);
        dunningLevelService.create(newDunningLevel);        
        auditLogService.trackOperation("CREATE", new Date(), newDunningLevel, newDunningLevel.getCode());
        return newDunningLevel;
    }

    @Override
    public Optional<DunningLevel> update(Long id, DunningLevel dunningLevel) {
        globalSettingsVerifier.checkActivateDunning();
        DunningLevel dunningLevelToUpdate = findById(id).orElseThrow(() -> new EntityDoesNotExistsException(DunningLevel.class, id));

        List<String> updatedFields = new ArrayList<>();

        if (StringUtils.isNotBlank(dunningLevel.getCode()) && !dunningLevel.getCode().equals(dunningLevelToUpdate.getCode())) {
            if (dunningLevelService.findByCode(dunningLevel.getCode()) != null) {
                throw new EntityAlreadyExistsException(DunningLevel.class, dunningLevel.getCode());
            }
            updatedFields.add("Code");
            dunningLevelToUpdate.setCode(dunningLevel.getCode());
        }
        if (dunningLevel.getDescription() != null) {
            if (!dunningLevel.getDescription().equals(dunningLevelToUpdate.getDescription())) {
                updatedFields.add("Description");
            }
            dunningLevelToUpdate.setDescription(dunningLevel.getDescription());
        }
        if (dunningLevel.isReminder() != null) {
            if (!dunningLevel.isReminder().equals(dunningLevelToUpdate.isReminder())) {
                updatedFields.add("Reminder");
            }
            dunningLevelToUpdate.setReminder(dunningLevel.isReminder());
        }
        if (dunningLevel.isActive() != null) {
            if (!dunningLevel.isActive().equals(dunningLevelToUpdate.isActive())) {
                auditLogService.trackOperation("CHANGE_STATUS", new Date(), dunningLevelToUpdate, dunningLevelToUpdate.getCode());
            }
            dunningLevelToUpdate.setActive(dunningLevel.isActive());
        }
        if (dunningLevel.getDaysOverdue() != null) {
            if (dunningLevelToUpdate.getDaysOverdue() == null || dunningLevel.getDaysOverdue() != dunningLevelToUpdate.getDaysOverdue()) {
                updatedFields.add("DaysOverdue");
            }
            dunningLevelToUpdate.setDaysOverdue(dunningLevel.getDaysOverdue());
        }
        if (dunningLevel.isSoftDecline() != null) {
            if (!dunningLevel.isSoftDecline().equals(dunningLevelToUpdate.isSoftDecline())) {
                updatedFields.add("SoftDecline");
            }
            dunningLevelToUpdate.setSoftDecline(dunningLevel.isSoftDecline());
        }
        if (dunningLevel.getMinBalance() != null) {
            if (dunningLevelToUpdate.getMinBalance() == null || dunningLevel.getMinBalance().compareTo(dunningLevelToUpdate.getMinBalance()) != 0) {
                updatedFields.add("MinBalance");
            }
            dunningLevelToUpdate.setMinBalance(dunningLevel.getMinBalance());
        }
        if (dunningLevel.getMinBalanceCurrency() != null) {
            if (!dunningLevel.getMinBalanceCurrency().equals(dunningLevelToUpdate.getMinBalanceCurrency())) {
                updatedFields.add("MinBalanceCurrency");
            }
            dunningLevelToUpdate.setMinBalanceCurrency(dunningLevel.getMinBalanceCurrency());
        }
        if (dunningLevel.getChargeType() != null) {
            if (dunningLevelToUpdate.getChargeType() == null || dunningLevel.getChargeType() != dunningLevelToUpdate.getChargeType()) {
                updatedFields.add("ChargeType");
            }
            dunningLevelToUpdate.setChargeType(dunningLevel.getChargeType());
        }
        if (dunningLevel.getChargeValue() != null) {
            if (dunningLevelToUpdate.getChargeValue() == null || dunningLevel.getChargeValue().compareTo(dunningLevelToUpdate.getChargeValue()) != 0) {
                updatedFields.add("ChargeValue");
            }
            dunningLevelToUpdate.setChargeValue(dunningLevel.getChargeValue());
        }
        if (dunningLevel.getChargeCurrency() != null) {
            if (!dunningLevel.getChargeCurrency().equals(dunningLevelToUpdate.getChargeCurrency())) {
                updatedFields.add("ChargeCurrency");
            }
            dunningLevelToUpdate.setChargeCurrency(dunningLevel.getChargeCurrency());
        }
        if (dunningLevel.isEndOfDunningLevel() != null) {
            if (!dunningLevel.isEndOfDunningLevel().equals(dunningLevelToUpdate.isEndOfDunningLevel())) {
                updatedFields.add("EndOfDunningLevel");
            }
            dunningLevelToUpdate.setEndOfDunningLevel(dunningLevel.isEndOfDunningLevel());
        }
        if (dunningLevel.getDunningActions() != null) {
            if (!dunningLevel.getDunningActions().equals(dunningLevelToUpdate.getDunningActions())) {
                updatedFields.add("DunningActions");
            }
            dunningLevelToUpdate.setDunningActions(dunningLevel.getDunningActions());
        }

        validateParameters(dunningLevelToUpdate);
        dunningLevelService.update(dunningLevelToUpdate);
        if (!updatedFields.isEmpty()) {
            String origine = (dunningLevelToUpdate!=null) ? dunningLevelToUpdate.getCode() : "";
            auditLogService.trackOperation("UPDATE", new Date(), dunningLevelToUpdate, origine, updatedFields);
        }
        return Optional.of(dunningLevelToUpdate);
    }

    private void validateParameters(DunningLevel baseEntity) {

        if (StringUtils.isBlank(baseEntity.getCode())) {
            throw new MissingParameterException("code");
        }
        if (baseEntity.getDaysOverdue() == null) {
            throw new MissingParameterException("dunningLevelDaysOverdue");
        }
        if (baseEntity.getMinBalanceCurrency() != null) {
            Currency currency = null;
            Long currencyId = baseEntity.getMinBalanceCurrency().getId();
            if (currencyId != null) {
                currency = findCurrencyByIdOrByCode(currencyId, null).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyId));
            } else {
                String currencyCode = baseEntity.getMinBalanceCurrency() == null ? null : baseEntity.getMinBalanceCurrency().getCurrencyCode();
                currency = findCurrencyByIdOrByCode(null, currencyCode).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyCode));
            }
            baseEntity.setMinBalanceCurrency(currency);
        }

        if (baseEntity.getChargeCurrency() != null) {
            Currency currency = null;
            Long currencyId = baseEntity.getChargeCurrency().getId();
            if (currencyId != null) {
                currency = findCurrencyByIdOrByCode(currencyId, null).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyId));
            } else {
                String currencyCode = baseEntity.getChargeCurrency() == null ? null : baseEntity.getChargeCurrency().getCurrencyCode();
                currency = findCurrencyByIdOrByCode(null, currencyCode).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyCode));
            }
            baseEntity.setChargeCurrency(currency);
        }

        String chargeCurrencyCode = baseEntity.getChargeCurrency() == null ? null : baseEntity.getChargeCurrency().getCurrencyCode();
        if (chargeCurrencyCode != null) {
            Currency chargeCurrency = currencyService.findByCode(chargeCurrencyCode);
            if (chargeCurrency == null) {
                throw new EntityDoesNotExistsException(Currency.class, chargeCurrencyCode);
            }
            baseEntity.setChargeCurrency(chargeCurrency);
        }

        if (baseEntity.getChargeValue() != null) {
            if (baseEntity.getChargeType() == DunningLevelChargeTypeEnum.PERCENTAGE && HUNDRED.compareTo(baseEntity.getChargeValue()) < 0) {
                throw new InvalidParameterException("dunningLevelChargeValue should be less than or equal to 100");
            }
        }

        if(baseEntity.isEndOfDunningLevel() && baseEntity.isReminder()) {
            throw new InvalidParameterException("Dunning level should not be reminder and end of level at the same time");
        }

        if (baseEntity.getDaysOverdue() != null) {
            if (baseEntity.isReminder() && baseEntity.getDaysOverdue() >= 0) {
                throw new InvalidParameterException("Reminder Dunning level days overdue should be negative");
            }
            if (!baseEntity.isReminder() && baseEntity.getDaysOverdue() < 0) {
                throw new InvalidParameterException("Negative days overdue allowed only for reminder levels");
            }
        }
        if (baseEntity.getDunningActions() != null && !(baseEntity.getDunningActions() instanceof PersistentCollection)) {
            Optional<DunningAction> unfoundAction = baseEntity.getDunningActions().stream().filter(action -> dunningActionService.findByCode(action.getCode()) == null).findFirst();
            if (unfoundAction.isPresent()) {
                throw new EntityDoesNotExistsException(DunningAction.class, unfoundAction.get().getCode());
            }
            baseEntity.setDunningActions(baseEntity.getDunningActions().stream().map(action -> dunningActionService.findByCode(action.getCode())).collect(Collectors.toList()));
        }
    }

    private void setDefaultValues(DunningLevel newDunningLevel) {
        if (newDunningLevel.isActive() == null) {
            newDunningLevel.setActive(Boolean.FALSE);
        }
        if (newDunningLevel.isReminder() == null) {
            newDunningLevel.setReminder(Boolean.FALSE);
        }
        if (newDunningLevel.isSoftDecline() == null) {
            newDunningLevel.setSoftDecline(Boolean.FALSE);
        }
        if (newDunningLevel.isEndOfDunningLevel() == null) {
            newDunningLevel.setEndOfDunningLevel(Boolean.FALSE);
        }
    }

    private Optional<Currency> findCurrencyByIdOrByCode(Long id, String code) {
        if (id != null) {
            return Optional.ofNullable(currencyService.findById(id));
        }
        return Optional.ofNullable(currencyService.findByCode(code));
    }

    @Override
    public Optional<DunningLevel> patch(Long id, DunningLevel baseEntity) {
        return empty();
    }

    @Override
    public List<DunningLevel> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return new ArrayList<DunningLevel>();
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }
}

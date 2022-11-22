package org.meveo.service.accounting.impl;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.accounting.AccountingOperationAction;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodForceEnum;
import org.meveo.model.accounting.AccountingPeriodStatusEnum;
import org.meveo.model.accounting.CustomLockOption;
import org.meveo.model.accounting.RegularUserLockOption;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.ProviderService;

@Stateless
public class AccountingPeriodService extends PersistenceService<AccountingPeriod> {
	
    @Inject
    private SubAccountingPeriodService subAccountingPeriodService;

	@Inject
	private ProviderService providerService;

	public AccountingPeriod create(AccountingPeriod entity, Boolean isUseSubAccountingPeriods) {
		return createAccountingPeriod(entity, isUseSubAccountingPeriods);
	}

	public AccountingPeriod update(AccountingPeriod entity, AccountingPeriod newValue) {
		return updateAccountingPeriod(entity, newValue);
	}
	
	public AccountingPeriod createAccountingPeriod(AccountingPeriod entity, Boolean isUseSubAccountingPeriods) {
		validateInputs(entity, isUseSubAccountingPeriods, entity.getSubAccountingPeriodType());
		if(entity.getAccountingPeriodYear() == null) {
			entity.setAccountingPeriodYear(getAccountingPeriodYear(entity.getStartDate(), entity.getEndDate()));
		}
		if(entity.getStartDate() == null) {
			entity.setStartDate(new Date());
		}
		create(entity);
		generateSubAccountingPeriods(entity);

		// Init MatchingCode sequence in Provider
		// providerService.resetMatchingCode();

		return entity;
	}

	public AccountingPeriod updateAccountingPeriod(AccountingPeriod entity, AccountingPeriod newValue) {
	    if (newValue.getStartDate() != null && entity.getStartDate().compareTo(newValue.getStartDate() ) != 0) {
            throw new ValidationException("Once the start date is set, it CANNOT be modified");
        }
	    if (newValue.getEndDate()  != null && entity.getEndDate().compareTo(newValue.getEndDate() ) != 0) {
			throw new ValidationException("Once the end date is set, it CANNOT be modified");
		}
		if (entity.isUseSubAccountingCycles() && Boolean.FALSE.equals(newValue.isUseSubAccountingCycles())) {
			throw new ValidationException("Use sub-accounting cycles CANNOT be modified");
		} else
			Optional.ofNullable(newValue.isUseSubAccountingCycles()).ifPresent(b -> entity.setUseSubAccountingCycles(Boolean.TRUE.equals(newValue.isUseSubAccountingCycles())));


		if (entity.getSubAccountingPeriodType() != null  && !entity.getSubAccountingPeriodType().equals(newValue.getSubAccountingPeriodType()) && isUsedOnAccountingOperations(entity)) {
			throw new ValidationException("sub-accounting cycles type CANNOT be modified because the sub dates is used in the account operations");
		} else
			Optional.ofNullable(newValue.getSubAccountingPeriodType()).ifPresent(subAP -> entity.setSubAccountingPeriodType(newValue.getSubAccountingPeriodType()));

	entity.setAccountingOperationAction(newValue.getAccountingOperationAction());
	entity.setCustomLockNumberDays(newValue.getCustomLockNumberDays());
	entity.setCustomLockOption(newValue.getCustomLockOption());
	entity.setForceCustomDay(newValue.getForceCustomDay());
	entity.setForceOption(newValue.getForceOption());
	entity.setRegularUserLockOption(newValue.getRegularUserLockOption());
	entity.setSubAccountingPeriodType(newValue.getSubAccountingPeriodType());


	validateInputs(entity, newValue.isUseSubAccountingCycles(), newValue.getSubAccountingPeriodType());
		update(entity);
		if (entity.getSubAccountingPeriodType() != null) {
			subAccountingPeriodService.updateSubAccountingPeriods(entity, entity.getSubAccountingPeriodType());
		}
		return entity;
	}

	private void generateSubAccountingPeriods(AccountingPeriod entity) {
		if (entity.isUseSubAccountingCycles())
			subAccountingPeriodService.createSubAccountingPeriods(entity, entity.getSubAccountingPeriodType());
	}

	private void validateEndDate(Date endDate) {
		if (endDate == null) {
			throw new ValidationException("endDate is mandatory to create AccountingPeriod");
		}
		if (endDate.before(new Date())) {
			throw new ValidationException("the given endDate " + DateUtils.formatAsDate(endDate) + " is incorrect , the endDate must be greater than today");
		}
	}
    
	public AccountingPeriod findByAccountingPeriodYear(String fiscalYear) {
		try {
			return (AccountingPeriod) getEntityManager().createNamedQuery("AccountingPeriod.findByFiscalYear")
					.setParameter("fiscalYear", fiscalYear).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} of AccountingPeriodYear {} found", getEntityClass().getSimpleName(), fiscalYear);
			return null;
		}
	}
    
    public AccountingPeriod findLastAccountingPeriod() {
    	try {
			return (AccountingPeriod) getEntityManager().createNamedQuery("AccountingPeriod.findLastAP").getSingleResult();
		} catch (NoResultException e) {
			log.debug("No OPEN AccountingPeriodYear found");
			return null;
		}
    }

	/**
	 * @return
	 */
	public AccountingPeriod generateNextAP() {
		final AccountingPeriod openAccountingPeriod = findLastAccountingPeriod();
		if(openAccountingPeriod == null) {
			throw new BusinessException("No accounting period found");
		}
		AccountingPeriod nextAP = new AccountingPeriod();

		final Date endDate = Date.from(openAccountingPeriod.getEndDate().toInstant().atZone(ZoneId.systemDefault()).plusYears(1).toInstant());
		nextAP.setEndDate(endDate);
		
		final Date startDate = Date.from(openAccountingPeriod.getStartDate().toInstant().atZone(ZoneId.systemDefault()).plusNanos(1).toInstant());
		
		nextAP.setAccountingPeriodYear(getAccountingPeriodYear(startDate,endDate));
		nextAP.setUseSubAccountingCycles(openAccountingPeriod.isUseSubAccountingCycles());
		nextAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);

		Optional.ofNullable(openAccountingPeriod.getCustomLockNumberDays()).ifPresent(nextAP::setCustomLockNumberDays);
		Optional.ofNullable(openAccountingPeriod.getCustomLockOption())
				.ifPresent(s -> nextAP.setCustomLockOption(openAccountingPeriod.getCustomLockOption()));
		Optional.ofNullable(openAccountingPeriod.getSubAccountingPeriodType())
				.ifPresent(s -> nextAP.setSubAccountingPeriodType(openAccountingPeriod.getSubAccountingPeriodType()));
		Optional.ofNullable(openAccountingPeriod.getAccountingOperationAction()).ifPresent(
				s -> nextAP.setAccountingOperationAction(openAccountingPeriod.getAccountingOperationAction()));
		Optional.ofNullable(openAccountingPeriod.getRegularUserLockOption())
				.ifPresent(s -> nextAP.setRegularUserLockOption(openAccountingPeriod.getRegularUserLockOption()));
		Optional.ofNullable(openAccountingPeriod.getForceCustomDay()).ifPresent(nextAP::setForceCustomDay);
		Optional.ofNullable(openAccountingPeriod.getForceOption()).ifPresent(nextAP::setForceOption);

		return createAccountingPeriod(nextAP, nextAP.isUseSubAccountingCycles());
	}

	/**
	 * @param startDate a date
	 * @param endDate a date
	 * @return a fiscal year. Ex. 2021-2022
	 */
	private String getAccountingPeriodYear(Date startDate, Date endDate) {
		if (startDate == null) {
			AccountingPeriod lastAccountingPeriod = findLastAccountingPeriod();
			startDate = Optional.ofNullable(lastAccountingPeriod).map(AccountingPeriod::getEndDate).orElse(null);
			if (startDate != null &&
					(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
							|| startDate.after(endDate))) {
				throw new ValidationException("the given end date " + DateUtils.formatAsDate(endDate) + " already exists");
			}
		}
		startDate = startDate != null ? startDate : new Date();
		final int startYear = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		final int endYear = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		return (startYear == endYear) ? "" + endYear : "" + startYear + "-" + endYear;
	}

	private void validateCustLockNumDaysAndCustLockOpt(Integer customLockNumberDays, CustomLockOption customLockOption) {
		if (customLockNumberDays == null || customLockOption == null)
			throw new BusinessApiException("When regularUserLockOption option is set to CUSTOM then the customLockNumberDays and the customLockOption must not be null");
		if (customLockNumberDays < 1 || customLockNumberDays > 31)
			throw new BusinessApiException("When regularUserLockOption option is set to CUSTOM then the customLockNumberDays must be from 1 (included) to 31 (included).");
	}

	private void validateForceCustomDay(Integer forceCustomDay) {
		if (forceCustomDay < 1 || forceCustomDay > 31)
			throw new BusinessApiException("When force option is set to CUSTOM_DAY then the allowed options are integers from 1 (included) to 31 (included).");
	}

	private void validateForceOptionAndForceCustDay(AccountingPeriodForceEnum forceOption, Integer forceCustomDay) {
		if (forceOption == null)
			throw new BusinessApiException("When accountingOperationAction is set to FORCE then the forceOption is mandatory");
		if (forceOption == AccountingPeriodForceEnum.CUSTOM_DAY && forceCustomDay == null)
			throw new BusinessApiException("When accountingOperationAction is set to FORCE and forceOption is set to CUSTOM_DAY then the forceCustomDay is mandatory");
	}

	private void validateInputs(AccountingPeriod entity, Boolean isUseSubAccountingPeriods, Object subAccountingPeriodType) {
		validateEndDate(entity.getEndDate());
		if (AccountingOperationAction.FORCE.equals(entity.getAccountingOperationAction())) {
			validateForceOptionAndForceCustDay(entity.getForceOption(), entity.getForceCustomDay());
		}
		if (entity.getCustomLockNumberDays() != null && entity.getCustomLockNumberDays() < 0)
			throw new BusinessApiException("customLockNumberDays must be positive");
		if (entity.getForceCustomDay() != null && entity.getForceCustomDay() < 0)
			throw new BusinessApiException("forceCustomDay must be positive");

		if (AccountingPeriodForceEnum.CUSTOM_DAY.equals(entity.getForceOption())) {
			validateForceCustomDay(entity.getForceCustomDay());
		}
		if (RegularUserLockOption.CUSTOM.equals(entity.getRegularUserLockOption())) {
			validateCustLockNumDaysAndCustLockOpt(entity.getCustomLockNumberDays(), entity.getCustomLockOption());
		}
		if (Boolean.TRUE.equals(isUseSubAccountingPeriods) && subAccountingPeriodType == null) {
			throw new BusinessApiException("subAccountingPeriodType cannot be null to use subAccountingPeriods");
		}
	}

	private boolean isUsedOnAccountingOperations(AccountingPeriod entity) {
		try {
			Date[] minMaxDates = subAccountingPeriodService.getMinMaxDatesFromSubAccountingPeriod(entity);
			Date startDate = Optional.ofNullable(minMaxDates[0]).orElseThrow(NoResultException::new);
			Date endDate = Optional.ofNullable(minMaxDates[1]).orElseThrow(NoResultException::new);
			Long count = getEntityManager().createQuery("select count(AO.id) from AccountOperation AO where AO.accountingDate between :start and :end", Long.class)
					.setParameter("start", startDate)
					.setParameter("end", endDate)
					.getSingleResult();
			return count > 0;
		} catch (NoResultException e) {
			return false;
		}
	}

	public AccountingPeriod findOpenAccountingPeriodByDate(Date date) {
        try {
            return getEntityManager().createNamedQuery("AccountingPeriod.findOpenAPByDate", entityClass)
                    .setParameter("date", date)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of AccountingPeriodYear {} found", getEntityClass().getSimpleName(), date);
            return null;
        }
    }
}

package org.meveo.service.accounting.impl;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodForceEnum;
import org.meveo.model.accounting.RegularUserLockOption;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

@Stateless
public class AccountingPeriodService extends PersistenceService<AccountingPeriod> {
	
    @Inject
    private SubAccountingPeriodService subAccountingPeriodService;
	
	public AccountingPeriod create(AccountingPeriod entity, Boolean isUseSubAccountingPeriods) {
		return createAccountingPeriod(entity, isUseSubAccountingPeriods, null);
	}
	
	public AccountingPeriod createAccountingPeriod(AccountingPeriod entity, Boolean isUseSubAccountingPeriods, Date startDate) {
		if (entity.getEndDate() == null) {
			throw new ValidationException("endDate is mandatory to create AccountingPeriod");
		}
		if (entity.getEndDate().before(new Date())) {
			throw new ValidationException("the given endDate " + DateUtils.formatAsDate(entity.getEndDate()) + " is incorrect ");
		}
		if(entity.getAccountingPeriodYear()==null) {
			entity.setAccountingPeriodYear(getAccountingPeriodYear(startDate, entity.getEndDate()));
		}
		if(entity.getForceOption() != null && entity.getForceOption().equals(AccountingPeriodForceEnum.CUSTOM_DAY)) {
			if (entity.getForceCustomDay() == null || entity.getForceCustomDay().intValue() == 0)
				throw new BusinessApiException("When force option is set to CUSTOM_DAY then the force custom day must not be null");
		}
		if (RegularUserLockOption.CUSTOM.equals(entity.getRegularUserLockOption())) {
			if (entity.getCustomLockNumberDays() == null || entity.getCustomLockOption() == null)
				throw new BusinessApiException("When regularUserLockOption option is set to CUSTOM then the customLockNumberDays and the customLockOption must not be null");
		}
		create(entity);
		if (Boolean.TRUE.equals(isUseSubAccountingPeriods)) {
			if (entity.getSubAccountingPeriodType() == null) {
				throw new BusinessApiException("subAccountingPeriodType cannot be null to use subAccountingPeriods");
			}
			subAccountingPeriodService.createSubAccountingPeriods(entity, entity.getSubAccountingPeriodType(), startDate, false);
		}
		return entity;
	}
	
    public AccountingPeriod findByAccountingPeriodYear(Integer year) {
        TypedQuery<AccountingPeriod> query = getEntityManager().createQuery("select ap from " + entityClass.getSimpleName() + " ap where accountingPeriodYear=:year", entityClass)
            .setParameter("year", year);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of AccountingPeriodYear {} found", getEntityClass().getSimpleName(), year);
            return null;
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
		AccountingPeriod nextAP = new AccountingPeriod();

		final Date endDate = Date.from(openAccountingPeriod.getEndDate().toInstant().atZone(ZoneId.systemDefault()).plusYears(1).toInstant());
		nextAP.setEndDate(endDate);
		
		final Date startDate = Date.from(openAccountingPeriod.getEndDate().toInstant().atZone(ZoneId.systemDefault()).plusNanos(1).toInstant());
		
		nextAP.setAccountingPeriodYear(getAccountingPeriodYear(startDate,endDate));
		nextAP.setUseSubAccountingCycles(openAccountingPeriod.isUseSubAccountingCycles());

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

		return createAccountingPeriod(nextAP, nextAP.isUseSubAccountingCycles(), startDate);
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private String getAccountingPeriodYear(Date startDate, Date endDate) {
		startDate = startDate != null ? startDate : new Date();
		final int startYear = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		final int endYear = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
		return (startYear == endYear) ? "" + endYear : "" + startYear + "-" + endYear;
	}
}

package org.meveo.service.accounting.impl;

import static java.time.LocalTime.MAX;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriodStatusEnum;
import org.meveo.model.accounting.SubAccountingPeriodTypeEnum;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.payments.impl.AccountOperationService;

@Stateless
public class SubAccountingPeriodService extends PersistenceService<SubAccountingPeriod> {

	private static final String ALL_USERS_TYPE = "allUsers";
	
	@Inject
	private AuditLogService auditLogService;
	
	@Inject
	private AccountOperationService accountOperationService;

    public SubAccountingPeriod findByAccountingPeriod(AccountingPeriod accountingPeriod, Date accountingDate) {
        TypedQuery<SubAccountingPeriod> query = getEntityManager()
            .createQuery("select s from " + entityClass.getSimpleName() + " s where s.accountingPeriod=:accountingPeriod and (:accountingDate >= s.startDate and :accountingDate <= s.endDate)",
                entityClass)
            .setParameter("accountingPeriod", accountingPeriod).setParameter("accountingDate", accountingDate);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

	public void createSubAccountingPeriods(AccountingPeriod ap, SubAccountingPeriodTypeEnum type) {
        Date maxDate = findMaxSubAccountingPeriod();

        LocalDateTime startDateTime = maxDate == null ? LocalDateTime.now() : maxDate.toInstant()
                    .atZone(ZoneId.systemDefault()).plusMonths(1).toLocalDateTime();
        LocalDateTime endDate = ap.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(MAX);
		createSubAccountingPeriodsByType(ap, type, startDateTime, endDate);
	}

	public void updateSubAccountingPeriods(AccountingPeriod ap, SubAccountingPeriodTypeEnum type) {
		Date[] minMaxDates = getMinMaxDatesFromSubAccountingPeriod(ap);
		Date startDate = Optional.ofNullable(minMaxDates[0]).orElse(ap.getAuditable().getCreated());
		Date endDate = Optional.ofNullable(minMaxDates[1]).orElse(ap.getEndDate());

		deleteOldSubAccountingPeriods(ap.getId());

		LocalDateTime startDateTime =
				Instant.ofEpochMilli(startDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime endDateTime =
				Instant.ofEpochMilli(endDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate().atTime(MAX);
		createSubAccountingPeriodsByType(ap, type, startDateTime, endDateTime);
	}

	private void createSubAccountingPeriodsByType(AccountingPeriod accountingPeriod, SubAccountingPeriodTypeEnum type,
												  LocalDateTime startDateTime, LocalDateTime endDate) {
		final int numberOfPeriodsPerYear = type.getNumberOfPeriodsPerYear();
		final int monthsPerPeriod = 12 / numberOfPeriodsPerYear;
		int number = 1;

		LocalDate fiscalYearStartDate = startDateTime.toLocalDate();
		LocalDateTime startDatePeriod = calculateInitialStartDatePeriod(fiscalYearStartDate,endDate);
		LocalDateTime endDatePeriod = calculateInitialEndDatePeriod(monthsPerPeriod, startDatePeriod, endDate);

		while ((!endDatePeriod.isAfter(endDate) || endDatePeriod.isEqual(endDate.toLocalDate().atStartOfDay()))) {
			if (!endDatePeriod.isBefore(fiscalYearStartDate.atTime(MAX))) {
				createSubAccountingPeriod(accountingPeriod, startDatePeriod, endDatePeriod, number);
				number++;
			}
			//next period
			startDatePeriod = endDatePeriod.plusDays(1).toLocalDate().atStartOfDay();
			endDatePeriod = calculateNextEndDatePeriod(monthsPerPeriod, endDatePeriod, endDate);
		}
	}


	public LocalDateTime calculateInitialStartDatePeriod(LocalDate fiscalYearStartDate, LocalDateTime endDate) {

		int startDay = 0;
		Month startMonth = Month.JANUARY;
		LocalDate lastYear = endDate.minusYears(1).plusDays(1).toLocalDate();

		if(isEndOfMonth(endDate)){
			startDay = fiscalYearStartDate.atStartOfDay().with(TemporalAdjusters.firstDayOfMonth()).getDayOfMonth();
		}else{
			startDay = endDate.getDayOfMonth() + 1;
			startMonth = lastYear.getMonth();
		}

		return LocalDate.of(lastYear.getYear(), startMonth, startDay).atStartOfDay();
	}

	private static LocalDateTime calculateNextEndDatePeriod(int monthsPerPeriod, LocalDateTime endDatePeriod, LocalDateTime endDate) {

		return isEndOfMonth(endDate) ?

				endDatePeriod.toLocalDate()
						.plusMonths(monthsPerPeriod)
						.with(TemporalAdjusters.lastDayOfMonth())
						.atStartOfDay() :

				endDatePeriod.toLocalDate()
						.plusMonths(monthsPerPeriod)
						.atStartOfDay();

	}

	private static LocalDateTime calculateInitialEndDatePeriod(int monthsPerPeriod, LocalDateTime startDatePeriod, LocalDateTime endDate) {

		return isEndOfMonth(endDate) ?

				startDatePeriod.toLocalDate()
						.plusMonths(monthsPerPeriod)
						.minusDays(1)
						.with(TemporalAdjusters.lastDayOfMonth())
						.atStartOfDay() :

				startDatePeriod.toLocalDate()
						.plusMonths(monthsPerPeriod)
						.minusDays(1)
						.atStartOfDay();
	}

	private static boolean isEndOfMonth(LocalDateTime endDate) {

		LocalDate endDateLocalDate = endDate.toLocalDate();

		return endDateLocalDate.withDayOfMonth(
				endDate.getMonth().length(endDate.toLocalDate().isLeapYear())).equals(endDateLocalDate);
	}

	private void createSubAccountingPeriod(AccountingPeriod ap, LocalDateTime startDate, LocalDateTime endDate, int number) {
		SubAccountingPeriod subAccountingPeriod = new SubAccountingPeriod();
		subAccountingPeriod.setAccountingPeriod(ap);
		subAccountingPeriod.setStartDate(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
		subAccountingPeriod.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
		subAccountingPeriod.setNumber(number);
		create(subAccountingPeriod);
	}

	public SubAccountingPeriod findByNumber(Integer number, String fiscalYear) {
		try {
			return (SubAccountingPeriod) getEntityManager().createNamedQuery("SubAccountingPeriod.findByNumber")
					.setParameter("number", number).setParameter("fiscalYear", fiscalYear).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} of SubAccountingPeriodYear {} found", getEntityClass().getSimpleName(), number);
			return null;
		}
	}
	
	public SubAccountingPeriod findLastSubAccountingPeriod() {
    	try {
			return (SubAccountingPeriod) getEntityManager().createNamedQuery("SubAccountingPeriod.findLastSubAP")
					.getSingleResult();
		} catch (NoResultException e) {
			log.debug("No SubAccountingPeriod found");
			return null;
		}
    }

	public SubAccountingPeriod findNextOpenSubAccountingPeriod(Date accountingDate) {
		try {
			return (SubAccountingPeriod) getEntityManager().createNamedQuery("SubAccountingPeriod.findNextOpenSubAP")
					.setParameter("accountingDate", accountingDate)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			log.debug("No Open SubAccountingPeriod found after {}", accountingDate);
			return null;
		}
	}
	
	public void updateSubAccountingAllUsersStatus(String fiscalYear, String status,
			SubAccountingPeriod subAccountingPeriod, String reason) {
		if (subAccountingPeriod.getAccountingPeriod() == null || !subAccountingPeriod.getAccountingPeriod().getAccountingPeriodYear().equals(fiscalYear) ) {
			throw new NotFoundException("The accounting period in fiscal year "+fiscalYear+" not found");
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.OPEN.toString())) {
			subAccountingPeriod.setAllUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
			subAccountingPeriod.setEffectiveClosedDate(null);
			subAccountingPeriod.setAllUsersReopeningReason(reason);
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.CLOSED.toString())) {
			subAccountingPeriod.setAllUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
			subAccountingPeriod.setEffectiveClosedDate(new Date());
		}

		updateSubAccountingRegularUsersStatus(fiscalYear, status, subAccountingPeriod, reason);
	}

	public void updateSubAccountingRegularUsersStatus(String fiscalYear, String status,
			SubAccountingPeriod subAccountingPeriod, String reason) {
		if (subAccountingPeriod.getAccountingPeriod() == null || !subAccountingPeriod.getAccountingPeriod().getAccountingPeriodYear().equals(fiscalYear) ) {
			throw new NotFoundException("The accounting period in fiscal year "+fiscalYear+" not found");
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.OPEN.toString())) {
			subAccountingPeriod.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
			subAccountingPeriod.setRegularUsersClosedDate(null);
			subAccountingPeriod.setRegularUsersReopeningReason(reason);
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.CLOSED.toString())) {
			subAccountingPeriod.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
			subAccountingPeriod.setRegularUsersClosedDate(new Date());
			
			List<Long> ids = new ArrayList<>();
			ids.add(subAccountingPeriod.getId());
			resetSequenceIfIsTheLastPeriode(subAccountingPeriod.getAccountingPeriod(), ids);
		}
	}
	
	@Override
    public SubAccountingPeriod update(SubAccountingPeriod entity) throws BusinessException {
		AuditLog auditLog = new AuditLog();
		SubAccountingPeriod subAccountingPeriod = findById(entity.getId());
		
		if (subAccountingPeriod != null && entity.getAllUsersSubPeriodStatus() != null && !entity.getAllUsersSubPeriodStatus().equals(subAccountingPeriod.getAllUsersSubPeriodStatus())) {
			createAuditLog(entity, auditLog, ALL_USERS_TYPE);
		}
		if (subAccountingPeriod != null && entity.getRegularUsersSubPeriodStatus() != null && !entity.getRegularUsersSubPeriodStatus().equals(subAccountingPeriod.getRegularUsersSubPeriodStatus())) {
			createAuditLog(entity, auditLog, "regularUsers");
		}
		super.update(entity);
		if(auditLog.getEntity() != null) {
			auditLogService.create(auditLog);
		}
		return entity;
	}
	
	
	
	public void createAuditLog(SubAccountingPeriod entity, AuditLog auditLog, String usersType) {
        
        auditLog.setActor(currentUser.getUserName());
        auditLog.setCreated(new Date());
        auditLog.setEntity("SubAccountingPeriod");
        auditLog.setOrigin(entity.getAccountingPeriod().getAccountingPeriodYear());
        auditLog.setAction("update "+(usersType.equals(ALL_USERS_TYPE)?"allUsersStatus":"regularUsersStatus"));
        auditLog.setParameters("user "+currentUser.getUserName()+" "
        					+(usersType.equals(ALL_USERS_TYPE)?entity.getAllUsersSubPeriodStatus():entity.getRegularUsersSubPeriodStatus())
        					+" the sub period number "+entity.getNumber()+ (usersType.equals(ALL_USERS_TYPE)?" for all users":"for regular users"));
    }

    private Date findMaxSubAccountingPeriod() {
        TypedQuery<Date> query = getEntityManager().createQuery("select max(s.endDate) from SubAccountingPeriod s ", Date.class);
        try {
            return new Date(query.getSingleResult().getTime());
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error("Cannot find the max date in sub accounting period");
            return null;
        }
    }
	public Date[] getMinMaxDatesFromSubAccountingPeriod(AccountingPeriod entity) {
		Date[] minMaxDates = new Date[2];
		try {
			Object[] result = (Object[]) getEntityManager().createQuery("select min(sap.startDate), max(sap.endDate) from SubAccountingPeriod sap "
							+ " where sap.accountingPeriod.id = :ap")
					.setParameter("ap", entity.getId())
					.getSingleResult();
			minMaxDates[0] = (Date) result[0];
			minMaxDates[1] = (Date) result[1];
		} catch (NoResultException e) {
			//ignore
		}
		return minMaxDates;
	}

	private void deleteOldSubAccountingPeriods(Long sapId) {
		try {
			getEntityManager().createQuery("delete from SubAccountingPeriod sap where sap.accountingPeriod.id = :sapId")
					.setParameter("sapId", sapId)
					.executeUpdate();
		} catch (Exception e) {
			log.error("Unable to delete sub accounting period for the accounting period id {}", sapId);
		}
	}
	
	public List<SubAccountingPeriod> findByAccountingPeriodAndEndDate(AccountingPeriod accountingPeriod, Date endDate) {
        try {
            return getEntityManager()
						.createNamedQuery("SubAccountingPeriod.findByAPAndAfterEndDate", entityClass)
						.setParameter("apId", accountingPeriod.getId())
						.setParameter("endDate", endDate, TemporalType.DATE)
						.getResultList();
        } catch (NoResultException e) {
            log.debug("No {} of AccountingPeriod {} found", getEntityClass().getSimpleName(), accountingPeriod.getId());
            return new ArrayList<>();
        }
    }

    public int closeSubAccountingPeriods(List<Long> ids) {
        return getEntityManager()
        .createNamedQuery("SubAccountingPeriod.closeSubAccountingPeriods")
        .setParameter("ids", ids)
        .executeUpdate();
    }

    public void resetSequenceIfIsTheLastPeriode(AccountingPeriod accountingPeriod, List<Long> ids) {
        boolean  isTheLastPeriodToClose = isTheLastPeriodToClose(accountingPeriod, ids);
        if (isTheLastPeriodToClose) {
            accountOperationService.resetOperationNumberSequence();
        }
    }

    public boolean isTheLastPeriodToClose(AccountingPeriod accountingPeriod, List<Long> ids) {
        return getEntityManager()
                .createNamedQuery("SubAccountingPeriod.isTheLastPeriodToClose", Long.class)
                .setParameter("accountingPeriod", accountingPeriod)
                .setParameter("ids", ids)
                .getSingleResult() == 0;
    }
}

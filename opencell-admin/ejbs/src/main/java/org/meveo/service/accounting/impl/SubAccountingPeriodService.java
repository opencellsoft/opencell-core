package org.meveo.service.accounting.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
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

@Stateless
public class SubAccountingPeriodService extends PersistenceService<SubAccountingPeriod> {

	private static final String allUsersType = "allUsers";
	
	@Inject
	private AuditLogService auditLogService;

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
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDate = ap.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX);
		createSubAccountingPeriodsByType(ap, type, startDateTime, endDate);
	}

	public void updateSubAccountingPeriods(AccountingPeriod ap, SubAccountingPeriodTypeEnum type) {
		Date[] minMaxDates = getMinMaxDatesFromSubAccountingPeriod(ap);
		Date startDate = Optional.ofNullable(minMaxDates[0]).orElse(ap.getAuditable().getCreated());
		Date endDate = Optional.ofNullable(minMaxDates[1]).orElse(ap.getEndDate());

		deleteOldSubAccountingPeriods(ap.getId());

		LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX);
		createSubAccountingPeriodsByType(ap, type, startDateTime, endDateTime);
	}

	private void createSubAccountingPeriodsByType(AccountingPeriod ap, SubAccountingPeriodTypeEnum type, LocalDateTime startDateTime, LocalDateTime endDate) {
		final int numberOfPeriodsPerYear = type.getNumberOfPeriodsPerYear();
		final int monthsPerPeriod = 12 / numberOfPeriodsPerYear;
		LocalDate now = startDateTime == null ? LocalDate.now() : startDateTime.toLocalDate();
		int currentYear = now.getYear();
		LocalDateTime startDatePeriod = now.withYear(currentYear).withDayOfYear(1).atStartOfDay();
		LocalDateTime endDatePeriod = startDatePeriod.toLocalDate().plusMonths(monthsPerPeriod).minusDays(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
		while (!endDatePeriod.isAfter(endDate)) {
			if (endDatePeriod.toLocalDate().isAfter(now)) {
				createSubAccPeriod(ap, startDatePeriod, endDatePeriod);
			}
			//next period
			startDatePeriod = endDatePeriod.plusDays(1).toLocalDate().atStartOfDay();
			endDatePeriod = startDatePeriod.toLocalDate().plusMonths(monthsPerPeriod).minusDays(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
		}
	}

	private void createSubAccPeriod(AccountingPeriod ap, LocalDateTime startDate, LocalDateTime endDate) {
		SubAccountingPeriod subAccountingPeriod = new SubAccountingPeriod();
		subAccountingPeriod.setAccountingPeriod(ap);
		subAccountingPeriod.setStartDate(Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()));
		subAccountingPeriod.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
		create(subAccountingPeriod);
	}

	public SubAccountingPeriod findByNumber(Integer number) {
		try {
			return (SubAccountingPeriod) getEntityManager().createNamedQuery("SubAccountingPeriod.findByNumber")
					.setParameter("number", number).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} of SubAccountingPeriodYear {} found", getEntityClass().getSimpleName(), number);
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
		}
	}
	
	@Override
    public SubAccountingPeriod update(SubAccountingPeriod entity) throws BusinessException {
		AuditLog auditLog = new AuditLog();
		SubAccountingPeriod subAccountingPeriod = findById(entity.getId());
		
		if (subAccountingPeriod != null && entity.getAllUsersSubPeriodStatus() != null && !entity.getAllUsersSubPeriodStatus().equals(subAccountingPeriod.getAllUsersSubPeriodStatus())) {
			createAuditLog(entity, auditLog, allUsersType);
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
        auditLog.setAction("update "+(usersType.equals(allUsersType)?"allUsersStatus":"regularUsersStatus"));
        auditLog.setParameters("user "+currentUser.getUserName()+" "
        					+(usersType.equals(allUsersType)?entity.getAllUsersSubPeriodStatus():entity.getRegularUsersSubPeriodStatus()) 
        					+" the sub period number "+entity.getNumber()+ (usersType.equals(allUsersType)?" for all users":"for regular users"));
    }

    private Date findMaxSubAccountingPeriod() {
        TypedQuery<Date> query = getEntityManager().createQuery("select max(s.endDate) from SubAccountingPeriod s ", Date.class);
        try {
            return query.getSingleResult();
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
}

package org.meveo.service.accounting.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	private final String allUsersType = "allUsers";
	
	@Inject
	private AuditLogService auditLogService;

    public SubAccountingPeriod findByAccountingPeriod(AccountingPeriod accountingPeriod, Date accountingDate) {
        TypedQuery<SubAccountingPeriod> query = getEntityManager()
            .createQuery("select s from " + entityClass.getSimpleName() + " s where accountingPeriod=:accountingPeriod and (:accountingDate >= startDate and :accountingDate <= endDate)",
                entityClass)
            .setParameter("accountingPeriod", accountingPeriod).setParameter("accountingDate", accountingDate);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    
	public List<SubAccountingPeriod> createSubAccountingPeriods(AccountingPeriod ap, SubAccountingPeriodTypeEnum type,
			Date start, boolean regularPeriods) {
		List<SubAccountingPeriod> periods = new ArrayList<>();
		LocalDateTime startDateTime = start==null? LocalDate.now().withDayOfMonth(1).atStartOfDay(): start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime endDate = ap.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX);
		final int numberOfPeriodsPerYear = type.getNumberOfPeriodsPerYear();
		final int monthsPerPeriod = 12 / numberOfPeriodsPerYear;
		while (endDate.isAfter(startDateTime)) {
			SubAccountingPeriod subAccountingPeriod = new SubAccountingPeriod();
			subAccountingPeriod.setAccountingPeriod(ap);
			subAccountingPeriod.setStartDate(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant()));
			startDateTime = startDateTime.plusMonths(monthsPerPeriod);
			if (!endDate.isAfter(startDateTime) && !regularPeriods) {
				subAccountingPeriod.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
			} else {
				subAccountingPeriod.setEndDate(Date.from(startDateTime.minusNanos(1).atZone(ZoneId.systemDefault()).toInstant()));
			}
			periods.add(subAccountingPeriod);
			create(subAccountingPeriod);
		}
		return periods;
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
			SubAccountingPeriod subAccountingPeriod) {
		if (subAccountingPeriod.getAccountingPeriod() == null || !subAccountingPeriod.getAccountingPeriod().getAccountingPeriodYear().equals(fiscalYear) ) {
			throw new NotFoundException("The accounting period in fiscal year "+fiscalYear+" not found");
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.OPEN.toString())) {
			subAccountingPeriod.setAllUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
			subAccountingPeriod.setEffectiveClosedDate(null);
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.CLOSED.toString())) {
			subAccountingPeriod.setAllUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
			subAccountingPeriod.setEffectiveClosedDate(new Date());
		}
	}

	public void updateSubAccountingRegularUsersStatus(String fiscalYear, String status,
			SubAccountingPeriod subAccountingPeriod) {
		if (subAccountingPeriod.getAccountingPeriod() == null || !subAccountingPeriod.getAccountingPeriod().getAccountingPeriodYear().equals(fiscalYear) ) {
			throw new NotFoundException("The accounting period in fiscal year "+fiscalYear+" not found");
		}
		if (status.equalsIgnoreCase(SubAccountingPeriodStatusEnum.OPEN.toString())) {
			subAccountingPeriod.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
			subAccountingPeriod.setRegularUsersClosedDate(null);
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
		
		if (subAccountingPeriod != null && !entity.getAllUsersSubPeriodStatus().equals(subAccountingPeriod.getAllUsersSubPeriodStatus())) {
			createAuditLog(entity, auditLog, allUsersType);
		}
		if (subAccountingPeriod != null && !entity.getRegularUsersSubPeriodStatus().equals(subAccountingPeriod.getRegularUsersSubPeriodStatus())) {
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

}

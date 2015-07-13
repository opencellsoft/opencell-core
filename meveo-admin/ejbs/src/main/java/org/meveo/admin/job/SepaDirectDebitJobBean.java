package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.sepa.SepaService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class SepaDirectDebitJobBean {

	@Inject
	private Logger log;

	@Inject
	private DDRequestLotOpService dDRequestLotOpService;

	@Inject
	private SepaService sepaService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser) {
		log.debug("Running for user={}, parameter={}", currentUser, parameter);
		Provider currentProvider=currentUser.getProvider();
		try {
			List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps(currentProvider);

			if (ddrequestOps != null) {
				log.info("ddrequestOps found:" + ddrequestOps.size());
			}

			for (DDRequestLotOp ddrequestLotOp : ddrequestOps) {
				try {
					if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
						sepaService.createDDRquestLot(ddrequestLotOp.getFromDueDate(), ddrequestLotOp.getToDueDate(),
								currentUser, currentProvider);
					} else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
						sepaService.exportDDRequestLot(ddrequestLotOp.getDdrequestLOT().getId(), currentUser);
					}

					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
					
				} catch (BusinessEntityException e) {
                    log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
					ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
				
				} catch (Exception e) {
                    log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
					ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
				}
			}
		} catch (Exception e) {
		    log.error("Failed to sepa direct debit", e);
		}
	}

}

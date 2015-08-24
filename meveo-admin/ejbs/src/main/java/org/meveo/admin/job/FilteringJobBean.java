package org.meveo.admin.job;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.ScriptInstanceService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilteringJobBean {

	@Inject
	private Logger log;

	@Inject
	private FilterService filterService;

	@EJB
	private ScriptInstanceService scriptInstanceService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter, String filterCode, String scriptInstanceCode,
			User currentUser) {
		log.debug("Running for user={}, parameter={}", currentUser, parameter);

		Provider provider = currentUser.getProvider();
		Filter filter = filterService.findByCode(filterCode, provider);
		if (filter != null) {
			ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);

			try {
				List<? extends IEntity> xmlEntities = filterService.filteredListAsObjects(filter, provider);
				for (Object obj : xmlEntities) {
					scriptInstanceService.executeScriptOnObject(scriptInstance, obj);
				}
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

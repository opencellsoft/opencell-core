package org.meveo.admin.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.meveo.service.filter.FilterService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Stateless
public class FilteringJobBean {

	@Inject
	private Logger log;

	@Inject
	private FilterService filterService;
	
	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter, String filterCode, String scriptInstanceCode,Map<String,Object> variables,
		String recordVariableName,	User currentUser) {
		log.debug("Running for user={}, parameter={}", currentUser, parameter);

		Provider provider = currentUser.getProvider();
		Filter filter = filterService.findByCode(filterCode, provider);
		if (filter != null) {
			Class<ScriptInterface> scriptInterfaceClass = scriptInstanceService.getScriptInterface(currentUser.getProvider(), scriptInstanceCode);
			ScriptInterface scriptInterface = null;
			try {
				scriptInterface = scriptInterfaceClass.newInstance();
				scriptInterface.init(variables,provider);
				List<? extends IEntity> xmlEntities = filterService.filteredListAsObjects(filter, provider);
				result.setNbItemsToProcess(xmlEntities.size());
				for (Object obj : xmlEntities) {
					Map<String,Object> context=new HashMap<String,Object>();
					context.put(recordVariableName,obj);
					try{
						scriptInterface.execute(context,provider);
						result.registerSucces();
					} catch(BusinessException ex){
						result.registerError(ex.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.setReport("error:"+e.getMessage());
			} finally{
				try{
					scriptInterface.finalize(variables,provider);
				}catch (Exception e) {
					e.printStackTrace();
					result.setReport("finalize error:"+e.getMessage());
				}
			}
		}
	}
}

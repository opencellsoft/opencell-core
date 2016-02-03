package org.meveo.api.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValueException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.JobTriggerService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Tyshan Shi
 **/
@Stateless
public class JobTriggerApi extends BaseApi {

	@Inject
	private JobTriggerService jobTriggerService;

	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;

	@Inject
	private ScriptInstanceService scriptInstanceService;
	
	@Inject
	private JobInstanceService jobInstanceService;

	public void create(JobTriggerDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter())
				&& !StringUtils.isBlank(postData.getEventTypeFilter())) {
			if (jobTriggerService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(JobTrigger.class, postData.getCode());
			}
			ScriptInstance scriptInstance = null;
			if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
				scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(),
						currentUser.getProvider());
				if (scriptInstance == null) {
					throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
				}
			}
			// check class
			try {
				Class.forName(postData.getClassNameFilter());
			} catch (Exception e) {
				throw new InvalidParameterException("classNameFilter", postData.getClassNameFilter());
			}

			NotificationEventTypeEnum notificationEventType = null;
			try {
				notificationEventType = NotificationEventTypeEnum.valueOf(postData.getEventTypeFilter());
			} catch (IllegalArgumentException e) {
				log.error("enum: {}", e);
				throw new InvalidEnumValueException(NotificationEventTypeEnum.class.getName(), postData.getEventTypeFilter());
			}

			CounterTemplate counterTemplate = null;
			if (!StringUtils.isBlank(postData.getCounterTemplate())) {
				counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(),
						currentUser.getProvider());
			}
			JobInstance jobInstance=null;
			if(!StringUtils.isBlank(postData.getJobInstance())){
				jobInstance=jobInstanceService.findByCode(postData.getJobInstance(), currentUser.getProvider());
			}

			JobTrigger notif = new JobTrigger();
			notif.setProvider(currentUser.getProvider());
			notif.setCode(postData.getCode());
			notif.setClassNameFilter(postData.getClassNameFilter());
			notif.setEventTypeFilter(notificationEventType);
			notif.setScriptInstance(scriptInstance);
			notif.setParams(postData.getScriptParams());
			notif.setElFilter(postData.getElFilter());
			notif.setCounterTemplate(counterTemplate);
			notif.setJobInstance(jobInstance);
			notif.setJobParams(postData.getJobParams());
			jobTriggerService.create(notif, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getClassNameFilter())) {
				missingParameters.add("classNameFilter");
			}
			if (StringUtils.isBlank(postData.getEventTypeFilter())) {
				missingParameters.add("eventTypeFilter");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public JobTriggerDto find(String notificationCode, Provider provider) throws MeveoApiException {
		JobTriggerDto result = new JobTriggerDto();

		if (!StringUtils.isBlank(notificationCode)) {
			JobTrigger notif = jobTriggerService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(JobTrigger.class, notificationCode);
			}

			result = new JobTriggerDto(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void update(JobTriggerDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getClassNameFilter())
				&& !StringUtils.isBlank(postData.getEventTypeFilter())) {
			JobTrigger notif = jobTriggerService.findByCode(postData.getCode(), currentUser.getProvider());
			if (notif == null) {
				throw new EntityDoesNotExistsException(JobTrigger.class, postData.getCode());
			}
			ScriptInstance scriptInstance = null;
			if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
				scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(),
						currentUser.getProvider());
				if (scriptInstance == null) {
					throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getScriptInstanceCode());
				}
			}
			// check class
			try {
				Class.forName(postData.getClassNameFilter());
			} catch (Exception e) {
				throw new InvalidParameterException("classNameFilter", postData.getClassNameFilter());
			}

			NotificationEventTypeEnum notificationEventType = null;
			try {
				notificationEventType = NotificationEventTypeEnum.valueOf(postData.getEventTypeFilter());
			} catch (IllegalArgumentException e) {
				log.error("enum: {}", e);
				throw new InvalidEnumValueException(NotificationEventTypeEnum.class.getName(), postData.getEventTypeFilter());
			}

			CounterTemplate counterTemplate = null;
			if (!StringUtils.isBlank(postData.getCounterTemplate())) {
				counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(),
						currentUser.getProvider());
			}
			
			JobInstance jobInstance=null;
			if(!StringUtils.isBlank(postData.getJobInstance())){
				jobInstance=jobInstanceService.findByCode(postData.getJobInstance(), currentUser.getProvider());
			}

			notif.setClassNameFilter(postData.getClassNameFilter());
			notif.setEventTypeFilter(notificationEventType);
			notif.setScriptInstance(scriptInstance);
			notif.setParams(postData.getScriptParams());
			notif.setElFilter(postData.getElFilter());
			notif.setCounterTemplate(counterTemplate);
			notif.setJobInstance(jobInstance);
			notif.setJobParams(postData.getJobParams());

			jobTriggerService.update(notif, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getClassNameFilter())) {
				missingParameters.add("classNameFilter");
			}
			if (StringUtils.isBlank(postData.getEventTypeFilter())) {
				missingParameters.add("eventTypeFilter");
			}
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String notificationCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(notificationCode)) {
			JobTrigger notif = jobTriggerService.findByCode(notificationCode, provider);

			if (notif == null) {
				throw new EntityDoesNotExistsException(JobTrigger.class, notificationCode);
			}

			jobTriggerService.remove(notif);
		} else {
			missingParameters.add("code");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void createOrUpdate(JobTriggerDto postData, User currentUser) throws MeveoApiException {
		if (jobTriggerService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}

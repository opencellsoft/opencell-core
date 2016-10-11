package org.meveo.api.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.JobTrigger;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.JobTriggerService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Tyshan Shi
 **/
@Stateless
public class JobTriggerApi extends BaseCrudApi<JobTrigger, JobTriggerDto> {

    @Inject
    private JobTriggerService jobTriggerService;

    @SuppressWarnings("rawtypes")
    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private JobInstanceService jobInstanceService;

    public JobTrigger create(JobTriggerDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }
        handleMissingParameters();

        if (jobTriggerService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(JobTrigger.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(), currentUser.getProvider());
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

        CounterTemplate counterTemplate = null;
        if (!StringUtils.isBlank(postData.getCounterTemplate())) {
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }
        JobInstance jobInstance = null;
        if (!StringUtils.isBlank(postData.getJobInstance())) {
            jobInstance = jobInstanceService.findByCode(postData.getJobInstance(), currentUser.getProvider());
            if (jobInstance == null) {
                throw new EntityDoesNotExistsException(JobInstance.class, postData.getJobInstance());
            }
        }

        JobTrigger notif = new JobTrigger();
        notif.setProvider(currentUser.getProvider());
        notif.setCode(postData.getCode());
        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);
        notif.setJobInstance(jobInstance);
        notif.setJobParams(postData.getJobParams());
        jobTriggerService.create(notif, currentUser);

        return notif;
    }

    @Override
    public JobTriggerDto find(String notificationCode, User currentUser) throws MeveoApiException {
        JobTriggerDto result = new JobTriggerDto();

        if (!StringUtils.isBlank(notificationCode)) {
            JobTrigger notif = jobTriggerService.findByCode(notificationCode, currentUser.getProvider());

            if (notif == null) {
                throw new EntityDoesNotExistsException(JobTrigger.class, notificationCode);
            }

            result = new JobTriggerDto(notif);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }

        return result;
    }

    public JobTrigger update(JobTriggerDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getClassNameFilter())) {
            missingParameters.add("classNameFilter");
        }
        if (postData.getEventTypeFilter() == null) {
            missingParameters.add("eventTypeFilter");
        }
        handleMissingParameters();

        JobTrigger notif = jobTriggerService.findByCode(postData.getCode(), currentUser.getProvider());
        if (notif == null) {
            throw new EntityDoesNotExistsException(JobTrigger.class, postData.getCode());
        }
        ScriptInstance scriptInstance = null;
        if (!StringUtils.isBlank(postData.getScriptInstanceCode())) {
            scriptInstance = scriptInstanceService.findByCode(postData.getScriptInstanceCode(), currentUser.getProvider());
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

        CounterTemplate counterTemplate = null;
        if (!StringUtils.isBlank(postData.getCounterTemplate())) {
            counterTemplate = counterTemplateService.findByCode(postData.getCounterTemplate(), currentUser.getProvider());
            if (counterTemplate == null) {
                throw new EntityDoesNotExistsException(CounterTemplate.class, postData.getCounterTemplate());
            }
        }

        JobInstance jobInstance = null;
        if (!StringUtils.isBlank(postData.getJobInstance())) {
            jobInstance = jobInstanceService.findByCode(postData.getJobInstance(), currentUser.getProvider());
            if (jobInstance == null) {
                throw new EntityDoesNotExistsException(JobInstance.class, postData.getJobInstance());
            }
        }

        notif.setClassNameFilter(postData.getClassNameFilter());
        notif.setEventTypeFilter(postData.getEventTypeFilter());
        notif.setScriptInstance(scriptInstance);
        notif.setParams(postData.getScriptParams());
        notif.setElFilter(postData.getElFilter());
        notif.setCounterTemplate(counterTemplate);
        notif.setJobInstance(jobInstance);
        notif.setJobParams(postData.getJobParams());

        notif = jobTriggerService.update(notif, currentUser);

        return notif;
    }

    public void remove(String notificationCode, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(notificationCode)) {
            JobTrigger notif = jobTriggerService.findByCode(notificationCode, currentUser.getProvider());

            if (notif == null) {
                throw new EntityDoesNotExistsException(JobTrigger.class, notificationCode);
            }

            jobTriggerService.remove(notif, currentUser);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }
    }

    @Override
    public JobTrigger createOrUpdate(JobTriggerDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (jobTriggerService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            return create(postData, currentUser);
        } else {
            return update(postData, currentUser);
        }
    }
}

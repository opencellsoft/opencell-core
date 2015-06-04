package org.meveo.api.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.TimerEntityService;

@Stateless
public class TimerEntityApi extends BaseApi {

    @Inject
    private TimerEntityService timerEntityService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    public void create(TimerEntityDto postData, User currentUser) throws MeveoApiException {
        if (!StringUtils.isBlank(postData.getJobCategory()) && !StringUtils.isBlank(postData.getJobName()) && !StringUtils.isBlank(postData.getName())
                && !StringUtils.isBlank(postData.getHour()) && !StringUtils.isBlank(postData.getMinute()) && !StringUtils.isBlank(postData.getSecond())
                && !StringUtils.isBlank(postData.getYear()) && !StringUtils.isBlank(postData.getMonth()) && !StringUtils.isBlank(postData.getDayOfMonth())
                && !StringUtils.isBlank(postData.getDayOfWeek())) {

            Provider provider = currentUser.getProvider();

            if (timerEntityService.findByName(postData.getName(), provider) != null) {
                throw new EntityAlreadyExistsException(TimerEntity.class, postData.getName());
            }
            JobCategoryEnum jobCategory = null;
            try {
                jobCategory = JobCategoryEnum.valueOf(postData.getJobCategory().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job category=" + postData.getJobCategory());
            }
            if (jobCategory == null) {
                throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid job name=" + postData.getJobName());
            }

            List<CustomFieldTemplate> customFieldTemplates = new ArrayList<CustomFieldTemplate>();
            HashMap<String, String> jobs = new HashMap<String, String>();
            jobs = TimerEntityService.jobEntries.get(jobCategory);

            // Create or add missing custom field templates for a job
            if (jobs.containsKey(postData.getJobName())) {
                customFieldTemplates.clear();
                Job job = timerEntityService.getJobByName(postData.getJobName());
                if (job.getCustomFields(currentUser) != null) {
                    customFieldTemplates = customFieldTemplateService.findByJobName(postData.getJobName());
                    if (customFieldTemplates != null && customFieldTemplates.size() != job.getCustomFields(currentUser).size()) {
                        for (CustomFieldTemplate cf : job.getCustomFields(currentUser)) {
                            if (!customFieldTemplates.contains(cf)) {
                                try {
                                    customFieldTemplateService.create(cf);
                                    customFieldTemplates.add(cf);
                                } catch (BusinessException e) {
                                    log.error("Failed  to init custom fields", e);
                                }
                            }
                        }
                    }
                }

            }
            TimerEntity timerEntity = new TimerEntity();

            timerEntity.getTimerInfo().setUserId(currentUser.getId());
            timerEntity.getTimerInfo().setActive(postData.isActive());
            timerEntity.getTimerInfo().setParametres(postData.getParameter());
            if (!StringUtils.isBlank(postData.getFollowingTimer())) {
                TimerEntity nextJob = timerEntityService.findByName(postData.getFollowingTimer(), provider);
                timerEntity.setFollowingTimer(timerEntityService.findByName(postData.getFollowingTimer(), provider));
                if (nextJob == null) {
                    throw new MeveoApiException(MeveoApiErrorCode.BUSINESS_API_EXCEPTION, "Invalid next job=" + postData.getFollowingTimer());
                }
            }
            timerEntity.setJobCategoryEnum(jobCategory);
            timerEntity.setJobName(postData.getJobName());
            timerEntity.setName(postData.getName());
            timerEntity.setHour(postData.getHour());
            timerEntity.setMinute(postData.getMinute());
            timerEntity.setSecond(postData.getSecond());
            timerEntity.setYear(postData.getYear());
            timerEntity.setMonth(postData.getMonth());
            timerEntity.setDayOfMonth(postData.getDayOfMonth());
            timerEntity.setDayOfWeek(postData.getDayOfWeek());

            if (postData.getCustomFields() != null) {
                // populate customFields
                if (postData.getCustomFields() != null) {
                    try {
                        populateCustomFields(customFieldTemplates, postData.getCustomFields().getCustomField(), timerEntity, "timerEntity", currentUser);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        log.error("Failed to associate custom field instance to an entity", e);
                        throw new MeveoApiException("Failed to associate custom field instance to an entity");
                    }
                }
            }

            timerEntityService.create(timerEntity, currentUser, provider);

        } else {

            if (StringUtils.isBlank(postData.getJobName())) {
                missingParameters.add("jobName");
            }
            if (StringUtils.isBlank(postData.getName())) {
                missingParameters.add("name");
            }
            if (StringUtils.isBlank(postData.getHour())) {
                missingParameters.add("hour");
            }
            if (StringUtils.isBlank(postData.getMinute())) {
                missingParameters.add("minute");
            }
            if (StringUtils.isBlank(postData.getSecond())) {
                missingParameters.add("second");
            }
            if (StringUtils.isBlank(postData.getYear())) {
                missingParameters.add("year");
            }
            if (StringUtils.isBlank(postData.getMonth())) {
                missingParameters.add("month");
            }
            if (StringUtils.isBlank(postData.getDayOfMonth())) {
                missingParameters.add("dayOfMonth");
            }
            if (StringUtils.isBlank(postData.getDayOfWeek())) {
                missingParameters.add("dayOfWeek");
            }

            throw new MissingParameterException(getMissingParametersExceptionMessage());
        }
    }
}
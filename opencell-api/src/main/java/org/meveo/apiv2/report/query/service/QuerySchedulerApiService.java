package org.meveo.apiv2.report.query.service;

import static java.util.Optional.empty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.report.QuerySchedulerService;

public class QuerySchedulerApiService implements ApiService<QueryScheduler> {

    @Inject
    private QuerySchedulerService querySchedulerService;
    
    @Inject
    private JobInstanceService jobInstanceService;
    
    @Inject
    private UserService userService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

	@Transactional
    @Override
    public QueryScheduler create(QueryScheduler entity) {
        try {
        	for(User user: entity.getUsersToNotify()) {
        		if(userService.findById(user.getId()) == null) {
        			throw new NotFoundException("The user with id {" + user.getId() + "} does not exists");
        		}
        		user.setVersion(userService.findById(user.getId()).getVersion());
        	}
        	
        	ReportQuery reportQuery = entity.getReportQuery();
        	String code = reportQuery.getCode() + "_Job";
			JobInstance jobInstance = jobInstanceService.findByCode(code);
			boolean disabled = entity.getIsQueryScheduler();
			if (jobInstance != null) {
				entity = jobInstance.getQueryScheduler() != null ? querySchedulerService.findById(jobInstance.getQueryScheduler().getId()) : entity;
			}else {
				jobInstance = new JobInstance();
			}

			jobInstance.setCode(code);
            jobInstance.setDescription("Job for report query='" + reportQuery.getCode() + "'");
            jobInstance.setJobCategoryEnum(MeveoJobCategoryEnum.REPORTING_QUERY);
            jobInstance.setJobTemplate("ReportQueryJob");
            jobInstance.setCfValue("reportQuery", reportQuery);
            jobInstance.setQueryScheduler(entity);
			jobInstance.setDisabled(!disabled);
			if(jobInstance.getId() == null) {
				querySchedulerService.create(entity);
				jobInstanceService.create(jobInstance);
				// Update the QueryScheduler
				entity.setJobInstance(jobInstance);
	            querySchedulerService.update(entity);
			}
			jobInstanceService.update(jobInstance);
			entity.getUsersToNotify().size();
			entity.getEmailsToNotify().size();
            return entity;
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }


	@Override
	public List<QueryScheduler> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Collections.emptyList();
		
	}

	@Override
	public Long getCount(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<QueryScheduler> findById(Long id) {
		return empty();
	}

	@Override
	public Optional<QueryScheduler> update(Long id, QueryScheduler baseEntity) {
		return empty();
	}

	@Override
	public Optional<QueryScheduler> patch(Long id, QueryScheduler baseEntity) {
		return empty();
	}

	@Override
	public Optional<QueryScheduler> delete(Long id) {
		return empty();
	}

	@Override
	public Optional<QueryScheduler> findByCode(String code) {
		return empty();
	}

}
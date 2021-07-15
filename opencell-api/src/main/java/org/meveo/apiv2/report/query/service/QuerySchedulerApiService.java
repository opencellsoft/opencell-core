package org.meveo.apiv2.report.query.service;

import static java.util.Optional.empty;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.admin.User;
import org.meveo.model.report.query.QueryScheduler;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.report.QuerySchedulerService;

public class QuerySchedulerApiService implements ApiService<QueryScheduler> {

    @Inject
    private QuerySchedulerService querySchedulerService;
    
    @Inject
    private UserService userService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;


    

    @Override
    public QueryScheduler create(QueryScheduler entity) {
        try {
        	for(User user: entity.getUsersToNotify()) {
        		if(userService.findById(user.getId()) == null) {
        			throw new NotFoundException("The user with id {" + user.getId() + "} does not exists");
        		}
        	}
        	querySchedulerService.create(entity);
            return entity;
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }


	@Override
	public List<QueryScheduler> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return null;
		
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
package org.meveo.api;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.util.MeveoJpaForJobs;

/**
 * @author Edward P. Legaspi
 * @since Oct 15, 2013
 **/
public abstract class BaseApi {

	@Inject
	protected ProviderService providerService;

	@Inject
	protected UserService userService;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

}

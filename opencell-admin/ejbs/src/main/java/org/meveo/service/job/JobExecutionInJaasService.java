/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.job;

import java.io.Serializable;

import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.BaseService;

@Stateless
@RunAs("jobRunner")
public class JobExecutionInJaasService extends BaseService implements Serializable {

    private static final long serialVersionUID = -7234046782694277895L;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Initiate job in a JAAS secured fashion - see @RunAs annotation. To be run from a job schedule expiration.
     * 
     * @param jobInstance Job instance to run
     * @param job Job implementation class
     * @throws BusinessException business exception.
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void executeInJaas(JobInstance jobInstance, Job job) throws BusinessException {
        // Force authentication to a current job's user
        currentUserProvider.forceAuthentication(jobInstance.getAuditable().getCreator(), jobInstance.getProviderCode());

        // log.trace("Running {} as user {}", job.getClass(), currentUser);
        job.execute(jobInstance, null);
    }
}
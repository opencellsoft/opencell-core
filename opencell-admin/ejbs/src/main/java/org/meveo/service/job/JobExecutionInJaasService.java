/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
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
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

package org.meveo.jpa.event;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.service.index.ElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA delete event listener. Flushes pending changes to Elastic Search.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
public class DeleteEventListener implements PostDeleteEventListener {

    //private static final long serialVersionUID = 4290464068190662604L;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        ElasticClient elasticClient = (ElasticClient) EjbUtils.getServiceInterface("ElasticClient");
        try {
            elasticClient.flushChanges();
        } catch (BusinessException e) {
            Logger log = LoggerFactory.getLogger(getClass());
            log.error("Failed to flush ES changes", e);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }
}
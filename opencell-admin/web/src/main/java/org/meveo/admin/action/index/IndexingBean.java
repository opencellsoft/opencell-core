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

package org.meveo.admin.action.index;

import java.io.Serializable;
import java.util.concurrent.Future;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ReindexingStatistics;
import org.slf4j.Logger;

@Named
@SessionScoped
public class IndexingBean implements Serializable {

    private static final long serialVersionUID = 7051728474316387375L;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    protected Messages messages;

    @Inject
    private Logger log;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    private Future<ReindexingStatistics> reindexingFuture;
    
	public IndexingBean() {
		BaseBean.showDeprecatedWarning();
	}
	
    /**
     * Drop and recreate index and reindex data of current provider
     */
    public void cleanAndReindex() {
        reindexingFuture = null;

        if (!elasticClient.isEnabled()) {
            messages.error(new BundleKey("messages", "indexing.notEnabled"));
            return;
        }

        try {
            reindexingFuture = elasticClient.cleanAndReindex(currentUser.unProxy(), true);
            messages.info(new BundleKey("messages", "indexing.started"));

        } catch (Exception e) {
            log.error("Failed to initiate Elastic Search cleanup and population", e);
            messages.info(new BundleKey("messages", "indexing.startFailed"), e.getMessage());
        }
    }

    /**
     * Drop and recreate index and reindex data of all providers
     */
    public void cleanAndReindexAll() {
        reindexingFuture = null;

        if (!elasticClient.isEnabled()) {
            messages.error(new BundleKey("messages", "indexing.notEnabled"));
            return;
        }

        try {
            reindexingFuture = elasticClient.cleanAndReindexAll(currentUser.unProxy(), true);
            messages.info(new BundleKey("messages", "indexing.started"));

        } catch (Exception e) {
            log.error("Failed to initiate Elastic Search cleanup and population", e);
            messages.info(new BundleKey("messages", "indexing.startFailed"), e.getMessage());
        }
    }

    public Future<ReindexingStatistics> getReindexingFuture() {
        return reindexingFuture;
    }

    public boolean isEnabled() {
        return elasticClient.isEnabled();
    }
}
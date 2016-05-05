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
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class TriggeredEDRTemplateBean extends BaseBean<TriggeredEDRTemplate> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PricePlanMatrix} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private TriggeredEDRTemplateService triggeredEdrService;

	@Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public TriggeredEDRTemplateBean() {
		super(TriggeredEDRTemplate.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<TriggeredEDRTemplate> getPersistenceService() {
		return triggeredEdrService;
	}

	@Override
	protected String getListViewName() {
		return "triggeredEdrTemplates";
	}

	@Override
	public String getEditViewName() {
		return "triggeredEdrTemplateDetail";
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
    @ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		String result = super.saveOrUpdate(killConversation);
		ratingCacheContainerProvider.updateUsageChargeTemplateInCache(entity);
		return result;
	}
	
    public void duplicate() {
        if (entity == null || entity.getId() == null) {
            return;
        }
        entity = triggeredEdrService.refreshOrRetrieve(entity);

        // Detach and clear ids of entity and related entities
        triggeredEdrService.detach(entity);
        entity.setId(null);
        entity.setCode(entity.getCode() + "_copy");

        try {
            triggeredEdrService.create(entity, getCurrentUser());

        } catch (BusinessException e) {
            log.error("error when duplicate offer#{0}:#{1}", entity.getCode(), e);
        }
    }
}
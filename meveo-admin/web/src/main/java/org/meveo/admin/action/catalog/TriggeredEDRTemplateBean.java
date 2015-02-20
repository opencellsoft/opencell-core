/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;

@Named
@ConversationScoped
public class TriggeredEDRTemplateBean extends StatelessBaseBean<TriggeredEDRTemplate> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PricePlanMatrix} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private TriggeredEDRTemplateService triggeredEdrService;

	@Inject
	private UsageRatingService usageRatingService;
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public TriggeredEDRTemplateBean() {
		super(TriggeredEDRTemplate.class);
	}

	/**
	 * Override default list view name. (By default its class name starting
	 * lower case + 's').
	 * 
	 * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
	 */
	protected String getDefaultViewName() {
		return "triggeredEdrTemplates";
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
	public String saveOrUpdate(boolean killConversation) throws BusinessException{
		usageRatingService.updateTemplateCache(entity);
		return super.saveOrUpdate(killConversation);
	}

	@Override
   protected String saveOrUpdate(TriggeredEDRTemplate entity) throws BusinessException {
		usageRatingService.updateTemplateCache(entity);
		return super.saveOrUpdate(entity);
   }
}
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.base.PersistenceService;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

@Named
@ConversationScoped
public class OfferTemplateListBean extends OfferTemplateBean {

	private static final long serialVersionUID = -3037867704912788024L;

	private List<OfferTemplate> selectedOfferTemplates = new ArrayList<OfferTemplate>();

	public LazyDataModel<OfferTemplate> getLazyDataModelNoBSM() {
		filters.put("businessOfferModel", PersistenceService.SEARCH_IS_NULL);
		return getLazyDataModel(filters, listFiltered);
	}

	public void addForExport(OfferTemplate offerTemplate) {
		if (!selectedOfferTemplates.contains(offerTemplate)) {
			selectedOfferTemplates.add(offerTemplate);
		}
	}
	
	public void deleteForExport(OfferTemplate offerTemplate) {
		if (selectedOfferTemplates.contains(offerTemplate)) {
			selectedOfferTemplates.remove(offerTemplate);
		}
	}

	public void showSelectedOffersForExport() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("resizable", false);
		options.put("draggable", false);
		options.put("modal", true);
		RequestContext.getCurrentInstance().openDialog("selectedOffersForExport", options, null);
	}

	public long countActive() {
		return offerTemplateService.countActive();
	}

	public long countDisabled() {
		return offerTemplateService.countDisabled();
	}

	public long countExpiring() {
		return offerTemplateService.countExpiring();
	}

	public List<OfferTemplate> getSelectedOfferTemplates() {
		return selectedOfferTemplates;
	}

	public void setSelectedOfferTemplates(List<OfferTemplate> selectedOfferTemplates) {
		this.selectedOfferTemplates = selectedOfferTemplates;
	}
	
	public LazyDataModel<OfferTemplate> listFromBOM() {
		filters.clear();
		filters.put("businessOfferModel", PersistenceService.SEARCH_IS_NOT_NULL);
		
		return getLazyDataModel();
	}

}
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
package org.meveo.service.payments.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * OCCTemplate service implementation.
 * 
 *  @author anasseh
 *  @lastModifiedVersion 5.0
 */
@Stateless
public class OCCTemplateService extends BusinessService<OCCTemplate> {

	private static final String DUNNING_OCC_CODE = "bayad.dunning.occCode";
	private static final String DDREQUEST_OCC_CODE = "bayad.ddrequest.occCode";
		
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

	@SuppressWarnings("unchecked")
	public List<OCCTemplate> getListOccSortedByName() {
		log.debug("start of find list {} SortedByName ..", "OCCTemplate");
		QueryBuilder qb = new QueryBuilder(OCCTemplate.class, "c");
		qb.addOrderCriterion("description", true);
		List<OCCTemplate> occTemplates = (List<OCCTemplate>) qb.getQuery(getEntityManager()).getResultList();
		log.debug("start of find list {} SortedByName   result {}", new Object[] { "OCCTemplate", occTemplates == null ? "null" : occTemplates.size() });
		return occTemplates;
	}

	public OCCTemplate getDunningOCCTemplate() throws Exception {
		String occCodeDefaultValue = "INV_FEE";				
		return getOccTemplateByCFKeyOrProperty(DUNNING_OCC_CODE, occCodeDefaultValue);
	}

	public OCCTemplate getDirectDebitOCCTemplate() {				
		String occCodeDefaultValue = "DD_OCC";				
		return getOccTemplateByCFKeyOrProperty(DDREQUEST_OCC_CODE, occCodeDefaultValue);
	}

    private OCCTemplate getOccTemplateByCFKeyOrProperty(String occCodeKey, String occCodeDefaultValue) {

        try {
            String occTemplateCode = null;
            occTemplateCode = (String) customFieldInstanceService.getOrCreateCFValueFromParamValue(occCodeKey, occCodeDefaultValue, appProvider, true);
            return findByCode(occTemplateCode);

        } catch (Exception e) {
            log.error("error while getting occ template ", e);
            return null;
        }
    }
}

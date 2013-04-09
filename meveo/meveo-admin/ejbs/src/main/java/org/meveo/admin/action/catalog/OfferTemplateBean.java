/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link OfferTemplate} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Named
@ConversationScoped
public class OfferTemplateBean extends BaseBean<OfferTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OfferTemplate} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OfferTemplateService offerTemplateService;
    
    @Inject
    private ServiceTemplateService serviceTemplateService;
    
    private DualListModel<ServiceTemplate> perks;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    
	public DualListModel<ServiceTemplate> getDualListModel() {
		if (perks == null) {
			List<ServiceTemplate> perksSource = serviceTemplateService.list();
			List<ServiceTemplate> perksTarget = new ArrayList<ServiceTemplate>();
			if (getEntity().getCode() != null) {
				perksTarget.addAll(getEntity().getServiceTemplates());
			}
			perksSource.removeAll(perksTarget);
			perks = new DualListModel<ServiceTemplate>(perksSource, perksTarget);
		}
		return perks;
	}
    
    
    
    public OfferTemplateBean() {
        super(OfferTemplate.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Produces
    @Named("offerTemplate")
    public OfferTemplate init() {
        return initEntity();

    }


    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<OfferTemplate> getPersistenceService() {
        return offerTemplateService;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
     */
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("serviceTemplates");
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
     */
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("serviceTemplates");
    }
    
    @SuppressWarnings("unchecked")
	public void setDualListModel(DualListModel<ServiceTemplate> perks) {
		getEntity().setServiceTemplates((List<ServiceTemplate>) perks.getTarget());
	}

}

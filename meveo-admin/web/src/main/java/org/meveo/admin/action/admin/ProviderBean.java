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
package org.meveo.admin.action.admin;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.InterBankTitle;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class ProviderBean extends CustomFieldBean<Provider> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProviderService providerService;

    public ProviderBean() {
        super(Provider.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Provider> getPersistenceService() {
        return providerService;
    }

    @Override
    protected String getListViewName() {
        return "providers";
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    public Provider initEntity() {
        super.initEntity();
        if (entity.getId() != null && entity.getInvoiceConfiguration() == null) {
            InvoiceConfiguration invoiceConfiguration = new InvoiceConfiguration();
            invoiceConfiguration.setProvider(entity);
            entity.setInvoiceConfiguration(invoiceConfiguration);
        }
        if (entity.getBankCoordinates() == null) {
            entity.setBankCoordinates(new BankCoordinates());
        }
        if(entity.getInterBankTitle()==null){
        	entity.setInterBankTitle(new InterBankTitle());
        }
        return entity;
    }

    /**
     * Save or update provider.
     * 
     * @param entity Provider to save.
     * @throws BusinessException
     */
    @Override
    protected Provider saveOrUpdate(Provider entity) throws BusinessException {
        boolean isNew = entity.isTransient();
        if (isNew) {
            entity.getInvoiceConfiguration().setProvider(entity);
        }
        entity = super.saveOrUpdate(entity);
        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (!getCurrentUser().hasPermission("superAdmin", "superAdminManagement")) {
            super.saveOrUpdate(killConversation);
            messages.info(new BundleKey("messages", "update.successful"));
            return "providerSelfDetail";
        }
        String outcome = super.saveOrUpdate(killConversation);
        if (outcome != null) {
            return getEditViewName();
        }
        return null;
    }

}

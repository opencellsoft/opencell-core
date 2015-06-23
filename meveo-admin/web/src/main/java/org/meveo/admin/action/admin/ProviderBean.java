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
package org.meveo.admin.action.admin;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.billing.Language;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceConfigurationService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

@Named
@ViewScoped
public class ProviderBean extends BaseBean<Provider> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProviderService providerService;

    @Inject
    private UserService userService; 

    @Inject
    private RoleService roleService;
    
    @Inject
    private InvoiceConfigurationService invoiceConfigurationService;

    private static ParamBean paramBean = ParamBean.getInstance();

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

    public void onRowSelect(SelectEvent event) {
        if (event.getObject() instanceof Language) {
            Language language = (Language) event.getObject();
            log.info("populateLanguages language", language != null ? language.getLanguageCode() : null);
            if (language != null) {
                entity.setLanguage(language);
            }
        }

    }

    /**
     * Save or update provider.
     * 
     * @param entity Provider to save.
     * @throws BusinessException
     */
    @Override
    protected String saveOrUpdate(Provider entity) throws BusinessException {

        boolean isNew = entity.isTransient();
        String back = super.saveOrUpdate(entity);

        // Create a default role and a user
        if (isNew) {

            Role adminRole = roleService.findById(Long.parseLong(paramBean.getProperty("systgetEntityManager().adminRoleid", "1")));

            Role role = new Role();
            role.setName(adminRole.getName());
            role.setDescription(adminRole.getDescription());
            role.getPermissions().addAll(adminRole.getPermissions());

            role.setProvider(entity);
            roleService.create(role);

            User user = new User();
            user.setProvider(entity);
            user.setPassword(entity.getCode() + ".password");
            user.setUserName(entity.getCode() + ".ADMIN");
            user.getRoles().add(role);
            userService.create(user);
            log.info("created default user id={} for provider {}", user.getId(), entity.getCode());
            
            InvoiceConfiguration invoiceConfiguration =entity.getInvoiceConfiguration();
			invoiceConfiguration.setCode(entity.getCode()); 
			invoiceConfiguration.setDescription(entity.getDescription());   
	   		invoiceConfiguration.setProvider(entity);
	   		invoiceConfigurationService.create(invoiceConfiguration);
	   	    log.info("created invoiceConfiguration id={} for provider {}", invoiceConfiguration.getId(), entity.getCode());
	   	    
            messages.info(new BundleKey("messages", "provider.createdWithDefaultUser"), entity.getCode() + ".ADMIN", entity.getCode() + ".password");
        }

        return back;
    }
}

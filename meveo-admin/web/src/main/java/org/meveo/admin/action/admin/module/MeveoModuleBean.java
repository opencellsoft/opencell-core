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
package org.meveo.admin.action.admin.module;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.module.ModuleApi;
import org.meveo.model.module.MeveoModule;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Meveo module bean
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 */

@Named
@ViewScoped
public class MeveoModuleBean extends GenericModuleBean<MeveoModule> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link MeveoModule} service. Extends {@link PersistenceService}.
     */
    @Inject
    private MeveoModuleService meveoModuleService;

    @Inject
    private ModuleApi moduleApi;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public MeveoModuleBean() {
        super(MeveoModule.class);

    }

	/**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<MeveoModule> getPersistenceService() {
        return meveoModuleService;
    }

    public void install() {
        entity = install(entity);
    }

    public MeveoModule install(MeveoModule module) {
        try {

            if (!module.isDownloaded()) {
                return module;

            } else if (module.isInstalled()) {
                messages.warn(new BundleKey("messages", "meveoModule.installedAlready"));
                return module;
            }

            ModuleDto moduleDto = MeveoModuleService.moduleSourceToDto(module);

            module = moduleApi.install(moduleDto, currentUser);
            messages.info(new BundleKey("messages", "meveoModule.installSuccess"), moduleDto.getCode());

        } catch (Exception e) {
            log.error("Failed to install meveo module {} ", module.getCode(), e);
            messages.error(new BundleKey("messages", "meveoModule.installFailed"), module.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }

        return module;
    }

    public void uninstall() {
        try {

            if (!entity.isDownloaded()) {
                return;

            } else if (!entity.isInstalled()) {
                messages.warn(new BundleKey("messages", "meveoModule.notInstalled"));
                return;
            }

            entity = meveoModuleService.uninstall(entity, getCurrentUser());
            messages.info(new BundleKey("messages", "meveoModule.uninstallSuccess"), entity.getCode());

        } catch (Exception e) {
            log.error("Failed to uninstall meveo module {} ", entity.getCode(), e);
            messages.error(new BundleKey("messages", "meveoModule.uninstallFailed"), entity.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
    }

}
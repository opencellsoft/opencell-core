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
package org.meveo.admin.action.admin.module;

import java.util.ArrayList;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.util.pagination.EntityListDataModelPF;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.module.ModuleApi;
import org.meveo.service.admin.impl.MeveoModuleService;

@Named
@ConversationScoped
public class MeveoModuleListBean extends MeveoModuleBean {

	private static final long serialVersionUID = 1L;

	@Inject
	private ModuleApi moduleApi;
	@Inject
	private MeveoModuleService meveoModuleService;

	private ModuleDto selectedModuleDto;
	
	private EntityListDataModelPF<ModuleDto> moduleDtos = null;
	
	public EntityListDataModelPF<ModuleDto> getModuleDtos() {
		return moduleDtos;
	}

	public ModuleDto getSelectedModuleDto() {
		return selectedModuleDto;
	}

	public void setSelectedModuleDto(ModuleDto selectedModuleDto) {
		this.selectedModuleDto = selectedModuleDto;
	}
	public void initMeveoInstance() {
		log.debug("start initMeveoInstance");
		try {
			moduleDtos = new EntityListDataModelPF<ModuleDto>(new ArrayList<ModuleDto>());
	        moduleDtos.addAll(meveoModuleService.downloadModulesFromMeveoInstance(meveoInstance));
		}catch (Exception e) {
			log.error("Error when retrieve modules from {}. Reason {}", meveoInstance.getCode(),e.getMessage(),e);
			messages.error(new BundleKey("messages", "meveoModule.retrieveRemoteMeveoInstanceException"), meveoInstance.getCode(),e.getMessage());
			this.moduleDtos=null;
		}
	}

	public void installModule() {
		if (selectedModuleDto != null) {
			try {
				moduleApi.createOrUpdate(selectedModuleDto, currentUser);
				messages.info(new BundleKey("messages", "meveoModule.installSuccess"), selectedModuleDto.getCode());
			} catch (Exception e) {
				log.error("Error when create meveo module {} from meveoInstance {}", selectedModuleDto.getCode(),meveoInstance.getCode(), e);
				messages.error(new BundleKey("messages", "meveoModule.installFailed"), selectedModuleDto.getCode(),
						(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
			}
		}
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
}
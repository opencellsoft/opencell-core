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

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.module.ModuleApi;
import org.meveo.model.admin.MeveoModule;
import org.meveo.service.admin.impl.MeveoModuleService;

@Named
@ConversationScoped
public class MeveoModuleListBean extends MeveoModuleBean {

	private static final long serialVersionUID = 1L;

	@Inject
	private ModuleApi moduleApi;
	@Inject
	private MeveoModuleService meveoModuleService;

	private List<ModuleDto> moduleDtos=null;
	private ModuleDto selectedModuleDto;
	
	@Override
	public MeveoModule initEntity() {
		this.meveoInstance=null;
		this.moduleDtos=null;
		return super.initEntity();
	}

	public List<ModuleDto> getModuleDtos() {
		return this.moduleDtos;
	}

	public ModuleDto getSelectedModuleDto() {
		return selectedModuleDto;
	}

	public void setSelectedModuleDto(ModuleDto selectedModuleDto) {
		this.selectedModuleDto = selectedModuleDto;
		if(this.selectedModuleDto==null){
			this.moduleDtos=null;
		}
	}
	public void initMeveoInstance() {
		log.debug("start initMeveoInstance");
		try {
			this.moduleDtos = meveoModuleService.downloadModulesFromMeveoInstance(meveoInstance);
		}catch (Exception e) {
			log.error("Error when retrieve modules from {}. Reason {}", meveoInstance.getCode(),e.getMessage(),e);
			messages.error(new BundleKey("messages", "meveoModule.retrieveRemoteMeveoInstanceException"), meveoInstance.getCode(),e.getMessage());
			this.moduleDtos=null;
		}
	}

	public void installModule() {
		if (selectedModuleDto != null) {
			MeveoModule existed = meveoModuleService.findByCode(selectedModuleDto.getCode(), currentProvider);
			if (existed == null) {
				try {
					moduleApi.create(selectedModuleDto, currentUser);
					messages.info(new BundleKey("messages", "meveoModule.installSuccess"), selectedModuleDto.getCode());
				} catch (Exception e) {
					log.error("Error when create meveo module {} from meveoInstance {}", selectedModuleDto.getCode(),meveoInstance.getCode(), e);
					messages.error(new BundleKey("messages", "meveoModule.installFailed"), selectedModuleDto.getCode(),
							(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
				}
			} else {
				log.debug("Module {} from meveoInstance {} is existed at local",selectedModuleDto.getCode(),meveoInstance.getCode());
				messages.info(new BundleKey("messages", "meveoModule.installFailedExist"), selectedModuleDto.getCode());
			}
		}
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
}
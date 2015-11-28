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
package org.meveo.service.admin.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.module.ModuleDto;
import org.meveo.api.dto.module.ModuleItemDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.MeveoModuleItem;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.BusinessService;

@Stateless
public class MeveoModuleService extends BusinessService<MeveoModule> {

	public List<ModuleDto> downloadModulesFromMeveoInstance(MeveoInstance meveoInstance) throws MeveoApiException,RemoteAuthenticationException {
		List<ModuleDto> result = null;
		try {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(meveoInstance.getUrl()+(meveoInstance.getUrl().endsWith("/")?"":"/")+
					"api/rest/module/list");
			BasicAuthentication basicAuthentication=new BasicAuthentication(meveoInstance.getAuthUsername(),meveoInstance.getAuthPassword());
			target.register(basicAuthentication);
			
			Response response=target.request().get();
			if(response.getStatus()!=HttpURLConnection.HTTP_OK){
				if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
	                throw new RemoteAuthenticationException(response.getStatusInfo().getReasonPhrase());
	            } else {
	                throw new MeveoApiException("Failed to communicate to remote meveo instance. Http status " + response.getStatus() + " "
	                        + response.getStatusInfo().getReasonPhrase());
	            }
			}
			
			MeveoModuleDtosResponse resultDto=response.readEntity(MeveoModuleDtosResponse.class);
			log.debug("response {}",resultDto);
			if(ActionStatusEnum.SUCCESS!=resultDto.getActionStatus().getStatus().SUCCESS){
				throw new MeveoApiException("Fail to communicte "+meveoInstance.getCode()+". Error code "
						+resultDto.getActionStatus().getErrorCode()+", message "+resultDto.getActionStatus().getMessage());
			}
			result = resultDto.getModuleDtoList();
			if(result!=null){
				Collections.sort(result, new Comparator<ModuleDto>() {
					@Override
					public int compare(ModuleDto dto1, ModuleDto dto2) {
						return dto1.getCode().compareTo(dto2.getCode());
					}
				});
			}
			return result;
		} catch (Exception e) {
			log.error("Fail to communicate {}. Reason {}", meveoInstance.getCode(),(e==null?e.getClass().getSimpleName():e.getMessage()));
			throw new MeveoApiException("Failed to communicate "+meveoInstance.getCode()
				+". Error "+(e==null?e.getClass().getSimpleName():e.getMessage()));
		}
	}
	public void exportModule(MeveoModule entity,MeveoInstance meveoInstance) throws MeveoApiException,RemoteAuthenticationException{
		log.debug("export module {} to {}",entity,meveoInstance);
		if(meveoInstance!=null){
			try {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target(meveoInstance.getUrl()+(meveoInstance.getUrl().endsWith("/")?"":"/")+
						"api/rest/module");
				BasicAuthentication basicAuthentication=new BasicAuthentication(meveoInstance.getAuthPassword(),meveoInstance.getAuthPassword());
				target.register(basicAuthentication);
				
				ModuleDto moduleDto=new ModuleDto(entity.getCode(),entity.getDescription(),entity.getLicense(),entity.isDisabled());
				moduleDto.setModuleItems(new ArrayList<ModuleItemDto>());
				ModuleItemDto itemDto=null;
				for(MeveoModuleItem item:entity.getModuleItems()){
					itemDto=new ModuleItemDto(item.getItemCode(),item.getAppliesTo(),item.getItemType());
					moduleDto.getModuleItems().add(itemDto);
				}

	            Response response = target.request().post(Entity.entity(moduleDto, MediaType.APPLICATION_XML));
				if(response.getStatus()!=HttpURLConnection.HTTP_OK){
					if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
		                throw new RemoteAuthenticationException(response.getStatusInfo().getReasonPhrase());
		            } else {
		                throw new MeveoApiException("Failed to communicate to remote meveo instance "+meveoInstance.getCode()+
		                		". Http status "+response.getStatus() + " "+ response.getStatusInfo().getReasonPhrase());
		            }
				}
				ActionStatus actionStatus=response.readEntity(ActionStatus.class);
				log.debug("response {}",actionStatus);
				if(actionStatus!=null&&ActionStatusEnum.SUCCESS==actionStatus.getStatus()){
					throw new MeveoApiException("Fail to communicte "+meveoInstance.getCode()+". Error code "
							+actionStatus.getErrorCode()+", message "+actionStatus.getMessage());
				}
			} catch (Exception e) {
				log.error("Error when export module {} to {}. Error {}",entity.getCode(), 
						meveoInstance.getCode(),(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()),e);
				throw new MeveoApiException("Error code "
						+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
			}
		}
	}
}

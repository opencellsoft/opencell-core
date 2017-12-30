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
package org.meveo.service.communication.impl;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.communication.InboundCommunicationEvent;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.BusinessService;

/**
 * MeveoInstance service implementation.
 */
@Stateless
public class MeveoInstanceService extends BusinessService<MeveoInstance> {

	@Inject
	private Event<InboundCommunicationEvent> event;

	public MeveoInstance findByCode(String meveoInstanceCode) {
		QueryBuilder qb = new QueryBuilder(MeveoInstance.class, "c");
		qb.addCriterion("code", "=", meveoInstanceCode, true);
		
		try {
			return (MeveoInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("failed to find MeveoInstance", e.getMessage());
			return null;
		}
	}

	public void fireInboundCommunicationEvent(CommunicationRequestDto communicationRequestDto) {
		InboundCommunicationEvent inboundCommunicationEvent = new InboundCommunicationEvent();
		inboundCommunicationEvent.setCommunicationRequestDto(communicationRequestDto);
		event.fire(inboundCommunicationEvent);
	}
	
    /**
     * export module dto to remote meveo instance.
     * 
     * @param url url
     * @param meveoInstance meveo instance
     * @param dto base data transfer object
     * @return reponses
     * @throws BusinessException business exception.
     */
    public Response publishDto2MeveoInstance(String url, MeveoInstance meveoInstance, BaseDto dto) throws BusinessException {
        String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
        String username = meveoInstance.getAuthUsername() != null ? meveoInstance.getAuthUsername() : "";
        String password = meveoInstance.getAuthPassword() != null ? meveoInstance.getAuthPassword() : "";
        try {
            ResteasyClient client = new ResteasyClientBuilder().build();
            ResteasyWebTarget target = client.target(baseurl + url);
            BasicAuthentication basicAuthentication = new BasicAuthentication(username, password);
            target.register(basicAuthentication);

            Response response = target.request().post(Entity.entity(dto, MediaType.APPLICATION_XML));
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
                    throw new RemoteAuthenticationException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                } else {
                    throw new BusinessException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                }
            }
            return response;
        } catch (Exception e) {
            log.error("Failed to communicate {}. Reason {}", meveoInstance.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
            throw new BusinessException("Failed to communicate " + meveoInstance.getCode() + ". Error " + e.getMessage());
        }
    }

    /**
     * call String rest service to remote meveo instance.
     * 
     * @param url url
     * @param meveoInstanceCode meveo instance
     * @param body body of content to be sent.
     * @return reponse
     * @throws BusinessException business exception.
     */
  public Response callTextServiceMeveoInstance(String url, String meveoInstanceCode, String body) throws BusinessException {
	  MeveoInstance meveoInstance = findByCode(meveoInstanceCode);
	  return callTextServiceMeveoInstance(url,meveoInstance,body);
  }
  
  public Response callTextServiceMeveoInstance(String url, MeveoInstance meveoInstance, String body) throws BusinessException {
     String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
     String username = meveoInstance.getAuthUsername() != null ? meveoInstance.getAuthUsername() : "";
     String password = meveoInstance.getAuthPassword() != null ? meveoInstance.getAuthPassword() : "";
     try {
         ResteasyClient client = new ResteasyClientBuilder().build();
         ResteasyWebTarget target = client.target(baseurl + url);
         log.debug("call {} with body:{}",baseurl + url,body);
         BasicAuthentication basicAuthentication = new BasicAuthentication(username, password);
         target.register(basicAuthentication);

         Response response = target.request().post(Entity.entity(body, MediaType.APPLICATION_JSON));
         if (response.getStatus() != HttpURLConnection.HTTP_OK) {
             if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
                 throw new RemoteAuthenticationException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
             } else {
                 throw new BusinessException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
             }
         }
         return response;
     } catch (Exception e) {
         log.error("Failed to communicate {}. Reason {}", meveoInstance.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()), e);
         throw new BusinessException("Failed to communicate " + meveoInstance.getCode() + ". Error " + e.getMessage());
     }
 }

}

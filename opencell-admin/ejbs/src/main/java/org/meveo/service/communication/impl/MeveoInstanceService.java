/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.communication.impl;

import java.io.StringWriter;
import java.net.HttpURLConnection;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.jboss.resteasy.client.jaxrs.internal.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.CommunicateToRemoteInstanceException;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.commons.keystore.KeystoreManager;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ResteasyClientProxyBuilder;
import org.meveo.event.communication.InboundCommunicationEvent;
import org.meveo.export.RemoteAuthenticationException;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.BusinessService;

import com.slimpay.hapiclient.exception.HttpException;
import com.slimpay.hapiclient.exception.RelNotFoundException;

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
    public Response publishDto2MeveoInstance(String url, MeveoInstance meveoInstance, BaseEntityDto dto) throws BusinessException {
        String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
        String username = meveoInstance.getAuthUsername() != null ? meveoInstance.getAuthUsername() : "";
        String password = meveoInstance.getAuthPassword() != null ? meveoInstance.getAuthPassword() : "";
        try {
            ResteasyClient client = new ResteasyClientProxyBuilder().build();
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
     * export module dto to remote meveo instance.
     * 
     * @param url url
     * @param meveoInstance meveo instance
     * @param dto base data transfer object
     * @return reponses
     * @throws BusinessException business exception.
     * @throws HttpException 
     * @throws RelNotFoundException 
     * @throws OAuthSystemException 
     * @throws OAuthProblemException 
     * @throws JAXBException 
     */
    public OAuthResourceResponse publishDtoOAuth2MeveoInstance(String url, MeveoInstance meveoInstance, BaseEntityDto dto) throws BusinessException, RelNotFoundException, HttpException, OAuthSystemException, OAuthProblemException, JAXBException {
        
    	String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
    	
    	OAuthClientRequest lRequest = OAuthClientRequest
    	        .tokenLocation(meveoInstance.getUrl().split("/opencell")[0]+"/auth/realms/opencell/protocol/openid-connect/token")
    	        .setGrantType(GrantType.CLIENT_CREDENTIALS)
    	        .setClientId(meveoInstance.getClientId())
    	        .setClientSecret(meveoInstance.getClientSecret())
    	        .setScope("openid")
    	        .buildBodyMessage();
    	

    	OAuthClient client = new OAuthClient(new URLConnectionClient());
    	OAuthAccessTokenResponse response = client.accessToken(lRequest);
    	lRequest= new OAuthBearerClientRequest(baseurl + url).
                setAccessToken(response.getAccessToken()).buildQueryMessage();
    	
    	JAXBContext jaxbContext = JAXBContext.newInstance(MeveoModuleDto.class);
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	StringWriter sw = new StringWriter();
    	jaxbMarshaller.marshal(dto, sw);
    	String xmlString = sw.toString();
    	lRequest.setBody(xmlString);
    	
    	lRequest.setHeader(OAuth.HeaderType.CONTENT_TYPE, "application/xml");
    	
        OAuthResourceResponse resourceResponse= client.resource(lRequest, OAuth.HttpMethod.POST, OAuthResourceResponse.class);
    	
        log.debug("Publication response {}",resourceResponse.getResponseCode());     
        
        return resourceResponse;
    }

    /**
     * call String rest service to remote meveo instance.
     * 
     * @param url url
     * @param meveoInstanceCode meveo instance
     * @param body body of content to be sent.
     * @return reponse HTTP response
     * @throws BusinessException business exception.
     */
    public Response callTextServiceMeveoInstance(String url, String meveoInstanceCode, String body) throws CommunicateToRemoteInstanceException {
        MeveoInstance meveoInstance = findByCode(meveoInstanceCode);
        return callTextServiceMeveoInstance(url, meveoInstance, body);
    }

    public Response callTextServiceMeveoInstance(String url, MeveoInstance meveoInstance, String body) throws CommunicateToRemoteInstanceException {
        String baseurl = meveoInstance.getUrl().endsWith("/") ? meveoInstance.getUrl() : meveoInstance.getUrl() + "/";
        String username = meveoInstance.getAuthUsername() != null ? meveoInstance.getAuthUsername() : "";
        String password = meveoInstance.getAuthPassword() != null ? meveoInstance.getAuthPassword() : "";
        try {
            ResteasyClient client = new ResteasyClientProxyBuilder().build();
            ResteasyWebTarget target = client.target(baseurl + url);
            log.debug("call {} with body:{}", (baseurl + url), body);
            BasicAuthentication basicAuthentication = new BasicAuthentication(username, password);
            target.register(basicAuthentication);

            Response response = target.request().post(Entity.entity(body, MediaType.APPLICATION_JSON));
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                if (response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED || response.getStatus() == HttpURLConnection.HTTP_FORBIDDEN) {
                    throw new RemoteAuthenticationException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                } else {
                    throw new CommunicateToRemoteInstanceException("Http status " + response.getStatus() + ", info " + response.getStatusInfo().getReasonPhrase());
                }
            }
            return response;

        } catch (Exception e) {
            throw new CommunicateToRemoteInstanceException(meveoInstance.getCode(), e);
        }
    }

    @Override
    public void remove(MeveoInstance meveoInstance) {
        // remove credential of opencellInstance in the keystore
    	if(KeystoreManager.existKeystore()) {
    		KeystoreManager.removeCredential(meveoInstance.getClass().getSimpleName() + "." + meveoInstance.getId());
    	}

        super.remove(meveoInstance);
    }
}

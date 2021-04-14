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

package org.meveo.client;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiExecutor {
	
	private static final Logger log = LoggerFactory.getLogger(ApiExecutor.class);

    /**
     * 
     * @param api api to be executed
     * @param url url to call
     * @param userName user name
     * @param password password
     * @param method method to be invoked
     * @param cotentType content type
     * @param headers map of header
     * @param params map of parametter
     * @param body body of reponse
     * @param authMode authentication method
     * @param proxyInfos proxy infos.
     * @param tlsVersion TLSv1.1 or TLSv1.2 ...
     * @return http reponse.
     */
	public HttpResponse executeApi(String api, String url, String userName, String password, HttpMethodsEnum method, CommonContentTypeEnum cotentType, Map<String, String> headers, 
			Map<String, String> params, String body, AuthentificationModeEnum authMode ,ProxyInfos proxyInfos,String tlsVersion) {
		try {
			
			URI uri = new URI(url  + (api==null?"":api));			
			uri = addParamsToUri( uri, params);

			if (HttpMethodsEnum.POST == method) {
				return executePost(uri, userName, password, cotentType, headers, body,params, authMode,proxyInfos,tlsVersion);
			}

			if (HttpMethodsEnum.GET == method) {
				return executeGet(uri, userName, password, cotentType, headers, params, authMode,proxyInfos,tlsVersion);
			}

		} catch (Exception e) {
			log.error("error = {}", e);
		}
		return null;
	}

	private HttpResponse executePost(URI uri, String userName, String password, CommonContentTypeEnum cotentType, Map<String, String> headers, String body,Map<String, String> params,
			AuthentificationModeEnum authMode ,ProxyInfos proxyInfos,String tlsVersion) {
		try {
			
			
			HttpPost theRequest = new HttpPost(uri);
			if (cotentType != null) {
				theRequest.setHeader("Content-Type", cotentType.getValue());
			}
			if (AuthentificationModeEnum.BASIC == authMode) {
				theRequest.setHeader("Authorization", getBasicAuthentication(userName,password));
			}
			if (headers != null) {
				for (String key : headers.keySet()) {
					theRequest.setHeader(key, headers.get(key));
				}
			}
			if (body != null) {
				HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
				theRequest.setEntity(entity);
			}
			HttpResponse response = MeveoConnectionFactory.getClient(proxyInfos,tlsVersion).execute(theRequest);			
			if(response != null){
				log.info("executePost code :" + response.getStatusLine().getStatusCode());
			}
			return response;
		} catch (Exception e) {

			log.error("error = {}", e);

		}
		return null;
	}

	private HttpResponse executeGet(URI uri, String userName, String password, CommonContentTypeEnum cotentType, Map<String, String> headers, Map<String, String> params, 
			AuthentificationModeEnum authMode,ProxyInfos proxyInfos,String tlsVersion) {
		try {			
			
			HttpGet theRequest = new HttpGet(uri);
			if (cotentType != null) {
				theRequest.setHeader("Content-Type", cotentType.getValue());
			}
			if (AuthentificationModeEnum.BASIC == authMode) {
				theRequest.setHeader("Authorization", getBasicAuthentication(userName,password));
			}
			if (headers != null) {
				for (String key : headers.keySet()) {
					theRequest.setHeader(key, headers.get(key));
				}
			}

			HttpResponse response = MeveoConnectionFactory.getClient(proxyInfos,tlsVersion).execute(theRequest);
			if(response != null){
				log.info("executeGet code :" + response.getStatusLine().getStatusCode());
			}
			
			return response;
		} catch (Exception e) {

			log.error("error = {}", e);

		}
		return null;
	}

	/**
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	private String getBasicAuthentication(String login, String password) {
		String authentification = login + ":" + password;
		byte[] authEncoded = Base64.encodeBase64(authentification.getBytes());
		return "Basic " + (new String(authEncoded));
	}

	/**
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */
	private URI addParamsToUri(URI uri,Map<String,String> params){
		if( params != null ){			
			UriBuilder uriBuilder = UriBuilder.fromUri(uri);	
			for(Entry<String, String> entry : params.entrySet()){
				uriBuilder = uriBuilder.queryParam(entry.getKey(), entry.getValue());
			}
			uri = uriBuilder.build().normalize();
		}
		return uri;
	}
}

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

package org.meveo.service.notification;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

@SuppressWarnings("deprecation")
@Stateless
public class RemoteInstanceNotifier {

	@Inject
	private Logger log;
	
	public void invoke(String input,String url) {
		//TODO: find a better way to send logs
		/*
		try {
			log.debug("Request  ={}",input);
			log.debug("Url ={}",url);
			ClientRequest request = new ClientRequest(url);			
			JSONParser jsonParser =  new JSONParser();		
			request.body("application/json", jsonParser.parse(input));
			request.accept("application/json");

			ClientResponse<String> response = request.post(String.class);
			String jsonResponse = "";
			if (response.getStatus() != 201) {
				log.debug("invoke Failed : HTTP error code : "+ response.getStatus());
			} else {    
				jsonResponse=response.getEntity();
				log.info("Response jsonResponse ={}",jsonResponse);				
				JSONObject jsonResponseObject = (JSONObject) jsonParser.parse(jsonResponse);
				JSONObject jsonActionStatus =  (JSONObject) jsonResponseObject.get("actionStatus");
				String responseStatus  = (String) jsonActionStatus.get("status");
				
				if("SUCCESS".equals(responseStatus)){
					log.debug("invoke remote service ok");
				}else{
					log.debug("invoke remote service fail");
				}
			}
			
		} catch (Exception e) {
			log.error("Exception on invoke : ",e);
			
		}
		*/
	}
	
	//TODO use this  (no deprecation ), also for checkUpdate
//	public void invoke(String input,String url) {
//		Response response =null;
//		try {		
//			log.debug("Request Check Update ={}",input);
//			log.debug("Request Check Update url={}",url);
//			Client client = ClientBuilder.newClient();
//			response = client.target(url).request().post(Entity.entity(input, MediaType.APPLICATION_JSON));
//			String value = response.readEntity(String.class);          
//			if (response.getStatus() != 201) {
//				log.debug("ChekUpdate Failed : HTTP error code : "+ response.getStatus());
//			} else {    
//				log.debug("ChekUpdate ok, response: "+ value);
//			}
//		} catch (Exception e) {
//			log.error("Exception on getVersionOutput : ",e);
//
//		}finally{
//			if(response != null){
//				response.close();
//			}
//		}
//	}
}

package org.meveo.service.notification;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;

@Stateless
public class RestNotifier {

	@Inject
	private Logger log;
	
	
	
	public void checkVersion(String input,String urlMoni) {
		try {
			
			log.debug("Request Check Update ={}",input);

			log.debug("Request Check Update url={}",urlMoni);

			//FIXME : deprecated
			ClientRequest request = new ClientRequest(urlMoni);
			request.body("application/json", input);
			request.accept("application/json");

			ClientResponse<String> response = request.post(String.class);
			String jsonResponse = "";
			if (response.getStatus() != 201) {
				log.debug("ChekUpdate Failed : HTTP error code : "+ response.getStatus());
			} else {    
				String tmp=null;
				try(BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes()))))
				{  
					while ((tmp = br.readLine())!= null) {
						jsonResponse+=tmp ;
					}
				} 
			}

			log.info("Response jsonResponse ={}",jsonResponse);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonResponseObject = (JSONObject) jsonParser.parse(jsonResponse);
			JSONObject jsonActionStatus =  (JSONObject) jsonResponseObject.get("actionStatus");
			String responseStatus  =(String) jsonActionStatus.get("status");
			Boolean newVersion  =(Boolean) jsonResponseObject.get("newVersion");
			if("SUCCESS".equals(responseStatus)){
				if(newVersion.booleanValue()){ 
					JSONObject jsonVersionObjectDto =  (JSONObject) jsonResponseObject.get("versionObjectDto");
					//versionOutput = (String)jsonVersionObjectDto.get("htmlContent");
				//	log.info("there's a NEW version  ={}",versionOutput);
				}else{
					log.debug("there is NO new version");
				}
			}else{
				log.debug("checkVersion remote service fail");
			}


		} catch (Exception e) {
			log.error("Exception on getVersionOutput : ",e);
			
		}
	}
	public void toto(String input,String url) {
		Response response =null;
		try {		
			log.debug("Request Check Update ={}",input);
			log.debug("Request Check Update url={}",url);
			Client client = ClientBuilder.newClient();
			response = client.target(url).request().post(Entity.entity(input, MediaType.APPLICATION_JSON));
			String value = response.readEntity(String.class);          
			if (response.getStatus() != 201) {
				log.debug("ChekUpdate Failed : HTTP error code : "+ response.getStatus());
			} else {    
				log.debug("ChekUpdate ok, response: "+ value);
			}
		} catch (Exception e) {
			log.error("Exception on getVersionOutput : ",e);

		}finally{
			if(response != null){
				response.close();
			}
		}
	}
}

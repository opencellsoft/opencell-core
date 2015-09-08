package org.meveo.admin.action;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.vfs.FileSystemException;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.util.Version;
import org.slf4j.LoggerFactory;

@Named
public class MappingDocBean implements Serializable {

	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;



	private ParamBean paramBean = ParamBean.getInstance();
	
	@Inject
	private MeveoInstanceService meveoInstanceService;

	private String OutputURL="";
	

	public String docMapper() {
		try {			
			String input = buildJsonRequest();
			log.debug("Request Documentation url ={}",input);

			String urlDoc = paramBean.getProperty("documentation.url","http://version.meveo.info/meveo-moni")+"/api/rest/documentation";
			log.debug("Request Documentation url={}",urlDoc);
			JSONParser jsonParser = new JSONParser();
			//FIXME : deprecated
			ClientRequest request = new ClientRequest(urlDoc);
			request.body("application/json", jsonParser.parse(input));
			request.accept("application/json");

			ClientResponse<String> response = request.post(String.class);
			String jsonResponse = "";
			
			
			if (response.getStatus() != 201) {
				log.debug("Documentation Mapping Failed : HTTP error code : "+ response.getStatus());
			} else {    
				jsonResponse=response.getEntity();
				log.debug("Documentation mapping reponse : "+ jsonResponse);				
				JSONObject jsonMappingObject = (JSONObject) jsonParser.parse(jsonResponse);
				String destinationPage = (String) jsonMappingObject.get("destinationPage");
				String URLWiki = (String) jsonMappingObject.get("URLWiki");
				OutputURL = URLWiki+"/"+ destinationPage;
				
				
				log.debug("Documentation Link  ={}",OutputURL);
				return OutputURL;
			    
				
			}
		} catch (Exception e) {
			log.error("Exception on getOutputUrl : ",e);
			OutputURL="error";
			
		}
		return OutputURL;
	}

	public String getOutputURL() {
		return OutputURL;
	}

	public void setOutputURL(String OutputURL) {
		this.OutputURL = OutputURL;
	}

	private String buildJsonRequest(){
		try{
			
			MeveoInstance meveoInstance = meveoInstanceService.getThis();
			FacesContext ctx = FacesContext.getCurrentInstance();
			HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
			String fullURI = servletRequest.getRequestURI();
			log.debug(fullURI);
			
			String macAddress="";
			String currentPage="";
			String destinationPage="";
			String productVersion = Version.appVersion;	
			try{				
				byte[] mac  = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
				}
				macAddress = sb.toString() ;
				
			}catch(Exception e){
				macAddress="error:"+e;
			}

			/*
			 * Dont add any fields in this json object
			 * if needed , so add idd it first in the remote service
			 */
			String input = "{"+
					"	  #currentPage#: #"+currentPage+"#,"+
					"	  #productVersion#: #"+productVersion+"#,"+
					"	  #macAddress#: #"+macAddress+"#,"+
					"	  #productVersion#: #"+meveoInstance+"#,"+
					"}";
			input = input.replaceAll("#", "\"");
			return input;

		}catch(Exception e){
			log.error("Exception on buildJsonRequest: ",e);
		}
		return "";

	}
	
}

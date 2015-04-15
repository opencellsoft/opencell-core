package org.meveo.admin.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.meveo.commons.utils.ParamBean;

/*
 * 
 *         ***********************************
 *              
					D R A F T
					
 *         ***********************************
 */

@Named
@ConversationScoped
public class CheckUpdateBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private org.slf4j.Logger log;

	private ParamBean paramBean = ParamBean.getInstance();

	public void doIt() {

		try {

			ClientRequest request = new ClientRequest(paramBean.getProperty(
					"url.update", "http://localhost:8080/mum/rest/getVersion"));
			request.accept("application/json");

			String input = "{		  \"productName\": \"Meveo\",		  \"productVersion\": \"4.0.2\",		  \"owner\": \"anasseh sarl\",		  \"productInfo\": {		"
					+ "				    \"md5\": \"067d9f8ecd6ff3981e2764e50846da5f\",						    \"creationDate\": \"2015-04-17T11:23:10\",						 "
					+ "   \"updateDate\": \"2015-04-17T11:23:10\",						    \"firstVersion\": \"1.02\"		  				},		"
					+ "  \"machineInfo\": {						    \"macAddress\": \"28:cf:e9:18:cc:13\",						    \"vendor\": \"Apple\",					"
					+ "	    \"installationMode\":\"Std\"		  				},		  \"machinePhysicalInfo\": {						    \"nbCores\": \"4\",	"
					+ "					    \"memory\": \"16 Go\",						    \"hdSize\": \"500 Go\"		  				},		  	"
					+ "					  						  \"machineSoftwareInfo\": {						    \"osName\": \"String\",				"
					+ "		    \"osVersion\": \"10.10.2\",						    \"javaVendor\": \"Oracle\",						    \"javaVersion\": \"1.7.0_40\",	"
					+ "					    \"asName\": \"Jboss\",						    \"asVersion\": \"eap-6.2\"		  				}				  }";
			request.body("application/json", input);

			ClientResponse<String> response = request.post(String.class);

			if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			HttpServletResponse response2 = (HttpServletResponse) externalContext
					.getResponse();

			PrintWriter pw = response2.getWriter();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(response.getEntity().getBytes())));

			String output = "";

			while ((output) != null) {
				pw.write(output);
				System.out.println("Output from Server .... :" + output);
			}
			
			br.close();

			FacesContext.getCurrentInstance().responseComplete();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}

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

@Named
@ConversationScoped
public class CheckUpdateBean implements Serializable {


	private static final long serialVersionUID = 1L;

	@Inject
	private org.slf4j.Logger log;

	private ParamBean paramBean = ParamBean.getInstance();

	public String doIt() {
		try {
			
			String productVersion = paramBean.getProperty("checkUpdate.productVersion", "4.1");
			String productName = paramBean.getProperty("checkUpdate.productName", "Meveo");
			String owner = paramBean.getProperty("checkUpdate.owner", "OpenCell");
			String macAddress = paramBean.getProperty("checkUpdate.macAddress", "aa:zz:ee:rr:tt:yy");
		
			ClientRequest request = new ClientRequest(paramBean.getProperty("checkUpdate.url", "http://localhost:8080/meveo-moni/api/rest/getVersion"));
			
			String input = "{ \"productName\": \""+productName+"\","+
					       "  \"productVersion\": \""+productVersion+"\","+
						   "  \"owner\": \""+owner+"\",	"+								
					       "  \"machineInfo\": { \"macAddress\": \""+macAddress+"\"}	"+
					       "}";
			
			request.body("application/json", input);
			request.accept("application/json");
			
			ClientResponse<String> response = request.post(String.class);
			if (response.getStatus() != 201) {
				throw new RuntimeException("ChekUpdate Failed : HTTP error code : "+ response.getStatus());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getEntity().getBytes())));
			String output = "",tmp=null;
			while ((tmp = br.readLine())!= null) {
				output+=tmp ;
			}
			
			br.close();
			return output;
			
		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;
	}

}

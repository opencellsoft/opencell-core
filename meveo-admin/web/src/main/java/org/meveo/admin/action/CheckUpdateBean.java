package org.meveo.admin.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Map;

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
			byte[] mac  = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			
			 StringBuilder sb = new StringBuilder();
		        for (int i = 0; i < mac.length; i++) {
		            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
		        }
			
			String productVersion = "4.0.2";
			String productName = paramBean.getProperty("checkUpdate.productName", "Meveo");
			String owner = paramBean.getProperty("checkUpdate.owner", "OpenCell");
			String macAddress = sb.toString() ;
			String md5="";
			String creationDate="";
			String updateDate="";
			String keyEntreprise="";
			String machineVendor= "";
			String installationMode="";
			
			String nbCores="";
			String memory="";
			String hdSize="";
			
			String osName = System.getProperty("os.name");
			String osVersion = System.getProperty("os.version");
			String osArch = System.getProperty("os.arch");
			String javaVendor = System.getProperty("java.vendor");
			String  javaVmVersion = System.getProperty("java.vm.version");
			String  javaVmName = System.getProperty("java.vm.name");
			String  javaSpecVersion = System.getProperty("java.runtime.version");
			String  asName = System.getProperty("program.name");
			String asVersion = System.getProperty("program.name");
			
			String urlMoni = paramBean.getProperty("checkUpdate.url", "http://version.meveo.info/meveo-moni/api/rest/getVersion");
			
			ClientRequest request = new ClientRequest(urlMoni);
			
			log.info("Requet Check Update url={}",urlMoni);
			String input = "{"+
					"	  #productName#: #"+productName+"#,"+
					"	  #productVersion#: #"+productVersion+"#,"+
					"	  #owner#: #"+owner+"#,"+
					"	  #productInfo#: {"+
					"					    #md5#: #"+md5+"#,"+
					"					    #creationDate#: #"+creationDate+"#,"+
					"					    #updateDate#: #"+updateDate+"#,"+
					"					    #keyEntreprise#: #"+keyEntreprise+"#"+					
					"	  				},"+
					"	  #machineInfo#: {"+
					"					    #macAddress#: #"+macAddress+"#,"+
					"					    #vendor#: #"+machineVendor+"#,"+
					"					    #installationMode#:#"+installationMode+"#"+
					"	  				},"+
					"	  #machinePhysicalInfo#: {"+
					"					    #nbCores#: #"+nbCores+"#,"+
					"					    #memory#: #"+memory+"#,"+
					"					    #hdSize#: #"+hdSize+"#"+
					"	  				},"+		  						  				
					"	  #machineSoftwareInfo#: {"+
					"					    #osName#: #"+osName+"#,"+
					"					    #osVersion#: #"+osVersion+"#,"+
					"					    #osArch#: #"+osArch+"#,"+					
					"					    #javaVendor#: #"+javaVendor+"#,"+
					"					    #javaVersion#: #"+javaSpecVersion+"#,"+
					"					    #javaVmVersion#: #"+javaVmVersion+"#,"+
					"					    #javaVmName#: #"+javaVmName+"#,"+
					"					    #asName#: #"+asName+"#,"+
					"					    #asVersion#: #"+asVersion+"#"+					
					"	  				}"+			
					"}";
			
			input = input.replaceAll("#", "\"");
			log.info("Requet Check Update ={}",input);
			
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

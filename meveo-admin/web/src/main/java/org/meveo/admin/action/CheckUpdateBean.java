package org.meveo.admin.action;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

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

@SuppressWarnings("deprecation")
@Named
@SessionScoped
public class CheckUpdateBean implements Serializable {


	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;



	private ParamBean paramBean = ParamBean.getInstance();
	
	@Inject
	private MeveoInstanceService meveoInstanceService;

	private String versionOutput="";

	public void checkVersion() {
		try {			
			String input = buildJsonRequest();
			log.debug("Request Check Update ={}",input);

			String urlMoni = paramBean.getProperty("checkUpdate.url","http://version.meveo.info/meveo-moni/api/rest/getVersion");
			log.debug("Request Check Update url={}",urlMoni);
			JSONParser jsonParser = new JSONParser();
			//FIXME : deprecated
			ClientRequest request = new ClientRequest(urlMoni);
			request.body("application/json", jsonParser.parse(input));
			request.accept("application/json");

			ClientResponse<String> response = request.post(String.class);
			String jsonResponse = "";
			if (response.getStatus() != 201) {
				log.debug("ChekUpdate Failed : HTTP error code : "+ response.getStatus());
			} else {    
				jsonResponse=response.getEntity();
				log.debug("ChekUpdate reponse : "+ jsonResponse);				
				JSONObject jsonResponseObject = (JSONObject) jsonParser.parse(jsonResponse);
				JSONObject jsonActionStatus =  (JSONObject) jsonResponseObject.get("actionStatus");
				String responseStatus  =(String) jsonActionStatus.get("status");
				Boolean newVersion  =(Boolean) jsonResponseObject.get("newVersion");
				if("SUCCESS".equals(responseStatus)){
					if(newVersion.booleanValue()){ 
						JSONObject jsonVersionObjectDto =  (JSONObject) jsonResponseObject.get("versionObjectDto");
						versionOutput = (String)jsonVersionObjectDto.get("htmlContent");
						log.debug("there's a NEW version  ={}",versionOutput);
					}else{
						log.debug("there is NO new version");
					}
				}else{
					log.debug("checkVersion remote service fail");
				}
			}
		} catch (Exception e) {
			log.error("Exception on getVersionOutput : ",e);
			versionOutput="-";
		}
	}

	public String getVersionOutput() {
		return versionOutput;
	}

	public void setVersionOutput(String versionOutput) {
		this.versionOutput = versionOutput;
	}

	private String buildJsonRequest(){
		try{
			
			MeveoInstance meveoInstance = meveoInstanceService.getThis();
			String  meveoInstanceCode = (meveoInstance==null )?null:meveoInstance.getCode();
			 		
			String productVersion = Version.appVersion;			
			String productName = paramBean.getProperty("checkUpdate.productName", "Meveo");
			String owner = paramBean.getProperty("checkUpdate.owner", "OpenCell");
			String macAddress = "";
			String ipAddress = "";
			String md5="";
			String creationDate= Version.build_time;
			String updateDate="";
			String keyEntreprise="";
			String machineVendor= "";
			String installationMode="";
			try{
				ipAddress = Inet4Address.getLocalHost().getHostAddress();
				byte[] mac  = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
				}
				macAddress = sb.toString() ;
				
			}catch(Exception e){
				macAddress="error:"+e;
			}

			Runtime runtime = Runtime.getRuntime();
			String nbCores=""+runtime.availableProcessors();
			String memory=runtime.freeMemory()+";"+runtime.totalMemory()+";"+runtime.maxMemory();
			String hdSize="";

			for (Path root : FileSystems.getDefault().getRootDirectories())
			{
				try
				{
					FileStore store = Files.getFileStore(root);
					hdSize=store.getUsableSpace()+";"+store.getTotalSpace();
				}
				catch (FileSystemException e)
				{
					hdSize="error:"+e;
				}
			}

			String osName = System.getProperty("os.name");
			String osVersion = System.getProperty("os.version");
			String osArch = System.getProperty("os.arch");
			String javaVendor = System.getProperty("java.vendor"); 
			String javaVmVersion = System.getProperty("java.vm.version");
			String javaVmName = System.getProperty("java.vm.name");
			String javaSpecVersion = System.getProperty("java.runtime.version");
			String asName = System.getProperty("program.name");
			String asVersion = System.getProperty("program.name");


			/*
			 * Dont add any fields in this json object
			 * if needed , so add idd it first in the remote service
			 */
			String input = "{"+
					"	  #meveoInstanceCode#: #"+meveoInstanceCode+"#,"+
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
					"					    #ipAddress#: #"+ipAddress+"#,"+					
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
			return input;

		}catch(Exception e){
			log.error("Exception on buildJsonRequest: ",e);
		}
		return null;

	}
}

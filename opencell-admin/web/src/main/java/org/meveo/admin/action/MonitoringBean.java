package org.meveo.admin.action;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.FileStore;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.util.Version;
import org.slf4j.LoggerFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 *
 */
@SuppressWarnings("deprecation")
@Named
public class MonitoringBean implements Serializable {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = 1L;
    private static String versionOutput = null;
    private static Date lastVersionCheckDate = new Date();
    private static Date startupDate = new Date();
    private static Map<String, String> docUrlMap = new HashMap<>();

    private static String macAddress = null;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

    public static String getMacAddress() {
        if (macAddress == null) {
            String firstInterface = null;
            Map<String, String> addressByNetwork = new HashMap<>();
            Enumeration<NetworkInterface> networkInterfaces = null;
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e1) {
            }
            if (networkInterfaces != null) {
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface network = networkInterfaces.nextElement();
                    byte[] bmac = null;
                    try {
                        bmac = network.getHardwareAddress();
                    } catch (SocketException e) {
                    }
                    if (bmac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < bmac.length; i++) {
                            sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
                        }

                        if (sb.toString().isEmpty() == false) {
                            addressByNetwork.put(network.getName(), sb.toString());
                            // System.out.println("Address = "+sb.toString()+" @ ["+network.getName()+"] "+network.getDisplayName());
                        }

                        if (sb.toString().isEmpty() == false && firstInterface == null) {
                            firstInterface = network.getName();
                        }
                    }
                }
            }
            if (firstInterface != null) {
                macAddress = addressByNetwork.get(firstInterface);
            } else {
                macAddress = "NOT_FOUND";
            }
        }
        return macAddress;
    }

    public JSONObject sendCommand(String url, String content) {
        JSONObject result = null;
        try {
            JSONParser jsonParser = new JSONParser();
            ClientRequest request = new ClientRequest(url);
            request.body("application/json", jsonParser.parse(content));
            request.accept("application/json");

            ClientResponse<String> response = request.post(String.class);
            String jsonResponse = "";
            if (response.getStatus() != 201) {
                log.debug("command {} to url {} Failed : HTTP error code : {}", content, url, response.getStatus());
            } else {
                jsonResponse = response.getEntity();
                log.debug("command reponse : " + jsonResponse);
                result = (JSONObject) jsonParser.parse(jsonResponse);
            }
        } catch (Exception e) {
            log.debug("parsing error", e);
        }
        return result;
    }

    public String getLastVersion() {
        String result = versionOutput;
        long age = (System.currentTimeMillis() - lastVersionCheckDate.getTime());
        // log.debug("getLastVersion versionOutput={} age={}",versionOutput,age);
        if (versionOutput != null) {
            if (age > 3600000l) {
                versionOutput = null;
            }
            return result;
        } else {
            versionOutput = "";
            lastVersionCheckDate = new Date();
            try {
                ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
                String meveoInstanceCode = paramBeanFactory.getInstance().getProperty("monitoring.instanceCode", "");
                String productVersion = Version.appVersion;
                String macAddress = getMacAddress();
                String md5 = "";
                String creationDate = Version.build_time;
                String updateDate = sdf.format(startupDate);
                String keyEntreprise = paramBeanFactory.getInstance().getProperty("monitoring.enterpriseKey", "");

                Runtime runtime = Runtime.getRuntime();
                String nbCores = "" + runtime.availableProcessors();
                String memory = runtime.freeMemory() + ";" + runtime.totalMemory() + ";" + runtime.maxMemory();
                String hdSize = "";

                for (Path root : FileSystems.getDefault().getRootDirectories()) {
                    try {
                        FileStore store = Files.getFileStore(root);
                        hdSize = store.getUsableSpace() + ";" + store.getTotalSpace();
                    } catch (FileSystemException e) {
                        hdSize = "error:" + e;
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
                String asVersion = System.getProperty("program.version");

                /*
                 * Dont add any fields in this json object if needed , so add idd it first in the remote service
                 */
                String input = "{" + "	  #meveoInstanceCode#: #" + meveoInstanceCode + "#," + "	  #productName#: #" + "Meveo" + "#," + "	  #productVersion#: #"
                        + productVersion + "#," + "	  #owner#: #" + "OpenCell" + "#," + "	  #productInfo#: {" + "					    #md5#: #" + md5 + "#,"
                        + "					    #creationDate#: #" + creationDate + "#," + "					    #updateDate#: #" + updateDate + "#,"
                        + "					    #keyEntreprise#: #" + keyEntreprise + "#" + "	  				}," + "	  #machineInfo#: {" + "					    #macAddress#: #"
                        + macAddress + "#," + "					    #ipAddress#: ##," + "	  				}," + "	  #machinePhysicalInfo#: {"
                        + "					    #nbCores#: #" + nbCores + "#," + "					    #memory#: #" + memory + "#," + "					    #hdSize#: #"
                        + hdSize + "#" + "	  				}," + "	  #machineSoftwareInfo#: {" + "					    #osName#: #" + osName + "#,"
                        + "					    #osVersion#: #" + osVersion + "#," + "					    #osArch#: #" + osArch + "#," + "					    #javaVendor#: #"
                        + javaVendor + "#," + "					    #javaVersion#: #" + javaSpecVersion + "#," + "					    #javaVmVersion#: #" + javaVmVersion + "#,"
                        + "					    #javaVmName#: #" + javaVmName + "#," + "					    #asName#: #" + asName + "#,"
                        + "					    #asVersion#: #" + asVersion + "#" + "	  				}" + "}";
                input = input.replaceAll("#", "\"");
                // log.debug("Request Check Update ={}",input);

                String urlMoni = paramBeanFactory.getInstance().getProperty("monitoring.url", "http://version.meveo.info/meveo-moni") + "/api/rest/getVersion";
                JSONObject jsonResponseObject = sendCommand(urlMoni, input);
                JSONObject jsonActionStatus = (JSONObject) jsonResponseObject.get("actionStatus");
                String responseStatus = (String) jsonActionStatus.get("status");
                Boolean newVersion = (Boolean) jsonResponseObject.get("newVersion");
                if ("SUCCESS".equals(responseStatus) && newVersion.booleanValue()) {
                    JSONObject jsonVersionObjectDto = (JSONObject) jsonResponseObject.get("versionObjectDto");
                    versionOutput = (String) jsonVersionObjectDto.get("htmlContent");
                    if (versionOutput == null) {
                        versionOutput = "";
                    }
                }
            } catch (Exception e) {
                versionOutput = "-";
            }
        }
        // log.debug("return {}",versionOutput);
        return versionOutput;
    }

    public String getDocUrl() {
        String result = "";
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
            String fullURI = servletRequest.getRequestURI();
            if (docUrlMap.containsKey(fullURI)) {
                result = docUrlMap.get(fullURI);
            } else {
                ParamBeanFactory paramBeanFactory = (ParamBeanFactory) EjbUtils.getServiceInterface(ParamBeanFactory.class.getSimpleName());
                String meveoInstanceCode = paramBeanFactory.getInstance().getProperty("monitoring.instanceCode", "");
                String macAddress = "";
                String keyEntreprise = paramBeanFactory.getInstance().getProperty("monitoring.enterpriseKey", "");
                String currentPage = "";
                String destinationPage = "";
                String productVersion = Version.appVersion;
                String input = "{" + "	  #meveoInstanceCode#: #" + meveoInstanceCode + "#," + "	  #productVersion#: #" + productVersion + "#," + "	  #macAddress#: #"
                        + macAddress + "#," + "     #keyEntreprise#: #" + keyEntreprise + "#" + "	  #currentPage#: #" + currentPage + "#," + "}";
                input = input.replaceAll("#", "\"");
                // log.debug("Request Documentation url ={}",input);

                String urlDoc = paramBeanFactory.getInstance().getProperty("monitoring.url", "http://version.meveo.info/meveo-moni") + "/api/rest/documentation";
                JSONObject jsonResponseObject = sendCommand(urlDoc, input);
                destinationPage = (String) jsonResponseObject.get("destinationPage");
                String URLWiki = (String) jsonResponseObject.get("URLWiki");
                result = URLWiki + "/" + destinationPage;
                // log.debug("Documentation Link ={}",result);
                docUrlMap.put(fullURI, result);
            }
        } catch (Exception e) {
            log.error("Exception on getDocUrl {}", e.getMessage());
        }

        return result;
    }

}

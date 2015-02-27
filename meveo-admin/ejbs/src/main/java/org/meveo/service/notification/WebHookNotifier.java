package org.meveo.service.notification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.util.MeveoJpaForJobs;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.slf4j.Logger;
import org.w3c.dom.Document;

@Stateless
public class WebHookNotifier {

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@Inject
	Logger log;

	@Inject
	NotificationHistoryService notificationHistoryService;

	private String evaluate(String expression, IEntity e) throws BusinessException {
		HashMap<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("event", e);

		return (String) RatingService.evaluateExpression(expression, userMap, String.class);
	}

	private Map<String, String> evaluateMap(Map<String, String> map, IEntity e) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		HashMap<Object, Object> userMap = new HashMap<Object, Object>();
		userMap.put("event", e);

		for (String key : map.keySet()) {
			result.put(key, (String) RatingService.evaluateExpression(map.get(key), userMap, String.class));
		}

		return result;
	}

	@Asynchronous
	public void sendRequest(WebHook webHook, IEntity e) {
		log.debug("webhook sendRequest");
		String result = "";

		try {
			String url = webHook.getHost().startsWith("http") ? webHook.getHost() : "http://" + webHook.getHost();
			if (webHook.getPort() > 0) {
				url += ":" + webHook.getPort();
			}

			if (!StringUtils.isBlank(webHook.getPage())) {
				url += "/" + evaluate(webHook.getPage(), e);
			}
			Map<String,String> params = evaluateMap(webHook.getParams(), e);
            String paramQuery="";
            String sep="";
            for(String paramKey:params.keySet()){
            	paramQuery+=sep+URLEncoder.encode(paramKey, "UTF-8")+"="+URLEncoder.encode(params.get(paramKey), "UTF-8");
            	sep="&";
            }
            if(WebHookMethodEnum.HTTP_GET == webHook.getHttpMethod()){
            	url+="?"+paramQuery;
            } else {
            	log.debug("paramQuery={}",paramQuery);
            }
			log.debug("webhook url: {}", url);
			URL obj = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            Map<String, String> headers = evaluateMap(webHook.getHeaders(), e);
            if(!StringUtils.isBlank(webHook.getUsername()) && !headers.containsKey("Authorization")){
     			byte[] bytes = Base64.encodeBase64((webHook.getUsername() + ":" + webHook.getPassword()).getBytes());
     			headers.put("Authorization", "Basic "+new String(bytes));
			}
           
			for (String key : headers.keySet()) {
		        conn.setRequestProperty(key, headers.get(key));
			}
			
			if (WebHookMethodEnum.HTTP_GET == webHook.getHttpMethod()) {
				conn.setRequestMethod("GET");
			} else if (WebHookMethodEnum.HTTP_POST == webHook.getHttpMethod()) {
				conn.setRequestMethod("POST");
			} else if (WebHookMethodEnum.HTTP_PUT == webHook.getHttpMethod()) {
				conn.setRequestMethod("PUT");
			} else if (WebHookMethodEnum.HTTP_DELETE == webHook.getHttpMethod()) {
				conn.setRequestMethod("DELETE");
			}
			conn.setUseCaches(false);
			
            if(WebHookMethodEnum.HTTP_GET != webHook.getHttpMethod()){
            	conn.setDoOutput(true);
	            OutputStream os = conn.getOutputStream();
	            BufferedWriter writer = new BufferedWriter(
	                    new OutputStreamWriter(os, "UTF-8"));
	            writer.write(paramQuery);
	            writer.flush();
	            writer.close();
	            os.close();
            }
			int responseCode = conn.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			result = response.toString();
			if(responseCode!=200){
				try {
					log.debug("webhook httpStatus error : " + responseCode + " response="+result);
					notificationHistoryService.create(webHook, e,"http error status="+responseCode +" response="+result,
							NotificationHistoryStatusEnum.FAILED);
				} catch (BusinessException e2) {
					log.debug("webhook history error : " + e2.getMessage());
					e2.printStackTrace();
				}
			} else {
				HashMap<Object, Object> userMap = new HashMap<Object, Object>();
				userMap.put("event", e);
				userMap.put("response",result);
				if(webHook.getElAction()!=null && webHook.getElAction().indexOf("jsObj.")>=0){
					JSONObject json;
					try {
						json = new JSONObject(result);
						userMap.put("jsObj",json);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				} else if(webHook.getElAction()!=null && webHook.getElAction().indexOf("xmlDoc.")>=0){
					try {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = dbf.newDocumentBuilder();
						Document doc = builder.parse(result);
						userMap.put("xmlDoc",doc);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				try {
					RatingService.evaluateExpression(webHook.getElAction(), userMap, String.class);
				} catch(Exception e1){
					e1.printStackTrace();
				}
				
				notificationHistoryService.create(webHook, e, result, NotificationHistoryStatusEnum.SENT);
				log.debug("webhook answer : " + result);
			}
		} catch (BusinessException e1) {
			try {
				log.debug("webhook business error : " + e1.getMessage());
				notificationHistoryService.create(webHook, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.debug("webhook history error : " + e2.getMessage());
				e2.printStackTrace();
			}
		} catch (IOException e1) {
			try {
				e1.printStackTrace();
				log.debug("webhook io error : " + e1.getMessage());
				notificationHistoryService.create(webHook, e, e1.getMessage(), NotificationHistoryStatusEnum.TO_RETRY);
			} catch (BusinessException e2) {
				log.debug("webhook history error : " + e2.getMessage());
				e2.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		String test="{  \"sid\": \"CLb2f57233976448368708c754b3c1efb7\",  \"date_created\": \"Sat, 21 Feb 2015 18:37:49 +0000\","
				+ "  \"date_updated\": \"Sat, 21 Feb 2015 18:37:49 +0000\",  \"account_sid\": \"ACae6e420f425248d6a26948c17a9e2acf\","
				+ "  \"api_version\": \"2012-04-24\",  \"friendly_name\": \"RC_A1\",  \"login\": \"RC_A1\","
				+ "  \"password\": \"toto\",  \"status\": \"1\",  \"voice_method\": \"POST\",  \"voice_fallback_method\": \"POST\","
				+ "  \"uri\": \"/restcomm/2012-04-24/Accounts/ACae6e420f425248d6a26948c17a9e2acf/Clients/CLb2f57233976448368708c754b3c1efb7.json\"}";
		try {
			JSONObject json = new JSONObject(test);
			System.out.println(json.getString("sid"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

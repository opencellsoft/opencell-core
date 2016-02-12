package org.meveo.client;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

public class ApiExecutor {

	// TODO Use org.apache.http.entity.mime.MultipartEntity;
	public HttpResponse executeApi(String api, String url, String userNale, String password, HttpMethodsEnum method, CommonContentTypeEnum cotentType, Map<String, String> headers, Map<String, String> params, String body, AuthentificationModeEnum authMode) {
		try {

			if (HttpMethodsEnum.POST == method) {
				return executePost(api, url, userNale, password, cotentType, headers, body, authMode);
			}

			if (HttpMethodsEnum.GET == method) {
				return executeGet(api, url, userNale, password, cotentType, headers, params, authMode);
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;
	}

	private HttpResponse executePost(String api, String url, String userName, String password, CommonContentTypeEnum cotentType, Map<String, String> headers, String body, AuthentificationModeEnum authMode) {
		try {

			// TODO set params on the url for get method
			String theUrl = url  + api;
			HttpPost theRequest = new HttpPost(theUrl);
			System.out.println("executePost theUrl :"+theUrl);

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


			HttpResponse response = MeveoConnectionFactory.httpClient.execute(theRequest);
			
			System.out.println("executePost code :" + response.getStatusLine().getStatusCode());
			return response;
		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;
	}

	private HttpResponse executeGet(String api, String url, String userNale, String password, CommonContentTypeEnum cotentType, Map<String, String> headers, Map<String, String> params, AuthentificationModeEnum authMode) {
		throw new UnsupportedOperationException();
	}

	private String getBasicAuthentication(String login, String password) {
		String authentification = login + ":" + password;
		byte[] authEncoded = Base64.encodeBase64(authentification.getBytes());
		return "Basic " + (new String(authEncoded));
	}

}

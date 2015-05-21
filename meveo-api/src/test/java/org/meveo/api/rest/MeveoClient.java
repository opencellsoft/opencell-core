package org.meveo.api.rest;

import java.util.Properties;

import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * @author Edward P. Legaspi
 **/
public class MeveoClient {

	private Logger log = LoggerFactory.getLogger(MeveoClient.class);

	private String host;
	private String api;
	private String username;
	private String password;
	private Properties properties = new Properties();

	public MeveoClient() {

	}

	public MeveoClient(String host, String api) {
		this.host = host;
		this.api = api;
	}

	public MeveoClient(String host, String api, String username, String password) {
		this.host = host;
		this.api = api;
		this.username = username;
		this.password = password;
	}

	public void addParam(String key, String value) {
		properties.put(key, value);
	}

	public String execute() {
		try {
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(username, password));

			String params = "";
			if (properties != null) {
				for (String key : properties.stringPropertyNames()) {
					String value = properties.getProperty(key);
					if (!StringUtils.isBlank(params)) {
						params += "&";
					}
					params += key + "=" + value;
				}
			}

			String apiUrl = host + "/" + api;
			if (!StringUtils.isBlank(params)) {
				apiUrl = apiUrl + "?" + params;
			}
			WebResource webResource = client.resource(apiUrl);

			ClientResponse response = webResource.accept("application/json")
					.get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			return response.getEntity(String.class);
		} catch (Exception e) {
			log.error("error occurred while executing ",e);
			return "";
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}

package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.WebHook;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "WebhookNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebhookNotificationDto extends NotificationDto {

	private static final long serialVersionUID = -24934196712758476L;

	@XmlElement(required = true)
	private String host;
	private int port;

	@XmlElement(required = true)
	private String page;

	/**
	 * Valid values: HTTP_GET, HTTP_POST, HTTP_PUT, HTTP_DELETE.
	 */
	@XmlElement(required = true)
	private String httpMethod;

	private String username;
	private String password;
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();

	public WebhookNotificationDto() {

	}

	public WebhookNotificationDto(WebHook e) {
		super(e);

		host = e.getHost();
		page = e.getPage();
		httpMethod = e.getHttpMethod().name();
		username = e.getUsername();
		password = e.getUsername();
		if (e.getHeaders() != null) {
			headers.putAll(e.getHeaders());
		}
		if (e.getParams() != null) {
			params.putAll(e.getParams());
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
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

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "WebhookDto [host=" + host + ", port=" + port + ", page=" + page + ", httpMethod=" + httpMethod + ", username=" + username + ", password=" + password + ", headers="
				+ headers + ", params=" + params + "]";
	}

}

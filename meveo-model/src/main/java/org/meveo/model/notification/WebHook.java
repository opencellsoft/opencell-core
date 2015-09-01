package org.meveo.model.notification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;

@Entity
@Table(name = "ADM_NOTIF_WEBHOOKS")
public class WebHook extends Notification {

    private static final long serialVersionUID = -2527123286118840886L;

    @Column(name = "HTTP_HOST", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String host;

    @Column(name = "HTTP_PORT")
    @NotNull
    @Max(65535)
    private int port;

    @Column(name = "HTTP_PAGE", length = 255)
    @NotNull
    @Size(max = 255)
    private String page;

    @Column(name = "HTTP_METHOD", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private WebHookMethodEnum httpMethod;

    @Column(name = "USERNAME", length = 255)
    @Size(max = 255)
    private String username;

    @Column(name = "PASSWORD", length = 255)
    @Size(max = 255)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ADM_NOTIF_WEBHOOK_HEADER")
    private Map<String, String> headers = new HashMap<String, String>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ADM_NOTIF_WEBHOOK_PARAM")
    private Map<String, String> webhookParams = new HashMap<String, String>();
    
    @Transient
    private StringBuffer encodedHeaders = new StringBuffer();
   
    @Transient
    private StringBuffer encodedParams = new StringBuffer();

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

    public WebHookMethodEnum getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(WebHookMethodEnum httpMethod) {
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

  
    /**
	 * @return the webhookParams
	 */
	public Map<String, String> getWebhookParams() {
		return webhookParams;
	}

	/**
	 * @param webhookParams the webhookParams to set
	 */
	public void setWebhookParams(Map<String, String> webhookParams) {
		this.webhookParams = webhookParams;
	}

	@Override
    public String toString() {
        final int maxLen = 10;
        return String.format("WebHook [host=%s, port=%s, page=%s, httpMethod=%s, username=%s, password=%s, headers=%s, webhookParams=%s, notification=%s]", host, port, page, httpMethod,
            username, password, headers != null ? toString(headers.entrySet(), maxLen) : null, webhookParams != null ? toString(webhookParams.entrySet(), maxLen) : null, super.toString());
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
    
    public StringBuffer getEncodedHeaders() {
		StringBuffer headers=new StringBuffer();
		if(getHeaders()!=null){
			String sep="";
			for(String key:getHeaders().keySet()){
				String valueHeaders=getHeaders().get(key);
				headers.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueHeaders.getBytes()));
				sep="|";
				}
			}
		return headers;
	}
    
    public StringBuffer getEncodedParams() {
		StringBuffer params=new StringBuffer();
		if(getHeaders()!=null){
			String sep="";
			for(String key:getWebhookParams().keySet()){
				String valueParams=getWebhookParams().get(key);
				params.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueParams.getBytes()));
				sep="|";
				}	
			}
		return params;
	}
}
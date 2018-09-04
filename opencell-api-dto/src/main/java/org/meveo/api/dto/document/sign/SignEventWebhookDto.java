package org.meveo.api.dto.document.sign;

import java.util.Map;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO encapsulating a webhook (for server-server notification) inputs for a signature procedure event.
 */
public class SignEventWebhookDto extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Instantiates a new sign event webhook dto.
     */
    public SignEventWebhookDto () {
    }
    
    /**
     * Instantiates a new sign event webhook dto.
     *
     * @param url the url
     * @param method the method
     */
    public SignEventWebhookDto (String url, String method) {
        this.url = url;
        this.method = method;
    }
    
    /** The url. */
    private String url;
    
    /** The method. */
    private String method;
    
    /** The headers. */
    private Map<String, String> headers;
    
    /**
     * Gets the method.
     *
     * @return the method
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Gets the headers.
     *
     * @return the headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    /**
     * Sets the method.
     *
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Sets the headers.
     *
     * @param headers the headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Sets the url.
     *
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    
}

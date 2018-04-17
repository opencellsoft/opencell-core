package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.HttpProtocol;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;

/**
 * The Class WebHookDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "WebhookNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebHookDto extends NotificationDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -24934196712758476L;

    /** The http protocol. */
    private HttpProtocol httpProtocol;
    
    /** The host. */
    @XmlElement(required = true)
    private String host;
    
    /** The port. */
    @XmlElement(required = true)
    private Integer port;

    /** The page. */
    @XmlElement(required = true)
    private String page;

    /**
     * Valid values: HTTP_GET, HTTP_POST, HTTP_PUT, HTTP_DELETE.
     */
    @XmlElement(required = true)
    private WebHookMethodEnum httpMethod;

    /** The username. */
    private String username;
    
    /** The password. */
    private String password;
    
    /** The headers. */
    private Map<String, String> headers = new HashMap<String, String>();
    
    /** The params. */
    private Map<String, String> params = new HashMap<String, String>();

    /**
     * Instantiates a new web hook dto.
     */
    public WebHookDto() {

    }

    /**
     * Instantiates a new web hook dto.
     *
     * @param webHook the WebHook entity
     */
    public WebHookDto(WebHook webHook) {
        super((Notification) webHook);
        host = webHook.getHost();
        port = webHook.getPort();
        page = webHook.getPage();
        httpMethod = webHook.getHttpMethod();
        username = webHook.getUsername();
        password = webHook.getUsername();
        if (webHook.getHeaders() != null) {
            headers.putAll(webHook.getHeaders());
        }
        if (webHook.getParams() != null) {
            params.putAll(webHook.getParams());
        }
        httpProtocol = webHook.getHttpProtocol();
    }

    /**
     * Gets the host.
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host.
     *
     * @param host the new host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port.
     *
     * @param port the new port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets the page.
     *
     * @return the page
     */
    public String getPage() {
        return page;
    }

    /**
     * Sets the page.
     *
     * @param page the new page
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * Gets the http method.
     *
     * @return the http method
     */
    public WebHookMethodEnum getHttpMethod() {
        return httpMethod;
    }

    /**
     * Sets the http method.
     *
     * @param httpMethod the new http method
     */
    public void setHttpMethod(WebHookMethodEnum httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
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
     * Sets the headers.
     *
     * @param headers the headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Sets the params.
     *
     * @param params the params
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * Gets the http protocol.
     *
     * @return the http protocol
     */
    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }

    /**
     * Sets the http protocol.
     *
     * @param httpProtocol the new http protocol
     */
    public void setHttpProtocol(HttpProtocol httpProtocol) {
        this.httpProtocol = httpProtocol;
    }
    
    @Override
    public String toString() {
        return "WebhookDto [host=" + host + ", port=" + port + ", page=" + page + ", httpMethod=" + httpMethod + ", username=" + username + ", password=" + password + ", headers="
                + headers + ", params=" + params + "]";
    }
}

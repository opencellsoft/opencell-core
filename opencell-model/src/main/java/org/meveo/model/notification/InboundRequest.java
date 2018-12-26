package org.meveo.model.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Incoming request
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "code" })
@Table(name = "adm_inbound_request", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_inbound_request_seq"), })
@NamedQueries({ @NamedQuery(name = "InboundRequest.countRequestsToPurgeByDate", query = "select count(*) FROM InboundRequest ir WHERE ir.auditable.created<=:date"),
        @NamedQuery(name = "InboundRequest.getRequestsToPurgeByDate", query = "select ir from InboundRequest ir WHERE ir.auditable.created<=:date") })
public class InboundRequest extends BusinessEntity {

    private static final long serialVersionUID = 2634877161620665288L;

    /**
     * Request content length
     */
    @Column(name = "content_length")
    private int contentLength;

    /**
     * Request content type
     */
    @Column(name = "content_type", length = 255)
    @Size(max = 255)
    private String contentType;

    /**
     * Request parameters
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_inbound_req_params")
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Http protocol
     */
    @Column(name = "protocol", length = 20)
    @Size(max = 20)
    private String protocol;

    /**
     * Scheme
     */
    @Column(name = "scheme", length = 20)
    @Size(max = 20)
    private String scheme;

    /**
     * Client's IP address
     */
    @Column(name = "remote_adrr", length = 255)
    @Size(max = 255)
    private String remoteAddr;

    /**
     * Client's port
     */
    @Column(name = "remote_port")
    private int remotePort;

    /**
     * Request body
     */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    /**
     * Method
     */
    @Column(name = "method", length = 10)
    @Size(max = 10)
    private String method;

    /**
     * Authentication type
     */
    @Column(name = "auth_type", length = 11)
    @Size(max = 11)
    private String authType;

    /**
     * Request cookies
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_inbound_req_cookies")
    private Map<String, String> coockies = new HashMap<String, String>();

    /**
     * Request headers
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_inbound_req_headers")
    @Column(name = "headers", columnDefinition = "TEXT" )
    @MapKeyColumn(name="headers_key")
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Path requested
     */
    @Column(name = "path_info", length = 255)
    @Size(max = 255)
    private String pathInfo;

    /**
     * Url requested
     */
    @Column(name = "request_uri", length = 255)
    @Size(max = 255)
    private String requestURI;

    // TODO: add parts of a multipart/form-data POST request

    // Response

    /**
     * Notifications fired as result of inbound request. There should be only one (at most few) notification fired, so cascade=Remove is fine
     */
    @OneToMany(mappedBy = "inboundRequest", cascade = CascadeType.REMOVE)
    private List<NotificationHistory> notificationHistories = new ArrayList<NotificationHistory>();

    /**
     * Content type to set in response
     */
    @Column(name = "resp_content_type", length = 255)
    @Size(max = 255)
    private String responseContentType;

    /**
     * Encoding to set in response
     */
    @Column(name = "resp_encoding", length = 50)
    @Size(max = 50)
    private String responseEncoding;

    /**
     * Body of response
     */
    transient private String responseBody;

    /**
     * Cookies to set in response
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_inbound_resp_cookies")
    private Map<String, String> responseCoockies = new HashMap<String, String>();

    /**
     * Headers to set in response
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "adm_inbound_resp_headers")
    private Map<String, String> responseHeaders = new HashMap<String, String>();

    /**
     * HTTP Status to force for response
     */
    @Column(name = "resp_status")
    private Integer responseStatus;

    /**
     * Encoded request parameters
     */
    @Transient
    private StringBuffer encodedParams = new StringBuffer();

    /**
     * Encoded request cookies
     */
    @Transient
    private StringBuffer encodedCookies = new StringBuffer();

    /**
     * Encoded request headers
     */
    @Transient
    private StringBuffer encodedHeaders = new StringBuffer();

    /**
     * Encoded response cookies
     */
    @Transient
    private StringBuffer encodedRespCookies = new StringBuffer();

    /**
     * Encoded response headers
     */
    @Transient
    private StringBuffer encodedRespHeaders = new StringBuffer();

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int i) {
        this.remotePort = i;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Map<String, String> getCoockies() {
        return coockies;
    }

    public void setCoockies(Map<String, String> coockies) {
        this.coockies = coockies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<NotificationHistory> getNotificationHistories() {
        return notificationHistories;
    }

    public void setNotificationHistories(List<NotificationHistory> notificationHistories) {
        this.notificationHistories = notificationHistories;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getResponseCoockies() {
        return responseCoockies;
    }

    public void setResponseCoockies(Map<String, String> responseCoockies) {
        this.responseCoockies = responseCoockies;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void add(NotificationHistory notificationHistory) {
        this.notificationHistories.add(notificationHistory);
        if (notificationHistory.getInboundRequest() != this) {
            notificationHistory.setInboundRequest(this);
        }
    }

    public String getResponseEncoding() {
        return responseEncoding;
    }

    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public StringBuffer getEncodedParams() {
        StringBuffer params = new StringBuffer();
        if (getHeaders() != null) {
            String sep = "";
            for (String key : getParameters().keySet()) {
                String valueParams = getParameters().get(key);
                params.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueParams.getBytes()));
                sep = "|";
            }
        }
        return params;
    }

    public StringBuffer getEncodedCookies() {
        StringBuffer cookies = new StringBuffer();
        if (getHeaders() != null) {
            String sep = "";
            for (String key : getCoockies().keySet()) {
                String valueCookies = getCoockies().get(key);
                cookies.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueCookies.getBytes()));
                sep = "|";
            }
        }
        return cookies;
    }

    public StringBuffer getEncodedHeaders() {
        StringBuffer headers = new StringBuffer();
        if (getHeaders() != null) {
            String sep = "";
            for (String key : getHeaders().keySet()) {
                String valueHeaders = getHeaders().get(key);
                headers.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueHeaders.getBytes()));
                sep = "|";
            }
        }
        return headers;
    }

    public StringBuffer getEncodedRespCookies() {
        StringBuffer responseCoockies = new StringBuffer();
        if (getResponseCoockies() != null) {
            String sep = "";
            for (String key : getResponseCoockies().keySet()) {
                String valueRespCookies = getResponseCoockies().get(key);
                responseCoockies.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueRespCookies.getBytes()));
                sep = "|";
            }
        }
        return responseCoockies;
    }

    public StringBuffer getEncodedRespHeaders() {
        StringBuffer responseHeaders = new StringBuffer();
        if (getResponseHeaders() != null) {
            String sep = "";
            for (String key : getResponseHeaders().keySet()) {
                String valueRespHeaders = getResponseHeaders().get(key);
                responseHeaders.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueRespHeaders.getBytes()));
                sep = "|";
            }
        }
        return responseHeaders;
    }

}

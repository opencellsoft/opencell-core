package org.meveo.model.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "ADM_INBOUND_REQUEST", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_INBOUND_REQUEST_SEQ")
@Inheritance(strategy = InheritanceType.JOINED)
public class InboundRequest extends BusinessEntity {

	private static final long serialVersionUID = 2634877161620665288L;

	@Column(name="CONTENT_LENGTH")
	private int contentLength;

	@Column(name="CONTENT_TYPE",length=255)
	private String contentType;

	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="ADM_INBOUND_REQ_PARAMS")
	private Map<String,String> parameters = new HashMap<String, String>();

	@Column(name="PROTOCOL",length=20)
	private String protocol;
	
	@Column(name="SCHEME",length=20)
	private String scheme;
	
	@Column(name="REMOTE_ADRR",length=255)
	private String remoteAddr;

	@Column(name="REMOTE_PORT")
	private int remotePort;
	
	transient private String body;
	
	@Column(name="METHOD",length=10)
	private String method;

	@Column(name="AUTH_TYPE",length=11)
	private String authType;

	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="ADM_INBOUND_REQ_COOKIES")
	private Map<String,String> coockies = new HashMap<String, String>();
	
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="ADM_INBOUND_REQ_HEADERS")
	private Map<String,String> headers = new HashMap<String, String>();

	@Column(name="PATH_INFO",length=255)
	private String pathInfo;
	
	@Column(name="REQUEST_URI",length=255)
	private String requestURI;
	
	//TODO: add parts of a multipart/form-data POST request
	
	//response

	@OneToMany(mappedBy="inboundRequest")
	private List<NotificationHistory> notificationHistories = new ArrayList<NotificationHistory>();

	@Column(name="RESP_CONTENT_TYPE",length=255)
	private String responseContentType;

	@Column(name="RESP_ENCODING",length=50)
	private String responseEncoding;

	transient private String responseBody;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="ADM_INBOUND_RESP_COOKIES")
	private Map<String,String> responseCoockies = new HashMap<String, String>();
	
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="ADM_INBOUND_RESP_HEADERS")
	private Map<String,String> responseHeaders = new HashMap<String, String>();

	
	
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

	public Map<String,String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String,String> parameters) {
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

	public Map<String,String> getCoockies() {
		return coockies;
	}

	public void setCoockies(Map<String,String> coockies) {
		this.coockies = coockies;
	}

	public Map<String,String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String,String> headers) {
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

	public void setNotificationHistories(
			List<NotificationHistory> notificationHistories) {
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
	
	public void add(NotificationHistory notificationHistory){
		this.notificationHistories.add(notificationHistory);
		if(notificationHistory.getInboundRequest() != this){
			notificationHistory.setInboundRequest(this);
		}
	}

	public String getResponseEncoding() {
		return responseEncoding;
	}

	public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }
}

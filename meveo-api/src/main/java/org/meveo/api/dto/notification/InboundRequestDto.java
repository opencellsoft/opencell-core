package org.meveo.api.dto.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.notification.InboundRequest;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InboundRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class InboundRequestDto extends BaseDto {

	private static final long serialVersionUID = -3195747154300291876L;

	private int contentLength;
	private String contentType;
	private String protocol;
	private String scheme;
	private String remoteAddr;
	private int remotePort;
	private String method;
	private String authType;
	private String pathInfo;
	private String requestURI;
	private String responseContentType;
	private String responseEncoding;

	public InboundRequestDto() {

	}

	public InboundRequestDto(InboundRequest e) {
		contentLength = e.getContentLength();
		contentType = e.getContentType();
		protocol = e.getProtocol();
		scheme = e.getScheme();
		remoteAddr = e.getRemoteAddr();
		remotePort = e.getRemotePort();
		method = e.getMethod();
		authType = e.getAuthType();
		pathInfo = e.getPathInfo();
		requestURI = e.getRequestURI();
		responseContentType = e.getResponseContentType();
		responseEncoding = e.getResponseEncoding();
	}

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

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
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

	public String getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}

	public String getResponseEncoding() {
		return responseEncoding;
	}

	public void setResponseEncoding(String responseEncoding) {
		this.responseEncoding = responseEncoding;
	}

	@Override
	public String toString() {
		return "InboundRequestDto [contentLength=" + contentLength + ", contentType=" + contentType + ", protocol=" + protocol + ", scheme=" + scheme + ", remoteAddr="
				+ remoteAddr + ", remotePort=" + remotePort + ", method=" + method + ", authType=" + authType + ", pathInfo=" + pathInfo + ", requestURI=" + requestURI
				+ ", responseContentType=" + responseContentType + ", responseEncoding=" + responseEncoding + "]";
	}

}

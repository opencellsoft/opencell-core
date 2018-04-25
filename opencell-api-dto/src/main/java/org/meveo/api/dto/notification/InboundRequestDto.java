package org.meveo.api.dto.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.notification.InboundRequest;

/**
 * The Class InboundRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InboundRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class InboundRequestDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3195747154300291876L;

    /** The content length. */
    private int contentLength;
    
    /** The content type. */
    private String contentType;
    
    /** The protocol. */
    private String protocol;
    
    /** The scheme. */
    private String scheme;
    
    /** The remote addr. */
    private String remoteAddr;
    
    /** The remote port. */
    private int remotePort;
    
    /** The method. */
    private String method;
    
    /** The auth type. */
    private String authType;
    
    /** The path info. */
    private String pathInfo;
    
    /** The request URI. */
    private String requestURI;
    
    /** The response content type. */
    private String responseContentType;
    
    /** The response encoding. */
    private String responseEncoding;

    /**
     * Instantiates a new inbound request dto.
     */
    public InboundRequestDto() {

    }

    /**
     * Instantiates a new inbound request dto.
     *
     * @param inboundRequest the InboundRequest entity
     */
    public InboundRequestDto(InboundRequest inboundRequest) {
        contentLength = inboundRequest.getContentLength();
        contentType = inboundRequest.getContentType();
        protocol = inboundRequest.getProtocol();
        scheme = inboundRequest.getScheme();
        remoteAddr = inboundRequest.getRemoteAddr();
        remotePort = inboundRequest.getRemotePort();
        method = inboundRequest.getMethod();
        authType = inboundRequest.getAuthType();
        pathInfo = inboundRequest.getPathInfo();
        requestURI = inboundRequest.getRequestURI();
        responseContentType = inboundRequest.getResponseContentType();
        responseEncoding = inboundRequest.getResponseEncoding();
    }

    /**
     * Gets the content length.
     *
     * @return the content length
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * Sets the content length.
     *
     * @param contentLength the new content length
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Gets the content type.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType the new content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the protocol.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol.
     *
     * @param protocol the new protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the scheme.
     *
     * @return the scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the scheme.
     *
     * @param scheme the new scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Gets the remote addr.
     *
     * @return the remote addr
     */
    public String getRemoteAddr() {
        return remoteAddr;
    }

    /**
     * Sets the remote addr.
     *
     * @param remoteAddr the new remote addr
     */
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    /**
     * Gets the remote port.
     *
     * @return the remote port
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * Sets the remote port.
     *
     * @param remotePort the new remote port
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * Gets the method.
     *
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method.
     *
     * @param method the new method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the auth type.
     *
     * @return the auth type
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * Sets the auth type.
     *
     * @param authType the new auth type
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Gets the path info.
     *
     * @return the path info
     */
    public String getPathInfo() {
        return pathInfo;
    }

    /**
     * Sets the path info.
     *
     * @param pathInfo the new path info
     */
    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    /**
     * Gets the request URI.
     *
     * @return the request URI
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Sets the request URI.
     *
     * @param requestURI the new request URI
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * Gets the response content type.
     *
     * @return the response content type
     */
    public String getResponseContentType() {
        return responseContentType;
    }

    /**
     * Sets the response content type.
     *
     * @param responseContentType the new response content type
     */
    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    /**
     * Gets the response encoding.
     *
     * @return the response encoding
     */
    public String getResponseEncoding() {
        return responseEncoding;
    }

    /**
     * Sets the response encoding.
     *
     * @param responseEncoding the new response encoding
     */
    public void setResponseEncoding(String responseEncoding) {
        this.responseEncoding = responseEncoding;
    }


    @Override
    public String toString() {
        return "InboundRequestDto [contentLength=" + contentLength + ", contentType=" + contentType + ", protocol=" + protocol + ", scheme=" + scheme + ", remoteAddr=" + remoteAddr
                + ", remotePort=" + remotePort + ", method=" + method + ", authType=" + authType + ", pathInfo=" + pathInfo + ", requestURI=" + requestURI
                + ", responseContentType=" + responseContentType + ", responseEncoding=" + responseEncoding + "]";
    }

}
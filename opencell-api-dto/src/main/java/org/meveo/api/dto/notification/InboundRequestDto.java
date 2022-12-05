/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.notification;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.notification.InboundRequest;

/**
 * The Class InboundRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InboundRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class InboundRequestDto extends BusinessEntityDto {

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
        super(inboundRequest);
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
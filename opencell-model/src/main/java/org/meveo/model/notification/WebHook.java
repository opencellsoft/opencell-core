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
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Type;
import org.meveo.model.ModuleItem;

/**
 * Notification that access URL
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@ModuleItem
@Table(name = "adm_notif_webhooks")
public class WebHook extends Notification {

    private static final long serialVersionUID = -2527123286118840886L;

    /**
     * Host
     */
    @Column(name = "http_host", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String host;

    /**
     * Http port
     */
    @Column(name = "http_port")
    @Max(65535)
    private Integer port;

    /**
     * Page
     */
    @Column(name = "http_page", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String page;

    /**
     * Http method
     */
    @Column(name = "http_method", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private WebHookMethodEnum httpMethod;

    /**
     * Username
     */
    @Column(name = "username", length = 255)
    @Size(max = 255)
    private String username;

    /**
     * Password
     */
    @Column(name = "pswd", length = 255)
    @Size(max = 255)
    private String password;

    /**
     * Expression to compose a request body
     */
    @Column(name = "body_el", length = 2000)
    @Size(max = 2000)
    private String bodyEL = null;

    /**
     * A list of expressions to construct request headers
     */
    @Type(type = "longText")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_webhook_header")
    @Column(name = "headers" )
    @MapKeyColumn(name="headers_key")
    private Map<String, String> headers = new HashMap<>();

    /**
     * A list of expressions to construct query parameters
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_webhook_param")
    private Map<String, String> webhookParams = new HashMap<>();

    /**
     * Protocol
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "http_protocol", length = 10, nullable = false)
    private HttpProtocol httpProtocol = HttpProtocol.HTTP;

    /**
     * Encoded request headers
     */
    @Transient
    private StringBuffer encodedHeaders = new StringBuffer();

    /**
     * Encoded request parameters
     */
    @Transient
    private StringBuffer encodedParams = new StringBuffer();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
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
        return String.format("WebHook [host=%s, port=%s, page=%s, httpMethod=%s, username=%s, pass_word=%s, headers=%s, webhookParams=%s, notification=%s]", host, port, page,
            httpMethod, username, password, headers != null ? toString(headers.entrySet(), maxLen) : null,
            webhookParams != null ? toString(webhookParams.entrySet(), maxLen) : null, super.toString());
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

    public StringBuffer getEncodedParams() {
        StringBuffer params = new StringBuffer();
        if (getHeaders() != null) {
            String sep = "";
            for (String key : getWebhookParams().keySet()) {
                String valueParams = getWebhookParams().get(key);
                params.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueParams.getBytes()));
                sep = "|";
            }
        }
        return params;
    }

    public String getBodyEL() {
        return bodyEL;
    }

    public void setBodyEL(String bodyEL) {
        this.bodyEL = bodyEL;
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(HttpProtocol httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

}
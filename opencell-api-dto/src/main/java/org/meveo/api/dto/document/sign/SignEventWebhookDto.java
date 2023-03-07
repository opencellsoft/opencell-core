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

    @Override
    public String toString() {
        return "SignEventWebhookDto [url=" + url + ", method=" + method + ", headers=" + headers + "]";
    }
}

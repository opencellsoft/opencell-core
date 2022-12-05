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

package org.tmf.dsmapi.catalog.resource;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "id": "22", "href": "http://serverlocation:port/documentManagment/attachment/22", "description": "Detailed image of the item" "type": "Picture", "url": "http://xxxxx"
 *         }
 * 
 */

@XmlRootElement(name = "Attachment", namespace="http://www.tmforum.org")
@XmlType(name = "Attachment", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class Attachment implements Serializable {
    public final static long serialVersionUID = 1L;

    private String id;

    private String href;

    private String description;

    private String type;

    private String url;

    public Attachment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash + (this.url != null ? this.url.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final Attachment other = (Attachment) object;
        if (Utilities.areEqual(this.id, other.id) == false) {
            return false;
        }

        if (Utilities.areEqual(this.href, other.href) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (Utilities.areEqual(this.type, other.type) == false) {
            return false;
        }

        if (Utilities.areEqual(this.url, other.url) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Attachment{" + "id=" + id + ", href=" + href + ", description=" + description + ", type=" + type + ", url=" + url + '}';
    }

    public static Attachment createProto() {
        Attachment attachment = new Attachment();

        attachment.id = "id";
        attachment.href = "href";
        attachment.description = "description";
        attachment.type = "type";
        attachment.url = "url";

        return attachment;
    }

}

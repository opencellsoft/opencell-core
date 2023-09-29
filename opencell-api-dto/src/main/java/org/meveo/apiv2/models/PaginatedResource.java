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

package org.meveo.apiv2.models;

import javax.annotation.Nullable;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.List;
import java.util.Map;

public interface PaginatedResource<T> {
    @Nullable
    Long getTotal();
    Long getLimit();
    Long getOffset();
    Map<String, Object> getFilters();
    List<T> getData();

    @XmlTransient
    default URI getNext()
    {
        if (getLinks() == null) return null;
        for (Link link : getLinks())
        {
            if ("next".equals(link.getRel())) return link.getUri();
        }
        return null;
    }

    @XmlTransient
    default URI getPrevious()
    {
        if (getLinks() == null) return null;
        for (Link link : getLinks())
        {
            if ("previous".equals(link.getRel())) return link.getUri();
        }
        return null;
    }
    @Nullable
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> getLinks();
}

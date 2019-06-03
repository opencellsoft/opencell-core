package org.meveo.apiv2.models;

import javax.annotation.Nullable;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.List;

public interface PaginatedResource<T> {
    Long getTotal();
    Long getLimit();
    Long getOffset();
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

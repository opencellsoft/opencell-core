package org.meveo.apiv2.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ImmutableResource.class)
public interface Resource extends Serializable {
    @Nullable
    Long getId();
    @Nullable
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> getLinks();
}

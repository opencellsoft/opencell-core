package org.meveo.api.dto.custom;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.apiv2.models.Resource;

public class UnitaryCustomTableDataResource implements Resource {

    @XmlAttribute(name = "entity")
    private UnitaryCustomTableDataDto entity;

    @XmlAttribute(name = "links")
    private List<Link> links;

    public UnitaryCustomTableDataResource(UnitaryCustomTableDataDto entity, List<Link> links) {
        this.entity = entity;
        this.links = links;
    }

    @Override
    public Long getId() {
        return entity.getValue().getId();
    }

    public String getCustomTableName(){
        return entity.getCustomTableCode();
    }


    @Override
    public List<Link> getLinks() {
        return this.links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}

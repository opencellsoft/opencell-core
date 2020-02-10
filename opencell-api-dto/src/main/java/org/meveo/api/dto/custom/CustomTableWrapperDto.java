package org.meveo.api.dto.custom;

import org.meveo.api.dto.response.PagingAndFiltering;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CustomFieldTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomTableWrapperDto {
    /**
     *
     */
    @XmlElement(required = true)
    private String ctwCode;
    private String entityId;
    private String entityClass;
    private PagingAndFiltering pagingAndFiltering;

    public String getCtwCode() {
        return ctwCode;
    }

    public void setCtwCode(String ctwCode) {
        this.ctwCode = ctwCode;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public PagingAndFiltering getPagingAndFiltering() {
        return pagingAndFiltering;
    }

    public void setPagingAndFiltering(PagingAndFiltering pagingAndFiltering) {
        this.pagingAndFiltering = pagingAndFiltering;
    }
}

package org.meveo.api.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.meveo.model.BusinessEntity;

@XmlRootElement(name = "BusinessEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessEntityDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute()
    private String code;
    @XmlAttribute()
    private String description;

    public BusinessEntityDto() {
    }

    public BusinessEntityDto(BusinessEntity entity) {
        this.code = entity.getCode();
        this.description = entity.getDescription();
    }

    public BusinessEntityDto(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

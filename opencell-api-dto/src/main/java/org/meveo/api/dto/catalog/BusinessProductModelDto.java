package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.model.module.MeveoModule;

@XmlRootElement(name = "BusinessProductModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessProductModelDto extends MeveoModuleDto implements Serializable {

    private static final long serialVersionUID = -4510290371772010482L;

    @NotNull
    @XmlElement(required = true)
    private ProductTemplateDto productTemplate;

    public BusinessProductModelDto() {

    }

    public BusinessProductModelDto(MeveoModule module) {
        super(module);
    }

    public ProductTemplateDto getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplateDto productTemplate) {
        this.productTemplate = productTemplate;
    }

}

package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetProductTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetProductTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2801794466203329264L;

    /** The product template. */
    private ProductTemplateDto productTemplate;

    /**
     * Gets the product template.
     *
     * @return the product template
     */
    public ProductTemplateDto getProductTemplate() {
        return productTemplate;
    }

    /**
     * Sets the product template.
     *
     * @param productTemplate the new product template
     */
    public void setProductTemplate(ProductTemplateDto productTemplate) {
        this.productTemplate = productTemplate;
    }

    @Override
    public String toString() {
        return "GetProductTemplateResponseDto [productTemplate=" + productTemplate + "]";
    }
}
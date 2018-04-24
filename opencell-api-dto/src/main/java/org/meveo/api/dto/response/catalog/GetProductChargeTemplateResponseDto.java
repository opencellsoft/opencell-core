package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetProductChargeTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetProductChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProductChargeTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6452175086333220603L;

    /** The product charge template. */
    private ProductChargeTemplateDto productChargeTemplate;

    /**
     * Gets the product charge template.
     *
     * @return the product charge template
     */
    public ProductChargeTemplateDto getProductChargeTemplate() {
        return productChargeTemplate;
    }

    /**
     * Sets the product charge template.
     *
     * @param productChargeTemplate the new product charge template
     */
    public void setProductChargeTemplate(ProductChargeTemplateDto productChargeTemplate) {
        this.productChargeTemplate = productChargeTemplate;
    }

    @Override
    public String toString() {
        return "GetProductChargeTemplateResponseDto [getProductChargeTemplate=" + productChargeTemplate + ", toString()=" + super.toString() + "]";
    }
}
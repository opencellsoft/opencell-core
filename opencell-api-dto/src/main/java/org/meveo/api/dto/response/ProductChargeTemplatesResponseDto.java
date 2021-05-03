package org.meveo.api.dto.response;

import org.meveo.api.dto.catalog.ProductChargeTemplateDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ProductChargeTemplatesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "ProductChargeTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductChargeTemplatesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The productChargeTemplates DTO. */
    private List<ProductChargeTemplateDto> productChargeTemplatesDto = new ArrayList<>();

    /**
     * Constructor of ProductChargeTemplatesResponseDto.
     */
    public ProductChargeTemplatesResponseDto() {
    }

    public ProductChargeTemplatesResponseDto(GenericSearchResponse<ProductChargeTemplateDto> searchResponse) {
        super(searchResponse.getPaging());
        this.setProductChargeTemplates(searchResponse.getSearchResults());
    }

    /**
     * Get the list of ProductChargeTemplateDtos.
     *
     * @return the list of ProductChargeTemplateDtos
     */
    public List<ProductChargeTemplateDto> getProductChargeTemplates() {
        return productChargeTemplatesDto;
    }

    /**
     * Sets the productChargeTemplates DTO.
     *
     * @param productChargeTemplatesDto the productChargeTemplates DTO
     */
    public void setProductChargeTemplates(List<ProductChargeTemplateDto> productChargeTemplatesDto) {
        this.productChargeTemplatesDto = productChargeTemplatesDto;
    }

    @Override
    public String toString() {
        return "ListProductChargeTemplatesResponseDto [productChargeTemplatesDto=" + productChargeTemplatesDto + ", toString()=" + super.toString() + "]";
    }
}

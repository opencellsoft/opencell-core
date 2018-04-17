package org.meveo.api.dto.response.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListProductTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetListProductTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListProductTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6452175083213220603L;

    /** The list product template. */
    private List<ProductTemplateDto> listProductTemplate;

    /**
     * Instantiates a new gets the list product template response dto.
     */
    public GetListProductTemplateResponseDto() {
    }

    /**
     * Gets the list product template.
     *
     * @return the list product template
     */
    public List<ProductTemplateDto> getListProductTemplate() {
        return listProductTemplate;
    }

    /**
     * Sets the list product template.
     *
     * @param listProductTemplate the new list product template
     */
    public void setListProductTemplate(List<ProductTemplateDto> listProductTemplate) {
        this.listProductTemplate = listProductTemplate;
    }

    /**
     * Adds the product template.
     *
     * @param productTemplate the product template
     */
    public void addProductTemplate(ProductTemplateDto productTemplate) {
        if (listProductTemplate == null) {
            listProductTemplate = new ArrayList<>();
        }
        listProductTemplate.add(productTemplate);
    }
}
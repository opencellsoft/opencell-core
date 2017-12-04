package org.meveo.api.dto.response.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

@XmlRootElement(name = "GetListProductTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListProductTemplateResponseDto extends SearchResponse {

    private static final long serialVersionUID = 6452175083213220603L;

    private List<ProductTemplateDto> listProductTemplate;

    public GetListProductTemplateResponseDto() {
    }

    public List<ProductTemplateDto> getListProductTemplate() {
        return listProductTemplate;
    }

    public void setListProductTemplate(List<ProductTemplateDto> listProductTemplate) {
        this.listProductTemplate = listProductTemplate;
    }

    public void addProductTemplate(ProductTemplateDto productTemplate) {
        if (listProductTemplate == null) {
            listProductTemplate = new ArrayList<>();
        }
        listProductTemplate.add(productTemplate);
    }
}
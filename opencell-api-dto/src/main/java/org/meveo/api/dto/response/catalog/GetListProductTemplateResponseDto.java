package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetListProductTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListProductTemplateResponseDto extends BaseResponse {

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

    @Override
    public String toString() {
        return "GetListProductTemplateResponseDto [listProductTemplate=" + listProductTemplate + "]";
    }
}
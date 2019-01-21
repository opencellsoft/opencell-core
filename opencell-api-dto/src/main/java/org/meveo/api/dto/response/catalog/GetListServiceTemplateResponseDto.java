package org.meveo.api.dto.response.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class GetListServiceTemplateResponseDto.
 * 
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */
@XmlRootElement(name = "GetListServiceTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListServiceTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6452175083213220603L;

    /** The list product template. */
    private List<ServiceTemplateDto> listServiceTemplate;

    /**
     * Gets the service template list.
     *
     * @return the list service template
     */
    public List<ServiceTemplateDto> getListServiceTemplate() {
        return listServiceTemplate;
    }

    /**
     * Sets the list product template.
     *
     * @param listProductTemplate the new list product template
     */
    public void setListServiceTemplate(List<ServiceTemplateDto> listServiceTemplate) {
        this.listServiceTemplate = listServiceTemplate;
    }

    public void addServiceTemplate(ServiceTemplateDto serviceTemplateDto) {
        if (listServiceTemplate == null) {
            listServiceTemplate = new ArrayList<>();
        }
        listServiceTemplate.add(serviceTemplateDto);
    }

}
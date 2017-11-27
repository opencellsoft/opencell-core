package org.meveo.api.dto.response.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.response.SearchResponse;

@XmlRootElement(name = "GetListBundleTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListBundleTemplateResponseDto extends SearchResponse {

    private static final long serialVersionUID = 5535571034571826093L;

    @XmlElementWrapper(name = "bundleTemplates")
    @XmlElement(name = "bundleTemplate")
    private List<BundleTemplateDto> bundleTemplates;

    public GetListBundleTemplateResponseDto() {
    }

    public List<BundleTemplateDto> getBundleTemplates() {
        return bundleTemplates;
    }

    public void setBundleTemplates(List<BundleTemplateDto> bundleTemplates) {
        this.bundleTemplates = bundleTemplates;
    }

    public void addBundleTemplate(BundleTemplateDto bundleTemplate) {
        if (bundleTemplates == null) {
            bundleTemplates = new ArrayList<>();
        }
        bundleTemplates.add(bundleTemplate);
    }
}
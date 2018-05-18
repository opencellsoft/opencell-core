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

/**
 * The Class GetListBundleTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetListBundleTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListBundleTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5535571034571826093L;

    /** The bundle templates. */
    @XmlElementWrapper(name = "bundleTemplates")
    @XmlElement(name = "bundleTemplate")
    private List<BundleTemplateDto> bundleTemplates;

    /**
     * Instantiates a new gets the list bundle template response dto.
     */
    public GetListBundleTemplateResponseDto() {
    }

    /**
     * Gets the bundle templates.
     *
     * @return the bundle templates
     */
    public List<BundleTemplateDto> getBundleTemplates() {
        return bundleTemplates;
    }

    /**
     * Sets the bundle templates.
     *
     * @param bundleTemplates the new bundle templates
     */
    public void setBundleTemplates(List<BundleTemplateDto> bundleTemplates) {
        this.bundleTemplates = bundleTemplates;
    }

    /**
     * Adds the bundle template.
     *
     * @param bundleTemplate the bundle template
     */
    public void addBundleTemplate(BundleTemplateDto bundleTemplate) {
        if (bundleTemplates == null) {
            bundleTemplates = new ArrayList<>();
        }
        bundleTemplates.add(bundleTemplate);
    }
}
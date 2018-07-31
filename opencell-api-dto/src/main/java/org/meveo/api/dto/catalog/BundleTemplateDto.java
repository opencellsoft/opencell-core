package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.BundleTemplate;

/**
 * The Class BundleTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BundleTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleTemplateDto extends ProductTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6581346092486998984L;

    /** The bundle product templates. */
    @XmlElementWrapper(required = true, name = "bundleProducts")
    @XmlElement(required = true, name = "bundleProduct")
    private List<BundleProductTemplateDto> bundleProductTemplates;

    /**
     * Instantiates a new bundle template dto.
     */
    public BundleTemplateDto() {
    }

    /**
     * Instantiates a new bundle template dto.
     *
     * @param bundleTemplate the bundle template
     * @param customFieldsDto the custom fields dto
     * @param asLink the as link
     */
    public BundleTemplateDto(BundleTemplate bundleTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
        super(bundleTemplate, customFieldsDto, asLink, true);
    }

    /**
     * Gets the bundle product templates.
     *
     * @return the bundle product templates
     */
    public List<BundleProductTemplateDto> getBundleProductTemplates() {
        return bundleProductTemplates;
    }

    /**
     * Sets the bundle product templates.
     *
     * @param bundleProductTemplates the new bundle product templates
     */
    public void setBundleProductTemplates(List<BundleProductTemplateDto> bundleProductTemplates) {
        this.bundleProductTemplates = bundleProductTemplates;
    }
}
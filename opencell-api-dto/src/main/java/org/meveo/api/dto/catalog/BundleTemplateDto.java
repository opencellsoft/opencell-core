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

@XmlRootElement(name = "BundleTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleTemplateDto extends ProductTemplateDto implements Serializable {

	private static final long serialVersionUID = -6581346092486998984L;

	@XmlElementWrapper(required = true, name = "bundleProducts")
	@XmlElement(required = true, name = "bundleProduct")
	private List<BundleProductTemplateDto> bundleProductTemplates;

	public BundleTemplateDto() {
	}

	public BundleTemplateDto(BundleTemplate bundleTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
		super(bundleTemplate, customFieldsDto, asLink, true);
	}

	public List<BundleProductTemplateDto> getBundleProductTemplates() {
		return bundleProductTemplates;
	}

	public void setBundleProductTemplates(List<BundleProductTemplateDto> bundleProductTemplates) {
		this.bundleProductTemplates = bundleProductTemplates;
	}
}
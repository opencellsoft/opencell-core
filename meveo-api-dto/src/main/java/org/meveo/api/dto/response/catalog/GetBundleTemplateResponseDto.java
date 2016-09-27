package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BundleTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetBundleTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBundleTemplateResponseDto extends BaseResponse {

	private static final long serialVersionUID = -2289076826198378613L;

		private BundleTemplateDto bundleTemplate;

	public BundleTemplateDto getBundleTemplate() {
		return bundleTemplate;
	}

	public void setBundleTemplate(BundleTemplateDto bundleTemplate) {
		this.bundleTemplate = bundleTemplate;
	}

	@Override
	public String toString() {
		return "GetBundleTemplateResponseDto [bundleTemplate=" + bundleTemplate + "]";
	}
}

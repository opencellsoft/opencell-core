package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;

@XmlRootElement(name = "ProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTemplateDto extends ProductOfferingDto implements Serializable {

	private static final long serialVersionUID = 1866373944715745993L;

	@XmlElementWrapper(name = "productChargeTemplates")
	@XmlElement(name = "productChargeTemplate", required = true)
	private List<ProductChargeTemplateDto> productChargeTemplates;

	private BusinessProductModelDto businessProductModel;

	@XmlElementWrapper(name = "walletTemplates")
	@XmlElement(name = "walletTemplate")
	private List<WalletTemplateDto> walletTemplates;

	public ProductTemplateDto() {
	}

	public ProductTemplateDto(ProductTemplate productTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
		super(productTemplate, customFieldsDto, asLink);

		if (asLink){
		    return;
		}
		
		BusinessProductModel businessProductModel = productTemplate.getBusinessProductModel();
		BusinessProductModelDto businessProductModelDto = null;
		if (businessProductModel != null) {
			businessProductModelDto = new BusinessProductModelDto(businessProductModel);
		}
		this.setBusinessProductModel(businessProductModelDto);
		List<WalletTemplate> walletTemplates = productTemplate.getWalletTemplates();
		if (walletTemplates != null && !walletTemplates.isEmpty()) {
			WalletTemplateDto walletDto = null;
			this.setWalletTemplates(new ArrayList<WalletTemplateDto>());
			for (WalletTemplate walletTemplate : walletTemplates) {
				walletDto = new WalletTemplateDto(walletTemplate);
				this.getWalletTemplates().add(walletDto);
			}
		}
	}

	public List<ProductChargeTemplateDto> getProductChargeTemplates() {
		return productChargeTemplates;
	}
	
	public void setProductChargeTemplates(List<ProductChargeTemplateDto> productChargeTemplates) {
		this.productChargeTemplates = productChargeTemplates;
	}

	public BusinessProductModelDto getBusinessProductModel() {
		return businessProductModel;
	}

	public void setBusinessProductModel(BusinessProductModelDto businessProductModel) {
		this.businessProductModel = businessProductModel;
	}

	public List<WalletTemplateDto> getWalletTemplates() {
		return walletTemplates;
	}

	public void setWalletTemplates(List<WalletTemplateDto> walletTemplates) {
		this.walletTemplates = walletTemplates;
	}

    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && (productChargeTemplates == null || productChargeTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }
}

package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;

@XmlRootElement(name = "ProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTemplateDto extends ProductOfferingDto implements Serializable {

	private static final long serialVersionUID = 1866373944715745993L;
	
	private ProductChargeTemplateDto productChargeTemplateDto;
	
	private BusinessProductModelDto businessProductModelDto;
	
	@XmlElementWrapper(name = "walletTemplates")
	@XmlElement(name = "walletTemplate")
	private List<WalletTemplateDto> walletTemplates;
	
	public ProductTemplateDto() {
	}
	
	public ProductTemplateDto(ProductTemplate productTemplate) {
		super(productTemplate);
		
		ProductChargeTemplate productChargeTemplate = productTemplate.getProductChargeTemplate();
		ProductChargeTemplateDto productChargeTemplateDto = new ProductChargeTemplateDto(productChargeTemplate);
		this.setProductChargeTemplateDto(productChargeTemplateDto);
		BusinessProductModel businessProductModel = productTemplate.getBusinessProductModel();
		BusinessProductModelDto businessProductModelDto = new BusinessProductModelDto(businessProductModel);
		this.setBusinessProductModelDto(businessProductModelDto);
		List<WalletTemplate> walletTemplates = productTemplate.getWalletTemplates();
		if(walletTemplates != null && !walletTemplates.isEmpty()){
			WalletTemplateDto walletDto = null;
			this.setWalletTemplates(new ArrayList<WalletTemplateDto>());
			for (WalletTemplate walletTemplate : walletTemplates) {
				walletDto = new WalletTemplateDto(walletTemplate);
				this.getWalletTemplates().add(walletDto);
			}
		}
		
		
	}

	public ProductChargeTemplateDto getProductChargeTemplateDto() {
		return productChargeTemplateDto;
	}
	
	public void setProductChargeTemplateDto(ProductChargeTemplateDto productChargeTemplateDto) {
		this.productChargeTemplateDto = productChargeTemplateDto;
	}

	public BusinessProductModelDto getBusinessProductModelDto() {
		return businessProductModelDto;
	}
	
	public void setBusinessProductModelDto(BusinessProductModelDto businessProductModelDto) {
		this.businessProductModelDto = businessProductModelDto;
	}

	public List<WalletTemplateDto> getWalletTemplates() {
		return walletTemplates;
	}
	
	public void setWalletTemplates(List<WalletTemplateDto> walletTemplates) {
		this.walletTemplates = walletTemplates;
	}
}

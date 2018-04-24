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

/**
 * The Class ProductTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "ProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductTemplateDto extends ProductOfferingDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1866373944715745993L;

    /** The product charge templates. */
    @XmlElementWrapper(name = "productChargeTemplates")
    @XmlElement(name = "productChargeTemplate", required = true)
    private List<ProductChargeTemplateDto> productChargeTemplates;

    /** The business product model. */
    private BusinessProductModelDto businessProductModel;

    /** The wallet templates. */
    @XmlElementWrapper(name = "walletTemplates")
    @XmlElement(name = "walletTemplate")
    private List<WalletTemplateDto> walletTemplates;

    /**
     * Instantiates a new product template dto.
     */
    public ProductTemplateDto() {
    }

    /**
     * Instantiates a new product template dto.
     *
     * @param productTemplate the product template
     * @param customFieldsDto the custom fields dto
     * @param asLink the as link
     */
    public ProductTemplateDto(ProductTemplate productTemplate, CustomFieldsDto customFieldsDto, boolean asLink) {
        super(productTemplate, customFieldsDto, asLink);

        if (asLink) {
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

    /**
     * Gets the product charge templates.
     *
     * @return the product charge templates
     */
    public List<ProductChargeTemplateDto> getProductChargeTemplates() {
        return productChargeTemplates;
    }

    /**
     * Sets the product charge templates.
     *
     * @param productChargeTemplates the new product charge templates
     */
    public void setProductChargeTemplates(List<ProductChargeTemplateDto> productChargeTemplates) {
        this.productChargeTemplates = productChargeTemplates;
    }

    /**
     * Gets the business product model.
     *
     * @return the business product model
     */
    public BusinessProductModelDto getBusinessProductModel() {
        return businessProductModel;
    }

    /**
     * Sets the business product model.
     *
     * @param businessProductModel the new business product model
     */
    public void setBusinessProductModel(BusinessProductModelDto businessProductModel) {
        this.businessProductModel = businessProductModel;
    }

    /**
     * Gets the wallet templates.
     *
     * @return the wallet templates
     */
    public List<WalletTemplateDto> getWalletTemplates() {
        return walletTemplates;
    }

    /**
     * Sets the wallet templates.
     *
     * @param walletTemplates the new wallet templates
     */
    public void setWalletTemplates(List<WalletTemplateDto> walletTemplates) {
        this.walletTemplates = walletTemplates;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(getDescription()) && (productChargeTemplates == null || productChargeTemplates.isEmpty()) && (customFields == null || customFields.isEmpty());
    }
}

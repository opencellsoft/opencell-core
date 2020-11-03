/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.CustomerBrand;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "ProductTemplate")
@DiscriminatorValue("PRODUCT")
@NamedQueries({ @NamedQuery(name = "ProductTemplate.countActive", query = "SELECT COUNT(*) FROM ProductTemplate WHERE lifeCycleStatus='ACTIVE' "),
        @NamedQuery(name = "ProductTemplate.countDisabled", query = "SELECT COUNT(*) FROM ProductTemplate WHERE lifeCycleStatus<>'ACTIVE'"),
        @NamedQuery(name = "ProductTemplate.countExpiring", query = "SELECT COUNT(*) FROM ProductTemplate WHERE :nowMinusXDay<validity.to and validity.to<=NOW()") })
public class ProductTemplate extends ProductOffering {

    private static final long serialVersionUID = 6380565206599659432L;

    @Transient
    public static final String CF_CATALOG_PRICE = "CATALOG_PRICE";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_product_templ_charge_templ", joinColumns = @JoinColumn(name = "product_template_id"), inverseJoinColumns = @JoinColumn(name = "product_charge_template_id"))
    private List<ProductChargeTemplate> productChargeTemplates = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_product_model_id")
    private BusinessProductModel businessProductModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicing_calendar_id")
    private Calendar invoicingCalendar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_product_wallet_template", joinColumns = @JoinColumn(name = "product_template_id"), inverseJoinColumns = @JoinColumn(name = "wallet_template_id"))
    @OrderColumn(name = "indx")
    private List<WalletTemplate> walletTemplates = new ArrayList<WalletTemplate>();
    
    
	/**
	 * status of product type of {@link ProductStatusEnum}
	 */
	@Column(name = "product_status", nullable = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	private ProductStatusEnum status;
	
	/**
	 * status date : modified automatically when the status change
	 */
	@Column(name = "status_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date statusDate;
	
	
	/**
	 * family of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_line_id", referencedColumnName = "id")
	private ProductLine productLine;
	
	/**
	 * brand of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_brand_id", referencedColumnName = "id")
	private CustomerBrand brand;
	
	/**
	 * reference: unique for product if it has a reference
	 */
	@Column(name = "reference", length = 50)
	@Size(max = 50)
	private String reference;
	
	/**
	 * model : it is an upgrade for the product
	 */
	@Column(name = "model", length = 50)
	@Size(max = 20)
	private String model;
	
	/**
	 * model children : display all older model 
	 */
	@ElementCollection
	@Column(name = "model_chlidren")
	@CollectionTable(name = "cpq_product_model_children", joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private Set<String> modelChlidren;
	

	/**
	 * list of discount attached to this product
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
				name = "cpq_product_discount_plan",
				joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "discount_id", referencedColumnName = "id")				
			)
	private Set<DiscountPlan> discountList = new HashSet<>();
	
	
	/**
	 * flag that indicate if true  discount list will have a specific 
	 * list otherwise all available discount attached to this product will be displayed
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "discount_flag", nullable = false)
	@NotNull
	private boolean discountFlag;

    public void addProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
        if (getProductChargeTemplates() == null) {
            productChargeTemplates = new ArrayList<>();
        }
        if (!productChargeTemplates.contains(productChargeTemplate)) {
            productChargeTemplates.add(productChargeTemplate);
        }
    }

    public void addWalletTemplate(WalletTemplate walletTemplate) {
        if (getWalletTemplates() == null) {
            walletTemplates = new ArrayList<>();
        }
        if (!walletTemplates.contains(walletTemplate)) {
            walletTemplates.add(walletTemplate);
        }
    }

    public List<ProductChargeTemplate> getProductChargeTemplates() {
        return productChargeTemplates;
    }

    public void setProductChargeTemplates(List<ProductChargeTemplate> productChargeTemplates) {
        this.productChargeTemplates = productChargeTemplates;
    }

    public BusinessProductModel getBusinessProductModel() {
        return businessProductModel;
    }

    public void setBusinessProductModel(BusinessProductModel businessProductModel) {
        this.businessProductModel = businessProductModel;
    }

    public Calendar getInvoicingCalendar() {
        return invoicingCalendar;
    }

    public void setInvoicingCalendar(Calendar invoicingCalendar) {
        this.invoicingCalendar = invoicingCalendar;
    }

    public List<WalletTemplate> getWalletTemplates() {
        return walletTemplates;
    }

    public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
        this.walletTemplates = walletTemplates;
    }
    
    

    /**
	 * @return the status
	 */
	public ProductStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ProductStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the productLine
	 */
	public ProductLine getProductLine() {
		return productLine;
	}

	/**
	 * @param productLine the productLine to set
	 */
	public void setProductLine(ProductLine productLine) {
		this.productLine = productLine;
	}

	/**
	 * @return the brand
	 */
	public CustomerBrand getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(CustomerBrand brand) {
		this.brand = brand;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the modelChlidren
	 */
	public Set<String> getModelChlidren() {
		return modelChlidren;
	}

	/**
	 * @param modelChlidren the modelChlidren to set
	 */
	public void setModelChlidren(Set<String> modelChlidren) {
		this.modelChlidren = modelChlidren;
	}

	/**
	 * @return the discountList
	 */
	public Set<DiscountPlan> getDiscountList() {
		return discountList;
	}

	/**
	 * @param discountList the discountList to set
	 */
	public void setDiscountList(Set<DiscountPlan> discountList) {
		this.discountList = discountList;
	}



	/**
	 * @return the discountFlag
	 */
	public boolean isDiscountFlag() {
		return discountFlag;
	}

	/**
	 * @param discountFlag the discountFlag to set
	 */
	public void setDiscountFlag(boolean discountFlag) {
		this.discountFlag = discountFlag;
	}

	@Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }
    
    
}
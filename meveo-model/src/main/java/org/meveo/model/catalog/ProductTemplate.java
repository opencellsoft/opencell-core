package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "PRODUCT")
@DiscriminatorValue("PRODUCT")
@NamedQueries({
		@NamedQuery(name = "ProductTemplate.countActive", query = "SELECT COUNT(*) FROM ProductTemplate WHERE disabled=false and provider=:provider"),
		@NamedQuery(name = "ProductTemplate.countDisabled", query = "SELECT COUNT(*) FROM ProductTemplate WHERE disabled=true and provider=:provider"),
		@NamedQuery(name = "ProductTemplate.countExpiring", query = "SELECT COUNT(*) FROM ProductTemplate WHERE :nowMinus1Day<validTo and validTo > NOW() and provider=:provider") })
public class ProductTemplate extends ProductOffering {

	private static final long serialVersionUID = 6380565206599659432L;
	
	@Transient
	public static final String CF_CATALOG_PRICE = "CATALOG_PRICE";

	@OneToMany(mappedBy = "productTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductChargeTemplate> productChargeTemplates = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "BUSINESS_PRODUCT_MODEL_ID")
	private BusinessProductModel businessProductModel;

	@ManyToOne
	@JoinColumn(name = "INVOICING_CALENDAR_ID")
	private Calendar invoicingCalendar;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_PRODUCT_WALLET_TEMPLATE", joinColumns = @JoinColumn(name = "PRODUCT_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "WALLET_TEMPLATE_ID"))
	@OrderColumn(name = "INDX")
	private List<WalletTemplate> walletTemplates = new ArrayList<WalletTemplate>();

	public List<ProductChargeTemplate> getProductChargeTemplates() {
		if (productChargeTemplates == null) {
			productChargeTemplates = new ArrayList<>();
		}
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

	@Override
	public ICustomFieldEntity[] getParentCFEntities() {
		return null;
	}

}

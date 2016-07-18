package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import org.meveo.model.ICustomFieldEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@DiscriminatorValue("PRODUCT")
public class ProductTemplate extends ProductOffering {

	private static final long serialVersionUID = 6380565206599659432L;

	@OneToOne
	@JoinColumn(name = "PRODUCT_CHARGE_TMPL_ID")
	private ProductChargeTemplate productChargeTemplate;

	@ManyToOne
	@JoinColumn(name = "BUSINESS_PRODUCT_MODEL_ID")
	private BusinessProductModel businessProductModel;
	
    @ManyToOne
    @JoinColumn(name = "INVOICING_CALENDAR_ID")
    private Calendar invoicingCalendar;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_PRODUCT_WALLET_TEMPLATE", joinColumns = @JoinColumn(name = "PRODUCT_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "WALLET_TEMPLATE_ID"))
	@OrderColumn(name = "INDX")
	private List<WalletTemplate> walletTemplates;

	public ProductChargeTemplate getProductChargeTemplate() {
		return productChargeTemplate;
	}

	public void setProductChargeTemplate(ProductChargeTemplate oneShotChargeTemplate) {
		this.productChargeTemplate = oneShotChargeTemplate;
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

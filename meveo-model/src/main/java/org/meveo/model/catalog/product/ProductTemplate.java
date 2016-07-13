package org.meveo.model.catalog.product;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;

/**
 * @author Edward P. Legaspi
 */
@Entity
@DiscriminatorValue("PRODUCT")
public class ProductTemplate extends ProductOffering {

	private static final long serialVersionUID = 6380565206599659432L;

	@ManyToOne
	@JoinColumn(name = "ONE_SHOT_CHARGE_TMPL_ID")
	private OneShotChargeTemplate oneShotChargeTemplate;

	@ManyToOne
	@JoinColumn(name = "BUSINESS_PRODUCT_MODEL_ID")
	private BusinessProductModel businessProductModel;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_PRODUCT_WALLET_TEMPLATE", joinColumns = @JoinColumn(name = "PRODUCT_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "WALLET_TEMPLATE_ID"))
	@OrderColumn(name = "INDX")
	private List<WalletTemplate> walletTemplates;

	public OneShotChargeTemplate getOneShotChargeTemplate() {
		return oneShotChargeTemplate;
	}

	public void setOneShotChargeTemplate(OneShotChargeTemplate oneShotChargeTemplate) {
		this.oneShotChargeTemplate = oneShotChargeTemplate;
	}

	public BusinessProductModel getBusinessProductModel() {
		return businessProductModel;
	}

	public void setBusinessProductModel(BusinessProductModel businessProductModel) {
		this.businessProductModel = businessProductModel;
	}

	public List<WalletTemplate> getWalletTemplates() {
		return walletTemplates;
	}

	public void setWalletTemplates(List<WalletTemplate> walletTemplates) {
		this.walletTemplates = walletTemplates;
	}

}

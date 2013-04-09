package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "BILLING_WALLET_TEMPLATE")
public class WalletTemplate extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "WALLET_TYPE")
	@Enumerated(EnumType.STRING)
	private BillingWalletTypeEnum walletType;

	public BillingWalletTypeEnum getWalletType() {
		return walletType;
	}

	public void setWalletType(BillingWalletTypeEnum walletType) {
		this.walletType = walletType;
	}

}

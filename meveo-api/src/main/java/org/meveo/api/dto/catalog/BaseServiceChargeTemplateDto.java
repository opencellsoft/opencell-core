package org.meveo.api.dto.catalog;

import java.io.Serializable;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseServiceChargeTemplateDto implements Serializable {

	private static final long serialVersionUID = 3771281513359110575L;

	private String code;
	private WalletsDto wallets;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "BaseServiceChargeTemplateDto [code=" + code + ", wallets=" + wallets + "]";
	}

	public WalletsDto getWallets() {
		if (wallets == null)
			wallets = new WalletsDto();
		return wallets;
	}

	public void setWallets(WalletsDto wallets) {
		this.wallets = wallets;
	}

}

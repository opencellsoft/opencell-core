package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetWalletTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetWalletTemplateResponseDto extends BaseResponse {

	private static final long serialVersionUID = 4964282153736897078L;

	private WalletTemplateDto walletTemplate;

	public WalletTemplateDto getWalletTemplate() {
		return walletTemplate;
	}

	public void setWalletTemplate(WalletTemplateDto walletTemplate) {
		this.walletTemplate = walletTemplate;
	}

	@Override
	public String toString() {
		return "GetWalletTemplateResponseDto [walletTemplate=" + walletTemplate + "]";
	}

}

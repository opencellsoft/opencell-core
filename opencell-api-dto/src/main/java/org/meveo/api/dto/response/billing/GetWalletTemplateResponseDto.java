package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetWalletTemplateResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetWalletTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetWalletTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4964282153736897078L;

    /** The wallet template. */
    private WalletTemplateDto walletTemplate;

    /**
     * Gets the wallet template.
     *
     * @return the wallet template
     */
    public WalletTemplateDto getWalletTemplate() {
        return walletTemplate;
    }

    /**
     * Sets the wallet template.
     *
     * @param walletTemplate the new wallet template
     */
    public void setWalletTemplate(WalletTemplateDto walletTemplate) {
        this.walletTemplate = walletTemplate;
    }

    @Override
    public String toString() {
        return "GetWalletTemplateResponseDto [walletTemplate=" + walletTemplate + "]";
    }
}
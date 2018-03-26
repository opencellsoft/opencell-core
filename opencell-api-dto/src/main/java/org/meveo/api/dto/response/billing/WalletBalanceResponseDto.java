package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.AmountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * Wallet balance
 * 
 * @author Andrius Karpavicius
 * @since 5.0.1
 */
@XmlRootElement(name = "WalletBalanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalletBalanceResponseDto extends BaseResponse {

    private static final long serialVersionUID = -6281416941898150225L;

    /**
     * Balance amounts
     */
    private AmountsDto amounts;

    public AmountsDto getAmounts() {
        return amounts;
    }

    public void setAmounts(AmountsDto amounts) {
        this.amounts = amounts;
    }

    @Override
    public String toString() {
        return "WalletBalanceResponseDto [amounts=" + amounts + "]";
    }
}
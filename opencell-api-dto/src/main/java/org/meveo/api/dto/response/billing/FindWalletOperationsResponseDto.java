package org.meveo.api.dto.response.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class FindWalletOperationsResponseDto.
 *
 * @author Edward P. Legaspi
 */

@XmlRootElement(name = "FindWalletOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FindWalletOperationsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1554482700055388991L;

    /** The wallet operations. */
    private List<WalletOperationDto> walletOperations;

    /**
     * Gets the wallet operations.
     *
     * @return the wallet operations
     */
    public List<WalletOperationDto> getWalletOperations() {
        if (walletOperations == null)
            walletOperations = new ArrayList<WalletOperationDto>();
        return walletOperations;
    }

    /**
     * Sets the wallet operations.
     *
     * @param walletOperations the new wallet operations
     */
    public void setWalletOperations(List<WalletOperationDto> walletOperations) {
        this.walletOperations = walletOperations;
    }

}

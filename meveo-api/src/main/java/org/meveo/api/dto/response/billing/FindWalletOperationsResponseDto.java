package org.meveo.api.dto.response.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/

@XmlRootElement(name = "FindWalletOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FindWalletOperationsResponseDto extends BaseResponse {

	private static final long serialVersionUID = -1554482700055388991L;

	private List<WalletOperationDto> walletOperations;

	public List<WalletOperationDto> getWalletOperations() {
		if (walletOperations == null)
			walletOperations = new ArrayList<WalletOperationDto>();
		return walletOperations;
	}

	public void setWalletOperations(List<WalletOperationDto> walletOperations) {
		this.walletOperations = walletOperations;
	}

}

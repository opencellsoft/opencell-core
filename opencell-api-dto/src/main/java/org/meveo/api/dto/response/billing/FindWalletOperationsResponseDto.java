/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

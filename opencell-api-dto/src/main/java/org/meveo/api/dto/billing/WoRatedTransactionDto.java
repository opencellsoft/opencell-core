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

package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;

/**
 * WoRatedTransactionDto : A dto class containing a minimum of Rated Transaction informations like "id" and "status" ... 
 */
@XmlRootElement(name = "ratedTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class WoRatedTransactionDto extends BusinessEntityDto {
    

    /**
     * Default constructor
     */
    public WoRatedTransactionDto() {
    }
    
    public WoRatedTransactionDto(RatedTransaction rt) {
        this.id = rt.getId();
        this.status = rt.getStatus();
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    
    /** The status. */
    private RatedTransactionStatusEnum status;

    /**
     * @return the status
     */
    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

}

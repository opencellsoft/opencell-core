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

package org.meveo.api.dto.response.account;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.model.billing.BillingAccount;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class GetBillingAccountResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetBillingAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({"tagCodes"})
public class GetBillingAccountResponseDto extends BillingAccountDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8538364402251002467L;

    /** The billing account. */
    private BillingAccountDto billingAccount;
    
    /** The tags. */ 
    @XmlElementWrapper(name = "tags")
    @XmlElement(name = "tags")
    private Set<TagDto> tags = new HashSet<>();
    
    
    private ActionStatus actionStatus = new ActionStatus();

    /**
     * Instantiates a new base response.
     */
    public GetBillingAccountResponseDto() {
        actionStatus = new ActionStatus();
    }

    /**
     * Instantiates a new base response.
     *
     * @param status the status
     * @param errorCode the error code
     * @param message the message
     */
    public GetBillingAccountResponseDto(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        actionStatus = new ActionStatus(status, errorCode, message);
    }
    

    public GetBillingAccountResponseDto(BillingAccount e) {
    	super(e);
    	if(e.getTags() != null && !e.getTags().isEmpty()) {
    		tags = e.getTags().stream().map(t -> {
    			final TagDto dto = new TagDto(t);
    			return dto;
    		}).collect(Collectors.toSet());
    	}
    }
 
	/**
     * Gets the billing account.
     *
     * @return the billing account
     */
    public BillingAccountDto getBillingAccount() {
        return billingAccount;
    }

    /**
     * Sets the billing account.
     *
     * @param billingAccount the new billing account
     */
    public void setBillingAccount(BillingAccountDto billingAccount) {
        this.billingAccount = billingAccount;
    }
    
    

    /**
	 * @return the tags
	 */
	public Set<TagDto> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<TagDto> tags) {
		this.tags = tags;
	}
	
	

	/**
	 * @return the actionStatus
	 */
	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	@Override
    public String toString() {
        return "GetBillingAccountResponse [billingAccount=" + billingAccount + ", toString()=" + super.toString() + "]";
    }
}
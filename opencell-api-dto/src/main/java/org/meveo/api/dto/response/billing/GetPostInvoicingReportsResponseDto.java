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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.billing.PostInvoicingReportsDTO;

/**
 * The Class GetPostInvoicingReportsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetPostInvoicingReportsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPostInvoicingReportsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The post invoicing reports DTO. */
    private PostInvoicingReportsDTO postInvoicingReportsDTO;

    /**
     * Instantiates a new gets the post invoicing reports response dto.
     */
    public GetPostInvoicingReportsResponseDto() {

    }

    /**
     * Gets the post invoicing reports DTO.
     *
     * @return the PostInvoicingReportsDTO
     */
    public PostInvoicingReportsDTO getPostInvoicingReportsDTO() {
        return postInvoicingReportsDTO;
    }

    /**
     * Sets the post invoicing reports DTO.
     *
     * @param postInvoicingReportsDTO the PostInvoicingReportsDTO to set
     */
    public void setPostInvoicingReportsDTO(PostInvoicingReportsDTO postInvoicingReportsDTO) {
        this.postInvoicingReportsDTO = postInvoicingReportsDTO;
    }

    @Override
    public String toString() {
        return "GetPostInvoicingReportsResponseDto [PostInvoicingReportsDTO=" + postInvoicingReportsDTO + "]";
    }
}
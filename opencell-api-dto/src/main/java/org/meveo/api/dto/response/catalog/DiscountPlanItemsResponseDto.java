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

package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class DiscountPlanItemsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "DiscountPlanItemsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4771102434084711881L;

    /** The discount plan items. */
    @XmlElementWrapper(name = "discountPlanItems")
    @XmlElement(name = "discountPlanItem")
    private List<DiscountPlanItemDto> discountPlanItems;

    /**
     * Gets the discount plan items.
     *
     * @return the discount plan items
     */
    public List<DiscountPlanItemDto> getDiscountPlanItems() {
        if ( discountPlanItems == null )
            discountPlanItems = new ArrayList<>();
        return discountPlanItems;
    }

    /**
     * Sets the discount plan items.
     *
     * @param discountPlanItems the new discount plan items
     */
    public void setDiscountPlanItems(List<DiscountPlanItemDto> discountPlanItems) {
        this.discountPlanItems = discountPlanItems;
    }

}

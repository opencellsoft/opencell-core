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

import java.util.HashMap;
import java.util.Map;

import org.meveo.api.dto.response.BaseResponse;

/**
 * A Dto class holding the Rate subscription response.
 */
public class RateSubscriptionResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2682870156756952749L;
    
    /** The total rated charges. */
    private Integer totalRatedCharges = 0;
    
    /** The rated charges result. 
     *  Map containing the charge Id and the rating result
     * */
    private Map<Long, String> ratedChargesResult = new HashMap<Long, String>();

    /**
     * @return the totalRatedCharges
     */
    public Integer getTotalRatedCharges() {
        return totalRatedCharges;
    }

    /**
     * @return the ratedChargesResult
     */
    public Map<Long, String> getRatedChargesResult() {
        return ratedChargesResult;
    }

    public void addResult(Long chargeId, int nbRating) {
        if (nbRating >= 1) {
            this.ratedChargesResult.put(chargeId, String.format("Charge [id=%d] was rated %d times ", chargeId, nbRating ) ); 
            this.totalRatedCharges = totalRatedCharges + nbRating;
        } else {
            this.ratedChargesResult.put(chargeId, String.format("Charge [id=%d] not rated ", chargeId) ); 
        }
    }

}

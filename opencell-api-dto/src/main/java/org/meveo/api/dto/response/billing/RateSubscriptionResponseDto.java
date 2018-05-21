package org.meveo.api.dto.response.billing;

import java.util.HashMap;
import java.util.Map;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class RateSubscriptionResponseDto.
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

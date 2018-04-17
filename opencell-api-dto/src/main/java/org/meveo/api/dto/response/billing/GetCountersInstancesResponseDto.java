package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.billing.CountersInstancesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetCountersInstancesResponseDto.
 * 
 *  @author anasseh
 */
public class GetCountersInstancesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8796230062356654392L;

    /** The counters instances. */
    private CountersInstancesDto countersInstances = new CountersInstancesDto();

    /**
     * Gets the counters instances.
     *
     * @return the counters instances
     */
    public CountersInstancesDto getCountersInstances() {
        return countersInstances;
    }

    /**
     * Sets the counters instances.
     *
     * @param countersInstances the new counters instances
     */
    public void setCountersInstances(CountersInstancesDto countersInstances) {
        this.countersInstances = countersInstances;
    }

    @Override
    public String toString() {
        return "GetCountersInstancesResponseDto [countersInstances=" + countersInstances + ", toString()=" + super.toString() + "]";
    }
}
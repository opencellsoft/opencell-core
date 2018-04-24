package org.meveo.api.dto.response.billing;

import org.meveo.api.dto.billing.DueDateDelayDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetDueDateDelayResponseDto.
 *
 * @author Edward P. Legaspi
 */
public class GetDueDateDelayResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7106268154657860158L;

    /** The due date delay. */
    private DueDateDelayDto dueDateDelay;

    /**
     * Gets the due date delay.
     *
     * @return the due date delay
     */
    public DueDateDelayDto getDueDateDelay() {
        return dueDateDelay;
    }

    /**
     * Sets the due date delay.
     *
     * @param dueDateDelay the new due date delay
     */
    public void setDueDateDelay(DueDateDelayDto dueDateDelay) {
        this.dueDateDelay = dueDateDelay;
    }

    @Override
    public String toString() {
        return "GetDueDateDelayResponseDto [dueDateDelay=" + dueDateDelay + "]";
    }
}
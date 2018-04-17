package org.meveo.api.dto.response.account;

import org.meveo.api.dto.account.ParentEntitiesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ParentEntitiesResponseDto.
 *
 * @author Tony Alejandro.
 */
public class ParentEntitiesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The parent entities. */
    private ParentEntitiesDto parentEntities;

    /**
     * Gets the parent entities.
     *
     * @return the parent entities
     */
    public ParentEntitiesDto getParentEntities() {
        return parentEntities;
    }

    /**
     * Sets the parent entities.
     *
     * @param parentEntities the new parent entities
     */
    public void setParentEntities(ParentEntitiesDto parentEntities) {
        this.parentEntities = parentEntities;
    }

    @Override
    public String toString() {
        return "ParentEntitiesResponseDto [parentEntities=" + parentEntities + ", toString()=" + super.toString() + "]";
    }
}
package org.meveo.api.dto.response;

import org.meveo.api.dto.account.ParentEntitiesDto;

/**
 * The Class ParentListResponse.
 *
 * @author Tony Alejandro.
 */
public class ParentListResponse extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The parents. */
    private ParentEntitiesDto parents;

    /**
     * Gets the parents.
     *
     * @return the parents
     */
    public ParentEntitiesDto getParents() {
        return parents;
    }

    /**
     * Sets the parents.
     *
     * @param parents the new parents
     */
    public void setParents(ParentEntitiesDto parents) {
        this.parents = parents;
    }

    @Override
    public String toString() {
        return "ParentListResponse [parents=" + parents + "]";
    }
}
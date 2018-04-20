package org.meveo.api.dto;

import java.util.Date;

/**
 * Equivalent of AuditableEntity in DTO. Contain the entity creation and modification dates.
 * 
 * @author Edward P. Legaspi
 */
public abstract class AuditableDto extends BaseDto {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1040133977061424749L;

    /**
     * created date.
     */
    private Date created;

    /**
     * @return created date.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created created date
     */
    public void setCreated(Date created) {
        this.created = created;
    }

}

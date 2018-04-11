package org.meveo.api.dto;

import org.meveo.model.BusinessEntity;
import org.meveo.model.IEnable;

/**
 * Equivalent of EnableBusinessEntity in DTO
 * 
 * @author Andrius Karpavicius
 * @since 5.1
 * 
 */
public abstract class EnableBusinessDto extends BusinessDto implements IEnableDto {

    private static final long serialVersionUID = -6333871594207883527L;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

    public EnableBusinessDto() {
    }

    public EnableBusinessDto(IEnable e) {
        super((BusinessEntity) e);
        if (e != null) {
            setDisabled(e.isDisabled());
        }
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isDisabled() {
        return disabled;
    }
}
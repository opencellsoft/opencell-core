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

    /**
     * Instantiates a new EnableBusinessDto
     */
    public EnableBusinessDto() {
    }

    /**
     * Converts JPA entity to a DTO
     * 
     * @param entity Entity to convert
     */
    public EnableBusinessDto(IEnable entity) {
        super((BusinessEntity) entity);
        if (entity != null) {
            setDisabled(entity.isDisabled());
        }
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }
}
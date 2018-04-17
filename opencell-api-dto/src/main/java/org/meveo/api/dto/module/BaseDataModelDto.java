package org.meveo.api.dto.module;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;

/**
 * The Class BaseDataModelDto.
 * 
 * @author andrius
 */
abstract class BaseDataModelDto extends BusinessDto implements IEntity {

    /**
     * Instantiates a new base data model dto.
     */
    public BaseDataModelDto() {

    }

    /**
     * Instantiates a new base data model dto.
     *
     * @param businessEntity the businessEntity
     */
    public BaseDataModelDto(BusinessEntity businessEntity) {
        super(businessEntity);
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

}
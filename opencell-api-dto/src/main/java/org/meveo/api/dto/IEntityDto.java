package org.meveo.api.dto;

/**
 * Entity that contains identifier
 * 
 * @author Andrius Karpavicius
 */
public interface IEntityDto {

    /**
     * @return Identifier
     */
    public Long getId();

    /**
     * @param id Identifier
     */
    public void setId(Long id);
}
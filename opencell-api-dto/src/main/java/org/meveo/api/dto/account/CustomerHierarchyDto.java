package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.SellersDto;

/**
 * The Class CustomerHierarchyDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CustomerHierarchy")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerHierarchyDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7727040970378439778L;

    /** The sellers. */
    @XmlElement(required = true)
    private SellersDto sellers;

    /**
     * Gets the sellers.
     *
     * @return the sellers
     */
    public SellersDto getSellers() {
        return sellers;
    }

    /**
     * Sets the sellers.
     *
     * @param sellers the new sellers
     */
    public void setSellers(SellersDto sellers) {
        this.sellers = sellers;
    }

}
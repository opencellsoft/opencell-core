package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.payments.CreditCategory;

/**
 * The Class CreditCategoryDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CreditCategory")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCategoryDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9096295121437014513L;

    /**
     * Instantiates a new credit category dto.
     */
    public CreditCategoryDto() {

    }

    /**
     * Instantiates a new credit category dto.
     *
     * @param creditCategory the CreditCategory entity
     */
    public CreditCategoryDto(CreditCategory creditCategory) {
        super(creditCategory);
    }

    @Override
    public String toString() {
        return "CreditCategoryDto [code=" + getCode() + ", description=" + getDescription() + "]";
    }

}
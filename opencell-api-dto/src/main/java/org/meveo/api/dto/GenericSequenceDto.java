package org.meveo.api.dto;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

@XmlRootElement
@XmlAccessorType(FIELD)
public class GenericSequenceDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4763606403469751014L;

    /** The prefix EL. */
    private String prefixEL;

    /** The invoice sequence code. */
    private String sequencePattern;

    /** The sequence size. */
    @Deprecated
    private Integer sequenceSize;

    /** The current invoice nb. */
    @Deprecated
    private Long currentInvoiceNb;

}

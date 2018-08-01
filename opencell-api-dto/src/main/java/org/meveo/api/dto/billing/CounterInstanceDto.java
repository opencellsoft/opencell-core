package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.CounterInstance;

/**
 * The Class CounterInstanceDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterInstanceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -72154111229222183L;

    /**
     * Instantiates a new counter instance dto.
     */
    public CounterInstanceDto() {
    }

    /**
     * Instantiates a new counter instance dto.
     *
     * @param counterInstance the counter instance
     */
    public CounterInstanceDto(CounterInstance counterInstance) {
        super(counterInstance);
    }

    @Override
    public String toString() {
        return "CounterInstanceDto [code=" + code + ", description=" + description + "]";
    }
}
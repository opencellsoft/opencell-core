package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TriggeredEdrTemplatesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TriggeredEdrTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrTemplatesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5790679004639676207L;

    /** The triggered edr. */
    private List<TriggeredEdrTemplateDto> triggeredEdr;

    /**
     * Gets the triggered edr.
     *
     * @return the triggered edr
     */
    public List<TriggeredEdrTemplateDto> getTriggeredEdr() {
        if (triggeredEdr == null) {
            triggeredEdr = new ArrayList<TriggeredEdrTemplateDto>();
        }
        return triggeredEdr;
    }

    /**
     * Sets the triggered edr.
     *
     * @param triggeredEdr the new triggered edr
     */
    public void setTriggeredEdr(List<TriggeredEdrTemplateDto> triggeredEdr) {
        this.triggeredEdr = triggeredEdr;
    }

    @Override
    public String toString() {
        return "TriggeredEdrTemplatesDto [triggeredEdr=" + triggeredEdr + ", toString()=" + super.toString() + "]";
    }
}
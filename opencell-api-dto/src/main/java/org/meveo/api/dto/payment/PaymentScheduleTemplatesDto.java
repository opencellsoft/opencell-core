/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class PaymentScheduleTemplatesDto.
 *
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleTemplatesDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleTemplatesDto extends SearchResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623228L;

    /** The PaymentScheduleTemplate Dtos. */
    @XmlElementWrapper
    @XmlElement(name = "template")
    private List<PaymentScheduleTemplateDto> templates = new ArrayList<>();
    
    
    
    /**
     * Instantiates a new payment schedule templates dto.
     */
    public PaymentScheduleTemplatesDto() {
         
    }

    /**
     * Gets the templates.
     *
     * @return the templates
     */
    public List<PaymentScheduleTemplateDto> getTemplates() {
        return templates;
    }

    /**
     * Sets the templates.
     *
     * @param templates the templates to set
     */
    public void setTemplates(List<PaymentScheduleTemplateDto> templates) {
        this.templates = templates;
    }
    
    

}

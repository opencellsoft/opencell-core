package org.meveo.api.dto.catalog;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.UsageChargeTemplate;

/**
 * The Class UsageChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "UsageChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageChargeTemplateDto extends ChargeTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -192169359113319490L;

    /** The wilcard. */
    static String WILCARD = null;

    /** The filter param 1. */
    @Size(min = 0, max = 255)
    private String filterParam1 = WILCARD;

    /** The filter param 2. */
    @Size(min = 0, max = 255)
    private String filterParam2 = WILCARD;

    /** The filter param 3. */
    @Size(min = 0, max = 255)
    private String filterParam3 = WILCARD;

    /** The filter param 4. */
    @Size(min = 0, max = 255)
    private String filterParam4 = WILCARD;

    /** The filter expression. */
    @Size(max = 2000)
    private String filterExpression = null;

    /** The priority. */
    private int priority = 1;

    /**
     * Instantiates a new usage charge template dto.
     */
    public UsageChargeTemplateDto() {

    }

    /**
     * Instantiates a new usage charge template dto.
     *
     * @param usageChargeTemplate the UsageChargeTemplate entity
     * @param customFieldInstances the custom field instances
     */
    public UsageChargeTemplateDto(UsageChargeTemplate usageChargeTemplate, CustomFieldsDto customFieldInstances) {
        super(usageChargeTemplate, customFieldInstances);
        filterParam1 = usageChargeTemplate.getFilterParam1();
        filterParam2 = usageChargeTemplate.getFilterParam2();
        filterParam3 = usageChargeTemplate.getFilterParam3();
        filterParam4 = usageChargeTemplate.getFilterParam4();
        setFilterExpression(usageChargeTemplate.getFilterExpression());
        priority = usageChargeTemplate.getPriority();
    }

    /**
     * Gets the filter param 1.
     *
     * @return the filter param 1
     */
    public String getFilterParam1() {
        return filterParam1;
    }

    /**
     * Sets the filter param 1.
     *
     * @param filterParam1 the new filter param 1
     */
    public void setFilterParam1(String filterParam1) {
        this.filterParam1 = filterParam1;
    }

    /**
     * Gets the filter param 2.
     *
     * @return the filter param 2
     */
    public String getFilterParam2() {
        return filterParam2;
    }

    /**
     * Sets the filter param 2.
     *
     * @param filterParam2 the new filter param 2
     */
    public void setFilterParam2(String filterParam2) {
        this.filterParam2 = filterParam2;
    }

    /**
     * Gets the filter param 3.
     *
     * @return the filter param 3
     */
    public String getFilterParam3() {
        return filterParam3;
    }

    /**
     * Sets the filter param 3.
     *
     * @param filterParam3 the new filter param 3
     */
    public void setFilterParam3(String filterParam3) {
        this.filterParam3 = filterParam3;
    }

    /**
     * Gets the filter param 4.
     *
     * @return the filter param 4
     */
    public String getFilterParam4() {
        return filterParam4;
    }

    /**
     * Sets the filter param 4.
     *
     * @param filterParam4 the new filter param 4
     */
    public void setFilterParam4(String filterParam4) {
        this.filterParam4 = filterParam4;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }


    /**
     * Gets the filter expression.
     *
     * @return the filter expression
     */
    public String getFilterExpression() {
        return filterExpression;
    }

    /**
     * Sets the filter expression.
     *
     * @param filterExpression the new filter expression
     */
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }
    
    @Override
    public String toString() {
        return "UsageChargeTemplateDto [filterParam1=" + filterParam1 + ", filterParam2=" + filterParam2 + ", filterParam3=" + filterParam3 + ", filterParam4=" + filterParam4
                + ", filterExpression=" + getFilterExpression() + ", priority=" + priority + "]";
    }
}
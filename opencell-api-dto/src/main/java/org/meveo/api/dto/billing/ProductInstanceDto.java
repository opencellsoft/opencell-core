package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;

/**
 * The Class ProductInstanceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstanceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6853333357907373635L;

    /** The application date. */
    private Date applicationDate;

    /** The quantity. */
    protected BigDecimal quantity = BigDecimal.ONE;

    /** The order number. */
    private String orderNumber;

    /** The product charge instances. */
    @XmlElement(name = "productChargeInstance")
    private List<ProductChargeInstanceDto> productChargeInstances = new ArrayList<>();

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new product instance dto.
     */
    public ProductInstanceDto() {

    }

    /**
     * Instantiates a new product instance dto.
     *
     * @param productInstance the ProductInstance entity
     * @param customFieldInstances the custom field instances
     */
    public ProductInstanceDto(ProductInstance productInstance, CustomFieldsDto customFieldInstances) {

        id = productInstance.getId();
        code = productInstance.getCode();
        description = productInstance.getDescription();
        applicationDate = productInstance.getApplicationDate();
        quantity = productInstance.getQuantity();
        orderNumber = productInstance.getOrderNumber();

        if (productInstance.getProductChargeInstances() != null) {
            for (ProductChargeInstance pci : productInstance.getProductChargeInstances()) {
                productChargeInstances.add(new ProductChargeInstanceDto(pci));
            }
        }

        customFields = customFieldInstances;
    }

    /**
     * Gets the application date.
     *
     * @return the application date
     */
    public Date getApplicationDate() {
        return applicationDate;
    }

    /**
     * Sets the application date.
     *
     * @param applicationDate the new application date
     */
    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

}
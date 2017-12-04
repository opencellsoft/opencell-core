package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstanceDto extends BusinessDto {

    private static final long serialVersionUID = 6853333357907373635L;

    private Date applicationDate;
    protected BigDecimal quantity = BigDecimal.ONE;
    private String orderNumber;

    @XmlElement(name = "productChargeInstance")
    private List<ProductChargeInstanceDto> productChargeInstances = new ArrayList<>();

    private CustomFieldsDto customFields;

    public ProductInstanceDto() {

    }

    public ProductInstanceDto(ProductInstance e, CustomFieldsDto customFieldInstances) {

        id = e.getId();
        code = e.getCode();
        description = e.getDescription();
        applicationDate = e.getApplicationDate();
        quantity = e.getQuantity();
        orderNumber = e.getOrderNumber();

        if (e.getProductChargeInstances() != null) {
            for (ProductChargeInstance pci : e.getProductChargeInstances()) {
                productChargeInstances.add(new ProductChargeInstanceDto(pci));
            }
        }

        customFields = customFieldInstances;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

}

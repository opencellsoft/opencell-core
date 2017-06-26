package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.ProductInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstanceDto extends BaseDto {

	private static final long serialVersionUID = 6853333357907373635L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute()
	private String description;

	private Date applicationDate;
	protected BigDecimal quantity = BigDecimal.ONE;
	private String orderNumber;

	@XmlElementWrapper(name = "productChargeInstances")
	@XmlElement(name = "productChargeInstance")
	private List<ChargeInstanceDto> productChargeInstances;

	private CustomFieldsDto customFields = new CustomFieldsDto();

	public ProductInstanceDto() {

	}

	public ProductInstanceDto(ProductInstance e, CustomFieldsDto customFieldInstances) {
		code = e.getCode();
		description = e.getDescription();
		applicationDate = e.getApplicationDate();
		quantity = e.getQuantity();
		orderNumber = e.getOrderNumber();

		if (e.getProductChargeInstances() != null) {
			productChargeInstances = new ArrayList<ChargeInstanceDto>();

			for (ProductChargeInstance ci : e.getProductChargeInstances()) {
				productChargeInstances.add(new ChargeInstanceDto(ci.getCode(), ci.getDescription(),
						ci.getStatus().name(), ci.getAmountWithTax(), ci.getAmountWithoutTax(),
						ci.getSeller().getCode(), ci.getUserAccount().getCode()));
			}
		}

		customFields = customFieldInstances;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public List<ChargeInstanceDto> getProductChargeInstances() {
		return productChargeInstances;
	}

	public void setProductChargeInstances(List<ChargeInstanceDto> productChargeInstances) {
		this.productChargeInstances = productChargeInstances;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

}

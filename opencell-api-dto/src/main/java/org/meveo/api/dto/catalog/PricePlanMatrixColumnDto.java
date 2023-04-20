package org.meveo.api.dto.catalog;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixColumn;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixColumnDto extends BaseEntityDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6095766234485840716L;

    @Schema(description = "The price plan matrix column code")
	@NotNull
    private String code;

    @Schema(description = "The price plan matrix code")
    private String pricePlanMatrixCode;

    @Schema(description = "The price plan matrix version")
    private int pricePlanMatrixVersion;

    @Schema(description = "The product code")
    private String productCode;

    @Schema(description = "The offer template code")
    private String offerTemplateCode;

    @Schema(description = "The attribute code")
    private String attributeCode;

    @Schema(description = "The position of price plan column")
    private int position;

    @Schema(description = "The type of the price plan column")
    private ColumnTypeEnum type;

    @Schema(description = "The el value of price plan matrix column")
    private String elValue;

    @Schema(description = "Indicate if the price plan matrix column is a range")
    private Boolean isRange;

    @Schema(description = "Price plan matrix identifier")
    private Long id;

    public PricePlanMatrixColumnDto() {
    }

    public PricePlanMatrixColumnDto(PricePlanMatrixColumn pricePlanMatrixColumn) {
        this.code = pricePlanMatrixColumn.getCode();
        this.pricePlanMatrixCode = pricePlanMatrixColumn.getPricePlanMatrixVersion().getPricePlanMatrix().getCode();
        this.pricePlanMatrixVersion = pricePlanMatrixColumn.getPricePlanMatrixVersion().getCurrentVersion();
        if(pricePlanMatrixColumn.getProduct() != null)
        	this.productCode = pricePlanMatrixColumn.getProduct().getCode();
        if(pricePlanMatrixColumn.getOfferTemplate() != null)
        	this.offerTemplateCode = pricePlanMatrixColumn.getOfferTemplate().getCode();
        this.attributeCode = pricePlanMatrixColumn.getAttribute().getCode();
        this.position = pricePlanMatrixColumn.getPosition();
        this.type = pricePlanMatrixColumn.getType();
        this.elValue = pricePlanMatrixColumn.getElValue();
        this.isRange = pricePlanMatrixColumn.getRange();
        this.id = pricePlanMatrixColumn.getId();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPricePlanMatrixCode() {
        return pricePlanMatrixCode;
    }

    public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
        this.pricePlanMatrixCode = pricePlanMatrixCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getOfferTemplateCode() {
        return offerTemplateCode;
    }

    public void setOfferTemplateCode(String offerTemplateCode) {
        this.offerTemplateCode = offerTemplateCode;
    }

    public String getAttributeCode() {
        return attributeCode;
    }

    public void setAttributeCode(String attributeCode) {
        this.attributeCode = attributeCode;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ColumnTypeEnum getType() {
        return type;
    }

    public void setType(ColumnTypeEnum type) {
        this.type = type;
    }

    public String getElValue() {
        return elValue;
    }

    public void setElValue(String elValue) {
        this.elValue = elValue;
    }

    public int getPricePlanMatrixVersion() {
        return pricePlanMatrixVersion;
    }

    public void setPricePlanMatrixVersion(int pricePlanMatrixVersion) {
        this.pricePlanMatrixVersion = pricePlanMatrixVersion;
    }

    public Boolean getRange() {
        return isRange == null ? false : isRange;
    }

    public void setRange(Boolean range) {
        isRange = range;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

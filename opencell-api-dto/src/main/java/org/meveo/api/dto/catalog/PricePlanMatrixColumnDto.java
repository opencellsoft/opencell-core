package org.meveo.api.dto.catalog;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixColumn;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixColumnDto extends BaseEntityDto {

    @NotNull
    private String code;

    @NotNull
    private String pricePlanMatrixCode;

    @NotNull
    private String productCode;

    @NotNull
    private String offerTemplateCode;

    @NotNull
    private String attributeCode;
    @NotNull
    private int position;
    @NotNull
    private ColumnTypeEnum type;

    @NotNull
    private String elValue;

    public PricePlanMatrixColumnDto() {
    }

    public PricePlanMatrixColumnDto(PricePlanMatrixColumn pricePlanMatrixColumn) {
        this.code = pricePlanMatrixColumn.getCode();
        this.pricePlanMatrixCode = pricePlanMatrixColumn.getPricePlanMatrix().getCode();
        this.productCode = pricePlanMatrixColumn.getProduct().getCode();
        this.offerTemplateCode = pricePlanMatrixColumn.getOfferTemplate().getCode();
        this.attributeCode = pricePlanMatrixColumn.getAttribute().getCode();
        this.position = pricePlanMatrixColumn.getPosition();
        this.type = pricePlanMatrixColumn.getType();
        this.elValue = pricePlanMatrixColumn.getElValue();
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
}

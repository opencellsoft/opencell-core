package org.meveo.api.dto;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn.CustomFieldColumnUseEnum;

/**
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomFieldMatrixColumn")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldMatrixColumnDto extends BaseDto {

    private static final long serialVersionUID = -7343379732647377673L;

    @XmlAttribute(required = true)
    private CustomFieldColumnUseEnum columnUse = CustomFieldColumnUseEnum.USE_KEY;

    @XmlAttribute(required = true)
    private int position;

    @XmlAttribute(required = true)
    @Size(max = 20)
    private String code;

    @XmlAttribute(required = true)
    @Size(max = 50)
    private String label;

    @XmlAttribute(required = true)
    private CustomFieldMapKeyEnum keyType;

    public CustomFieldMatrixColumnDto() {

    }

    public CustomFieldMatrixColumnDto(CustomFieldMatrixColumn column) {
        this.columnUse = column.getColumnUse();
        this.position = column.getPosition();
        this.code = column.getCode();
        this.label = column.getLabel();
        this.keyType = column.getKeyType();
    }

    public static CustomFieldMatrixColumn fromDto(CustomFieldMatrixColumnDto dto) {
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setColumnUse(dto.getColumnUse());
        column.setCode(dto.getCode());
        column.setKeyType(dto.getKeyType());
        column.setLabel(dto.getLabel());
        column.setPosition(dto.getPosition());

        return column;
    }

    public CustomFieldColumnUseEnum getColumnUse() {
        return columnUse;
    }

    public void setColumnUse(CustomFieldColumnUseEnum columnUse) {
        this.columnUse = columnUse;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CustomFieldMapKeyEnum getKeyType() {
        return keyType;
    }

    public void setKeyType(CustomFieldMapKeyEnum keyType) {
        this.keyType = keyType;
    }
}
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
 * The Class CustomFieldMatrixColumnDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CustomFieldMatrixColumn")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomFieldMatrixColumnDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7343379732647377673L;

    /** The column use. */
    @XmlAttribute(required = true)
    private CustomFieldColumnUseEnum columnUse = CustomFieldColumnUseEnum.USE_KEY;

    /** The position. */
    @XmlAttribute(required = true)
    private int position;

    /** The code. */
    @XmlAttribute(required = true)
    @Size(max = 20)
    private String code;

    /** The label. */
    @XmlAttribute(required = true)
    @Size(max = 50)
    private String label;

    /** The key type. */
    @XmlAttribute(required = true)
    private CustomFieldMapKeyEnum keyType;

    /**
     * Instantiates a new custom field matrix column dto.
     */
    public CustomFieldMatrixColumnDto() {

    }

    /**
     * Instantiates a new custom field matrix column dto.
     *
     * @param column the column
     */
    public CustomFieldMatrixColumnDto(CustomFieldMatrixColumn column) {
        this.columnUse = column.getColumnUse();
        this.position = column.getPosition();
        this.code = column.getCode();
        this.label = column.getLabel();
        this.keyType = column.getKeyType();
    }

    /**
     * From dto.
     *
     * @param dto the dto
     * @return the custom field matrix column
     */
    public static CustomFieldMatrixColumn fromDto(CustomFieldMatrixColumnDto dto) {
        CustomFieldMatrixColumn column = new CustomFieldMatrixColumn();
        column.setColumnUse(dto.getColumnUse());
        column.setCode(dto.getCode());
        column.setKeyType(dto.getKeyType());
        column.setLabel(dto.getLabel());
        column.setPosition(dto.getPosition());

        return column;
    }

    /**
     * Gets the column use.
     *
     * @return the column use
     */
    public CustomFieldColumnUseEnum getColumnUse() {
        return columnUse;
    }

    /**
     * Sets the column use.
     *
     * @param columnUse the new column use
     */
    public void setColumnUse(CustomFieldColumnUseEnum columnUse) {
        this.columnUse = columnUse;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the key type.
     *
     * @return the key type
     */
    public CustomFieldMapKeyEnum getKeyType() {
        return keyType;
    }

    /**
     * Sets the key type.
     *
     * @param keyType the new key type
     */
    public void setKeyType(CustomFieldMapKeyEnum keyType) {
        this.keyType = keyType;
    }
}